"""
Vision-Language Client  —  Medical Image → Text Description

Two backends (auto-selected):

  ┌─────────────────────────────────────────────────────────────────┐
  │  LOCAL / CPU  →  Microsoft Florence-2-base  (~0.5 GB, fast)    │
  │  AWS / GPU    →  LLaVA 1.5-7B  (~7 GB VRAM with 4-bit quant)  │
  └─────────────────────────────────────────────────────────────────┘

Set VISION_BACKEND=llava or VISION_BACKEND=florence in .env to override.
Default: "florence" when no CUDA, "llava" when CUDA is available.

The model is downloaded from Hugging Face on first use and cached locally.
DO NOT commit model weights to git.
"""

import torch
from PIL import Image
from typing import Optional, Dict, Any
import io
import os
from dotenv import load_dotenv

load_dotenv()

# ─────────────────────────────
# Configuration
# ─────────────────────────────
_HAS_CUDA = torch.cuda.is_available()

# Choose backend: "florence" (CPU-friendly) or "llava" (GPU, production)
VISION_BACKEND = os.getenv("VISION_BACKEND", "llava" if _HAS_CUDA else "florence")

# Florence-2 settings
FLORENCE_MODEL_ID = os.getenv("FLORENCE_MODEL_ID", "microsoft/Florence-2-base")

# LLaVA settings
LLAVA_MODEL_ID = os.getenv("LLAVA_MODEL_ID", "llava-hf/llava-1.5-7b-hf")
LLAVA_LOAD_IN_4BIT = os.getenv("LLAVA_LOAD_IN_4BIT", str(_HAS_CUDA)).lower() == "true"

# Shared settings
VISION_MAX_NEW_TOKENS = int(os.getenv("VISION_MAX_NEW_TOKENS", "512"))
VISION_CACHE_DIR = os.getenv("HF_CACHE_DIR", None)
VISION_DEVICE = os.getenv("VISION_DEVICE", "auto" if _HAS_CUDA else "cpu")

# Medical prompt for LLaVA
LLAVA_MEDICAL_PROMPT = (
    "USER: <image>\n"
    "You are a medical image analysis assistant. "
    "Describe what you see in this image in clinical terms. "
    "Include: affected area, color, texture, pattern, estimated severity "
    "(mild / moderate / severe), and any visible features such as swelling, "
    "redness, lesions, rashes, wounds, or discoloration. "
    "Keep the description concise (3-5 sentences).\n"
    "ASSISTANT:"
)

print(f"[VISION] Backend: {VISION_BACKEND} | CUDA: {_HAS_CUDA}")


# ═════════════════════════════════════════════════════════════════════
#  Florence-2 Backend  (CPU-friendly, ~500 MB)
# ═════════════════════════════════════════════════════════════════════

class FlorenceClient:
    """
    Uses Microsoft Florence-2-base for image captioning.
    Fast on CPU (~2-5 sec), tiny (~500 MB RAM).
    """

    def __init__(self):
        self.model = None
        self.processor = None
        self._is_loaded = False
        self.model_id = FLORENCE_MODEL_ID

    def load_model(self):
        if self._is_loaded:
            print("[FLORENCE] Model already loaded")
            return

        from transformers import AutoProcessor, AutoModelForCausalLM

        print(f"[FLORENCE] Loading model: {self.model_id}")

        try:
            self.processor = AutoProcessor.from_pretrained(
                self.model_id,
                cache_dir=VISION_CACHE_DIR,
                trust_remote_code=True,
            )
            self.model = AutoModelForCausalLM.from_pretrained(
                self.model_id,
                cache_dir=VISION_CACHE_DIR,
                torch_dtype=torch.float32,
                trust_remote_code=True,
            )
            self._is_loaded = True
            print("[FLORENCE] ✅ Model loaded successfully (CPU)")
        except Exception as e:
            print(f"[FLORENCE] ❌ Failed to load model: {e}")
            raise RuntimeError(f"Florence model loading failed: {e}")

    def describe_image(self, image_bytes: bytes, **kwargs) -> str:
        if not self._is_loaded:
            self.load_model()

        pil_image = Image.open(io.BytesIO(image_bytes)).convert("RGB")

        # Florence-2 uses task prompts — <MORE_DETAILED_CAPTION> gives rich descriptions
        task_prompt = "<MORE_DETAILED_CAPTION>"

        inputs = self.processor(
            text=task_prompt,
            images=pil_image,
            return_tensors="pt",
        )

        with torch.no_grad():
            output_ids = self.model.generate(
                input_ids=inputs["input_ids"],
                pixel_values=inputs["pixel_values"],
                max_new_tokens=VISION_MAX_NEW_TOKENS,
                do_sample=False,
            )

        generated_text = self.processor.batch_decode(
            output_ids, skip_special_tokens=False
        )[0]

        # Post-process Florence output
        parsed = self.processor.post_process_generation(
            generated_text,
            task=task_prompt,
            image_size=(pil_image.width, pil_image.height),
        )

        description = parsed.get(task_prompt, generated_text).strip()
        print(f"[FLORENCE] Generated description ({len(description)} chars)")
        return description

    def is_loaded(self) -> bool:
        return self._is_loaded

    def unload_model(self):
        if self._is_loaded:
            del self.model
            del self.processor
            self.model = None
            self.processor = None
            self._is_loaded = False
            print("[FLORENCE] Model unloaded")


# ═════════════════════════════════════════════════════════════════════
#  LLaVA Backend  (GPU / production, ~7 GB VRAM with 4-bit)
# ═════════════════════════════════════════════════════════════════════

class LlavaClient:
    """
    Uses LLaVA 1.5-7B for medical image description.
    Needs NVIDIA GPU with ≥8 GB VRAM (4-bit) or ≥16 GB (fp16).
    """

    def __init__(self):
        self.model = None
        self.processor = None
        self._is_loaded = False
        self.model_id = LLAVA_MODEL_ID
        self.device = VISION_DEVICE
        self.load_in_4bit = LLAVA_LOAD_IN_4BIT

    def load_model(self):
        if self._is_loaded:
            print("[LLAVA] Model already loaded")
            return

        from transformers import AutoProcessor, LlavaForConditionalGeneration

        print(f"[LLAVA] Loading model: {self.model_id}")
        print(f"[LLAVA] 4-bit quantisation: {self.load_in_4bit}")

        try:
            self.processor = AutoProcessor.from_pretrained(
                self.model_id,
                cache_dir=VISION_CACHE_DIR,
            )

            model_kwargs: Dict[str, Any] = {"cache_dir": VISION_CACHE_DIR}

            if self.load_in_4bit and _HAS_CUDA:
                from transformers import BitsAndBytesConfig
                model_kwargs["quantization_config"] = BitsAndBytesConfig(
                    load_in_4bit=True,
                    bnb_4bit_compute_dtype=torch.float16,
                    bnb_4bit_use_double_quant=True,
                    bnb_4bit_quant_type="nf4",
                )
                model_kwargs["device_map"] = "auto"
                model_kwargs["torch_dtype"] = torch.float16
            elif _HAS_CUDA:
                model_kwargs["device_map"] = "auto"
                model_kwargs["torch_dtype"] = torch.float16
            else:
                model_kwargs["torch_dtype"] = torch.float32

            self.model = LlavaForConditionalGeneration.from_pretrained(
                self.model_id, **model_kwargs
            )
            self._is_loaded = True
            print("[LLAVA] ✅ Model loaded successfully")
        except Exception as e:
            print(f"[LLAVA] ❌ Failed to load model: {e}")
            raise RuntimeError(f"LLaVA model loading failed: {e}")

    def describe_image(
        self,
        image_bytes: bytes,
        prompt: Optional[str] = None,
        max_new_tokens: Optional[int] = None,
    ) -> str:
        if not self._is_loaded:
            self.load_model()

        prompt = prompt or LLAVA_MEDICAL_PROMPT
        max_new_tokens = max_new_tokens or VISION_MAX_NEW_TOKENS

        pil_image = Image.open(io.BytesIO(image_bytes)).convert("RGB")

        inputs = self.processor(
            text=prompt, images=pil_image, return_tensors="pt"
        )
        inputs = {k: v.to(self.model.device) for k, v in inputs.items()}

        with torch.no_grad():
            output_ids = self.model.generate(
                **inputs, max_new_tokens=max_new_tokens, do_sample=False
            )

        generated_text = self.processor.decode(
            output_ids[0], skip_special_tokens=True
        )
        if "ASSISTANT:" in generated_text:
            generated_text = generated_text.split("ASSISTANT:")[-1].strip()

        print(f"[LLAVA] Generated description ({len(generated_text)} chars)")
        return generated_text

    def is_loaded(self) -> bool:
        return self._is_loaded

    def unload_model(self):
        if self._is_loaded:
            del self.model
            del self.processor
            self.model = None
            self.processor = None
            self._is_loaded = False
            if _HAS_CUDA:
                torch.cuda.empty_cache()
            print("[LLAVA] Model unloaded")


# ═════════════════════════════════════════════════════════════════════
#  Global singleton — auto-selects backend
# ═════════════════════════════════════════════════════════════════════

if VISION_BACKEND == "llava":
    llava_client = LlavaClient()
    print(f"[VISION] Using LLaVA backend ({LLAVA_MODEL_ID})")
else:
    llava_client = FlorenceClient()
    print(f"[VISION] Using Florence backend ({FLORENCE_MODEL_ID})")
