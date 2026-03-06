"""
Medical RAG Decision Tree Generator
====================================
Source  : MedlinePlus (https://medlineplus.gov/) + Mayo Clinic fallback
Search  : Tavily API
RAG     : Hybrid RAG — BM25 (rank_bm25) + semantic embeddings (sentence-transformers)
LLM     : Cerebras API  (llama3.3-70b primary, llama3.1-8b fallback)
Output  : generated_decision_tree.json  (same schema as decision_tree.json)
"""

import json
import os
import re
import sys
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import date

import numpy as np
from openai import OpenAI
from sklearn.metrics.pairwise import cosine_similarity
from tavily import TavilyClient

# ─────────────────────────────  API KEYS  ────────────────────────────────────
from dotenv import load_dotenv
load_dotenv()

TAVILY_API_KEY  = os.getenv("TAVILY_API_KEY")
CEREBRAS_API_KEY = os.getenv("RAG_CEREBRAS_API_KEY")

if not TAVILY_API_KEY:
    raise ValueError("TAVILY_API_KEY is not set in environment")
if not CEREBRAS_API_KEY:
    raise ValueError("RAG_CEREBRAS_API_KEY is not set in environment")

# ─────────────────────  GLOBAL EMBEDDER SINGLETON  ───────────────────────────
# Loaded once per process — all HybridRAG instances share it.
_EMBEDDER = None

def _get_global_embedder():
    global _EMBEDDER
    if _EMBEDDER is None:
        from sentence_transformers import SentenceTransformer
        print("  [Embedder] Loading all-MiniLM-L6-v2 (one-time) …")
        _EMBEDDER = SentenceTransformer("all-MiniLM-L6-v2")
        print("  [Embedder] Ready.")
    return _EMBEDDER

# ─────────────────────  CEREBRAS CLIENT (OpenAI-compat)  ─────────────────────
cerebras = OpenAI(
    api_key=CEREBRAS_API_KEY,
    base_url="https://api.cerebras.ai/v1",
)

# ─────────────────────────────  TAVILY CLIENT  ───────────────────────────────
tavily = TavilyClient(api_key=TAVILY_API_KEY)

# ──────────────────────────────  SYMPTOM EXTRACTOR  ──────────────────────────
def _extract_symptoms_with_llm(raw_input: str) -> list[str]:
    """
    Use Cerebras LLM to:
      • Understand free-form / conversational symptom descriptions
      • Fix spelling mistakes  (e.g. "stomch ake" → "stomach ache")
      • Extract each distinct symptom as a clean, standard medical term
    Returns a list of corrected symptom strings.
    """
    prompt = (
        "You are a medical NLP assistant. "
        "The user has typed the following text describing their symptoms — "
        "it may contain spelling mistakes, slang, or be a full sentence:\n\n"
        f'"{raw_input}"\n\n'
        "Tasks:\n"
        "1. Identify every symptom or medical complaint mentioned.\n"
        "2. Correct any spelling mistakes and normalise to standard medical terms.\n"
        "3. Return ONLY a JSON array of strings — one clean symptom per element. "
        "No explanation, no markdown, no extra text.\n"
        'Example output: ["chest pain", "fever", "headache"]'
    )

    for model in ("llama3.1-8b",):
        try:
            resp = cerebras.chat.completions.create(
                model=model,
                messages=[{"role": "user", "content": prompt}],
                temperature=0,
                max_tokens=256,
            )
            content = resp.choices[0].message.content.strip()
            # Extract the JSON array even if wrapped in markdown fences
            match = re.search(r"\[.*?\]", content, re.DOTALL)
            if match:
                symptoms = json.loads(match.group())
                if isinstance(symptoms, list) and symptoms:
                    return [str(s).strip() for s in symptoms if str(s).strip()]
        except Exception as exc:
            print(f"  [LLM Extract] Warning: {exc}")

    # Fallback: naive comma-split on the original input
    return [s.strip() for s in raw_input.split(",") if s.strip()]


def get_symptoms_from_user() -> list[str]:
    print("\n" + "─" * 60)
    print("  Describe your symptoms in plain English.")
    print("  You can type a sentence, list them, or mix both.")
    print("  Spelling mistakes are handled automatically.")
    print("  Example: \"I have a bad headche and my stomch hurts\"")
    print("─" * 60)
    raw = input("  Your symptoms: ").strip()
    if not raw:
        print("  Nothing entered. Exiting.")
        sys.exit(0)

    print("\n  [NLP] Extracting & correcting symptoms via LLM …")
    symptoms = _extract_symptoms_with_llm(raw)

    if not symptoms:
        print("  Could not identify any symptoms. Exiting.")
        sys.exit(0)

    print(f"  → Identified {len(symptoms)} symptom(s): {', '.join(symptoms)}\n")
    return symptoms

MEDLINE_BASE = "https://medlineplus.gov/"

# ──────────────────────────────────────────────────────────────────────────────
#  TEXT CLEANING  +  SENTENCE-AWARE CHUNKER
# ──────────────────────────────────────────────────────────────────────────────
def _clean_scraped_text(text: str) -> str:
    """Remove nav junk, cookie banners, and very short fragments."""
    text = re.sub(r"\s+", " ", text).strip()
    sentences = re.split(r"(?<=[.!?])\s+", text)
    sentences = [s.strip() for s in sentences if len(s.strip()) >= 45]
    return " ".join(sentences)


def _sentence_chunk(text: str, chunk_size: int = 700,
                    overlap_sentences: int = 2) -> list[str]:
    """Split text into overlapping chunks aligned to sentence boundaries."""
    sentences = re.split(r"(?<=[.!?])\s+", text)
    chunks, current, current_len = [], [], 0
    for sent in sentences:
        if current_len + len(sent) > chunk_size and current:
            chunks.append(" ".join(current))
            current = current[-overlap_sentences:]
            current_len = sum(len(s) for s in current)
        current.append(sent)
        current_len += len(sent)
    if current:
        chunks.append(" ".join(current))
    return [c for c in chunks if c.strip()]


# ══════════════════════════════════════════════════════════════════════════════
#  HYBRID RAG ENGINE  (BM25 keyword  +  semantic embeddings)
# ══════════════════════════════════════════════════════════════════════════════
class HybridRAG:
    """
    Hybrid RAG combining:
      • BM25  (rank_bm25)             – exact / keyword relevance
      • Semantic cosine similarity    – sentence-transformers embeddings
    Final score = semantic_weight * semantic + (1 - semantic_weight) * bm25
    """

    def __init__(self, chunk_size: int = 700, overlap_sentences: int = 2,
                 semantic_weight: float = 0.6):
        self.chunks: list[dict]   = []
        self.chunk_size           = chunk_size
        self.overlap_sentences    = overlap_sentences
        self.semantic_weight      = semantic_weight
        self._bm25                = None
        self._tokenized           = []
        self._embedder            = None
        self._chunk_embeddings    = None

    # ── use the module-level singleton ────────────────────────────────────────
    def _get_embedder(self):
        return _get_global_embedder()

    # ── add & clean document ──────────────────────────────────────────────────
    def add_document(self, text: str, source: str = ""):
        text = _clean_scraped_text(text)
        new_chunks = _sentence_chunk(text, self.chunk_size, self.overlap_sentences)
        for chunk in new_chunks:
            self.chunks.append({"text": chunk, "source": source})
        print(f"    [RAG] Stored {len(self.chunks)} chunks from '{source}'")

    # ── build BM25 + embedding index ──────────────────────────────────────────
    def build_index(self):
        if not self.chunks:
            print("    [RAG] No chunks to index.")
            return
        from rank_bm25 import BM25Okapi
        texts = [c["text"] for c in self.chunks]
        self._tokenized = [t.lower().split() for t in texts]
        self._bm25 = BM25Okapi(self._tokenized)
        embedder = self._get_embedder()
        self._chunk_embeddings = embedder.encode(
            texts, show_progress_bar=False, batch_size=32
        )
        print(f"    [RAG] Hybrid index built — {len(self.chunks)} chunks "
              f"(BM25 + semantic embeddings)")

    # ── hybrid retrieve ───────────────────────────────────────────────────────
    def retrieve(self, query: str, top_k: int = 8) -> list[str]:
        if self._bm25 is None or not self.chunks:
            return []
        # BM25 scores (normalised 0-1)
        bm25_raw   = np.array(self._bm25.get_scores(query.lower().split()), dtype=float)
        bm25_max   = bm25_raw.max()
        bm25_scores = bm25_raw / bm25_max if bm25_max > 0 else bm25_raw
        # Semantic scores
        embedder   = self._get_embedder()
        q_emb      = embedder.encode([query], show_progress_bar=False)
        sem_scores = cosine_similarity(q_emb, self._chunk_embeddings)[0]
        # Weighted hybrid
        combined = self.semantic_weight * sem_scores + \
                   (1.0 - self.semantic_weight) * bm25_scores
        top_i = np.argsort(combined)[::-1][:top_k]
        return [self.chunks[i]["text"] for i in top_i if combined[i] > 0.05]


# ══════════════════════════════════════════════════════════════════════════════
#  STEP 1 – FETCH FROM MEDLINEPLUS VIA TAVILY
# ══════════════════════════════════════════════════════════════════════════════
def fetch_medlineplus(symptom: str) -> str:
    """
    Query Tavily with site:medlineplus.gov to get authoritative medical
    content about the symptom.  Returns combined raw text.
    """
    queries = [
        f"site:medlineplus.gov {symptom} symptoms causes",
        f"site:medlineplus.gov {symptom} diagnosis treatment when to see doctor",
        f"site:medlineplus.gov {symptom} emergency warning signs",
    ]

    combined = f"=== MEDLINEPLUS INFORMATION: {symptom.upper()} ===\n\n"
    seen_urls: set[str] = set()

    # ── run all 3 MedlinePlus queries IN PARALLEL ─────────────────────────────
    def _tavily_search(q):
        return tavily.search(
            query=q,
            search_depth="advanced",
            max_results=4,
            include_raw_content=True,
            include_answer=True,
        )

    with ThreadPoolExecutor(max_workers=3) as pool:
        futures = {pool.submit(_tavily_search, q): q for q in queries}
        for future in as_completed(futures):
            try:
                result = future.result()
                if result.get("answer"):
                    combined += f"[Summary] {result['answer']}\n\n"
                for r in result.get("results", []):
                    url = r.get("url", "")
                    if url in seen_urls:
                        continue
                    seen_urls.add(url)
                    content = r.get("raw_content") or r.get("content") or ""
                    if content.strip():
                        combined += f"[Source: {url}]\n{content.strip()}\n\n"
            except Exception as exc:
                print(f"    [Tavily] Warning – query failed: {exc}")

    print(f"    [Tavily] Fetched {len(combined)} chars from {len(seen_urls)} URLs")

    # ── Fallback: if MedlinePlus content is sparse, also query Mayo Clinic ────
    if len(combined) < 4000:
        print(f"    [Tavily] Content sparse ({len(combined)} chars) "
              f"— fetching Mayo Clinic fallback …")
        fallback_queries = [
            f"site:mayoclinic.org {symptom} symptoms causes diagnosis",
            f"site:mayoclinic.org {symptom} treatment when to see doctor emergency",
        ]
        # ── fallback queries also run in parallel ─────────────────────────────
        def _tavily_fallback(q):
            return tavily.search(
                query=q,
                search_depth="advanced",
                max_results=3,
                include_raw_content=True,
                include_answer=True,
            )
        with ThreadPoolExecutor(max_workers=2) as pool:
            futures = {pool.submit(_tavily_fallback, q): q for q in fallback_queries}
            for future in as_completed(futures):
                try:
                    result = future.result()
                    if result.get("answer"):
                        combined += f"[Summary] {result['answer']}\n\n"
                    for r in result.get("results", []):
                        url = r.get("url", "")
                        if url in seen_urls:
                            continue
                        seen_urls.add(url)
                        content = r.get("raw_content") or r.get("content") or ""
                        if content.strip():
                            combined += f"[Source: {url}]\n{content.strip()}\n\n"
                except Exception as exc:
                    print(f"    [Tavily] Fallback query failed: {exc}")
        print(f"    [Tavily] After fallback: {len(combined)} chars total")

    return combined


# ══════════════════════════════════════════════════════════════════════════════
#  STEP 2 – CEREBRAS LLM  →  structured decision-tree JSON
# ══════════════════════════════════════════════════════════════════════════════
SYSTEM_PROMPT = """You are a clinical decision-support AI that produces a single valid JSON object.

YOUR ONLY DATA SOURCE IS THE RAG CONTEXT PROVIDED BY THE USER.
You must NOT use your training memory, prior knowledge, or generic medical templates for any
option values. Every question and every answer option you write must be directly traceable to
a fact stated in the retrieved RAG context.

This is especially mandatory for these four fields:
  - pain_character   (how the symptom physically feels or presents)
  - associated_symptoms  (what co-occurs with this specific symptom)
  - aggravating_factors  (what specifically worsens this symptom)
  - relieving_factors    (what specifically helps this symptom)

Before writing any option for those four fields, locate the supporting sentence in the RAG
context. If you cannot find it, do not write it.

Output ONLY the raw JSON object — no markdown fences, no explanation, nothing else."""


def build_llm_prompt(symptom: str, rag_chunks: list[str]) -> str:
    rag_text = "\n\n---\n\n".join(rag_chunks) if rag_chunks else "(no RAG context retrieved)"

    # Truncate so we stay within context window
    if len(rag_text) > 8000:
        rag_text = rag_text[:8000] + "\n... [truncated]"

    return f"""=== RETRIEVED MEDICAL CONTEXT (MedlinePlus via Tavily) ===
{rag_text}
=== END OF RAG CONTEXT ===

─────────────────────────────────────────────────────────────
STEP 1 — READ BEFORE YOU WRITE (internal reasoning, not output)

Before generating the JSON, scan the RAG context above and answer these
four questions in your head:

  Q-A: What physical qualities or sensations does the RAG use to describe
       how "{symptom}" feels? (these become pain_character options)

  Q-B: Which other symptoms or conditions does the RAG say commonly occur
       alongside "{symptom}"? (these become associated_symptoms options)

  Q-C: What does the RAG say specifically makes "{symptom}" worse — diet,
       activity, environment, conditions? (these become aggravating_factors)

  Q-D: What does the RAG say specifically relieves or treats "{symptom}" —
       medications, behaviours, positions, interventions? (these become
       relieving_factors options)

If the RAG does not clearly answer one of these questions, search harder —
look for synonyms, related conditions, treatment sections. Only after you
have RAG-sourced answers for all four should you proceed to STEP 2.

─────────────────────────────────────────────────────────────
STEP 2 — GENERATE JSON

Now produce the JSON object for symptom: "{symptom}"

Use this EXACT schema:

{{
  "symptom_id": "<snake_case_id>",
  "label": "<Human Readable Label>",
  "keywords": ["keyword1", "keyword2", "...(12-16 terms a patient might type)"],
  "default_urgency": "<red_emergency | yellow_doctor_visit | green_home_care>",

  "triage_rationale": {{
    "why_assess_carefully": ["reason1", "reason2", "reason3", "reason4"],
    "age_factor": "<age-specific consideration drawn from RAG>",
    "sex_specific_notes": "<sex-specific note drawn from RAG>",
    "pregnancy_note": "<pregnancy-specific note drawn from RAG>"
  }},

  "immediate_red_flags": [
    "red flag 1", "red flag 2", "red flag 3", "red flag 4", "red flag 5"
  ],

  "followup_questions": {{
    "onset_type": {{
      "question": "How did the {symptom} start?",
      "type": "single_choice",
      "options": ["Sudden", "Gradual over minutes", "Gradual over hours or days", "Chronic or recurring", "Not sure"]
    }},
    "severity": {{
      "question": "How severe is the {symptom} right now?",
      "type": "single_choice",
      "options": ["Mild", "Moderate", "Severe", "Very severe or unbearable"]
    }},
    "duration": {{
      "question": "How long have you had this {symptom}?",
      "type": "single_choice",
      "options": ["Less than 24 hours", "1-3 days", "4-7 days", "More than 1 week", "Chronic (months+)"]
    }},
    "pain_character": {{
      "question": "<A precise question about how {symptom} physically feels or presents — formed from what Q-A found in the RAG>",
      "type": "single_choice",
      "options": [
        "<Physical descriptor 1 the RAG uses for {symptom}>",
        "<Physical descriptor 2 from RAG>",
        "<Physical descriptor 3 from RAG>",
        "<Physical descriptor 4 from RAG>",
        "Other"
      ]
    }},
    "associated_symptoms": {{
      "question": "<Ask which other symptoms commonly occur with {symptom} — name the actual conditions from Q-B>",
      "type": "multi_choice",
      "options": [
        "<Co-occurring symptom 1 that Q-B found in the RAG for {symptom}>",
        "<Co-occurring symptom 2 from RAG>",
        "<Co-occurring symptom 3 from RAG>",
        "<Co-occurring symptom 4 from RAG>",
        "<Co-occurring symptom 5 from RAG>",
        "None"
      ]
    }},
    "aggravating_factors": {{
      "question": "<Ask what makes {symptom} worse — use the specific factors Q-C found in the RAG>",
      "type": "multi_choice",
      "options": [
        "<Aggravating factor 1 that Q-C found in RAG for {symptom}>",
        "<Aggravating factor 2 from RAG>",
        "<Aggravating factor 3 from RAG>",
        "<Aggravating factor 4 from RAG>",
        "Nothing specific"
      ]
    }},
    "relieving_factors": {{
      "question": "<Ask what makes {symptom} better — use the specific measures Q-D found in the RAG>",
      "type": "multi_choice",
      "options": [
        "<Relief measure 1 that Q-D found in RAG for {symptom}>",
        "<Relief measure 2 from RAG>",
        "<Relief measure 3 from RAG>",
        "<Relief measure 4 from RAG>",
        "Nothing helps"
      ]
    }}
  }},

  "urgency_decision_logic": {{
    "red_emergency": ["condition1 from RAG", "condition2", "condition3"],
    "yellow_doctor_visit": ["condition1 from RAG", "condition2", "condition3"],
    "green_home_care": ["condition1 from RAG", "condition2"]
  }},

  "llm_analysis_tips": ["tip1", "tip2", "tip3", "tip4"],

  "advice": {{
    "action": "<Primary recommended action from RAG>",
    "emergency_if": ["condition1", "condition2", "condition3"],
    "doctor_visit_if": ["condition1", "condition2"],
    "home_care_if": ["condition1", "condition2"],
    "reason": "<Short reason>",
    "do_not_delay": <true | false>
  }}
}}

─────────────────────────────────────────────────────────────
RULES:

1. RAG IS LAW — every option in pain_character, associated_symptoms,
   aggravating_factors, and relieving_factors must come from the RAG context
   above. Not from your training data. Not from memory. From the RAG text.

2. onset_type / severity / duration — copy the option lists EXACTLY as shown
   in the schema. Do not change a single word.

3. Angle-bracket placeholders like "<Physical descriptor 1...>" are structural
   instructions — REPLACE them with actual content from the RAG. Do not copy
   the angle-bracket text itself into the output.

4. SPECIFICITY CHECK — before writing each option, ask: "Could this option
   appear word-for-word in a completely different symptom's JSON?" If yes, it
   is too generic. Find something more specific in the RAG.

5. Return ONLY the raw JSON object — NO markdown fences, NO explanation text,
   NO text before the opening brace or after the closing brace.

6. The JSON must be 100% valid and parseable.

7. All option values must be human-readable display strings:
   "1-3 days" not "1_3_days", spaces not underscores, natural phrases a
   patient can read aloud.
"""


def _snake_to_display(s: str) -> str:
    """Convert a snake_case option string to a human-readable display string.

    Examples:
        "1_3_days"              → "1-3 days"
        "less_than_24_hours"    → "Less than 24 hours"
        "nothing_specific"      → "Nothing specific"
        "Gradual over minutes"  → unchanged (already display format)
    """
    if "_" not in s:
        return s  # already fine

    import re as _re

    # Handle numeric ranges like "1_3_days", "4_7_days"
    def replace_numeric_range(m):
        return f"{m.group(1)}-{m.group(2)} "

    result = _re.sub(r"(\d+)_(\d+)_", replace_numeric_range, s)
    result = result.replace("_", " ")
    # Capitalise first letter only (not every word — keeps "over" lowercase)
    result = result[0].upper() + result[1:] if result else result
    return result.strip()


def _sanitize_options(node: object) -> object:
    """Recursively walk a parsed dict/list and fix any snake_case option strings."""
    if isinstance(node, dict):
        for key, value in node.items():
            if key == "options" and isinstance(value, list):
                node[key] = [_snake_to_display(v) if isinstance(v, str) else v for v in value]
            else:
                _sanitize_options(value)
    elif isinstance(node, list):
        for item in node:
            _sanitize_options(item)
    return node


def call_cerebras(symptom: str, rag_chunks: list[str]) -> dict:
    """Call Cerebras LLM and parse the returned JSON."""
    prompt  = build_llm_prompt(symptom, rag_chunks)

    models_to_try = [
        "llama3.3-70b",   # best quality — try first
        "llama3.1-8b",    # fallback if 70b unavailable
    ]

    raw_response = ""
    for model in models_to_try:
        try:
            print(f"    [Cerebras] Trying model: {model}")
            resp = cerebras.chat.completions.create(
                model=model,
                messages=[
                    {"role": "system", "content": SYSTEM_PROMPT},
                    {"role": "user",   "content": prompt},
                ],
                temperature=0.15,   # lower = more deterministic, better JSON
                max_tokens=6000,    # give 70b more room for detailed output
            )
            raw_response = resp.choices[0].message.content.strip()
            print(f"    [Cerebras] Got {len(raw_response)} chars from model '{model}'")
            break
        except Exception as exc:
            print(f"    [Cerebras] Model '{model}' failed: {exc}")
            continue

    if not raw_response:
        raise RuntimeError("All Cerebras models failed.")

    # ── strip markdown fences if the model added them ─────────────────────────
    cleaned = raw_response
    if "```json" in cleaned:
        cleaned = cleaned.split("```json", 1)[1].split("```", 1)[0].strip()
    elif "```" in cleaned:
        cleaned = cleaned.split("```", 1)[1].split("```", 1)[0].strip()

    # ── find the outer JSON object ────────────────────────────────────────────
    brace_start = cleaned.find("{")
    brace_end   = cleaned.rfind("}")
    if brace_start != -1 and brace_end != -1:
        cleaned = cleaned[brace_start : brace_end + 1]

    parsed = json.loads(cleaned)
    _sanitize_options(parsed)
    return parsed


# ══════════════════════════════════════════════════════════════════════════════
#  MAIN PIPELINE
# ══════════════════════════════════════════════════════════════════════════════
def run_pipeline(symptoms: list[str]) -> dict:
    final_output = {
        "symptom_decision_tree": {
            "meta": {
                "version": "2.0",
                "purpose": "Symptom-specific triage + deep-dive questionnaire",
                "source": "MedlinePlus via Tavily RAG + Cerebras LLM",
                "generated_date": str(date.today()),
                "medlineplus_url": MEDLINE_BASE,
                "assumes_prior_data": [
                    "age",
                    "sex_at_birth",
                    "pregnancy_status",
                    "chief_complaint",
                    "onset",
                    "severity",
                    "associated_symptoms",
                    "past_medical_conditions",
                    "current_medications",
                    "allergies",
                    "recent_events",
                ],
            },
            "symptoms": [],
        }
    }

    errors = []

    for idx, symptom in enumerate(symptoms, 1):
        sep = "=" * 60
        print(f"\n{sep}")
        print(f"  [{idx}/{len(symptoms)}]  PROCESSING: {symptom.upper()}")
        print(sep)

        try:
            # ── 1. Fetch from MedlinePlus ──────────────────────────────────
            print("  Step 1 → Fetching from MedlinePlus via Tavily …")
            raw_text = fetch_medlineplus(symptom)

            # ── 2. Build Hybrid RAG ────────────────────────────────────────
            print("  Step 2 → Chunking & indexing in Hybrid RAG …")
            rag = HybridRAG(chunk_size=700, overlap_sentences=2, semantic_weight=0.6)
            rag.add_document(raw_text, source=f"medlineplus:{symptom}")
            rag.build_index()

            # ── 3. Retrieve via 3 targeted sub-queries (deduplicated) ──────
            print("  Step 3 → Retrieving relevant chunks (multi-query) …")
            sub_queries = [
                f"{symptom} emergency red flags warning signs when to call ambulance",
                f"{symptom} causes risk factors diagnosis tests",
                f"{symptom} treatment home care medications when to see doctor",
            ]
            seen_texts: set[str] = set()
            chunks: list[str] = []
            for sq in sub_queries:
                for chunk in rag.retrieve(sq, top_k=5):
                    if chunk not in seen_texts:
                        seen_texts.add(chunk)
                        chunks.append(chunk)
            chunks = chunks[:12]   # cap to avoid overflowing LLM context window
            print(f"    [RAG] Retrieved {len(chunks)} deduplicated chunks for LLM context")

            # ── 4. Cerebras LLM → structured JSON ─────────────────────────
            print("  Step 4 → Sending to Cerebras LLM …")
            symptom_node = call_cerebras(symptom, chunks)

            final_output["symptom_decision_tree"]["symptoms"].append(symptom_node)
            print(f"  ✓  Successfully generated: {symptom_node.get('label', symptom)}")

        except Exception as exc:
            print(f"  ✗  ERROR processing '{symptom}': {exc}")
            errors.append({"symptom": symptom, "error": str(exc)})

    if errors:
        final_output["symptom_decision_tree"]["errors"] = errors

    return final_output


# ──────────────────────────────────────────────────────────────────────────────
#  CACHE HELPERS
# ──────────────────────────────────────────────────────────────────────────────
def _normalize(text: str) -> str:
    """
    Strip ALL non-alphanumeric characters and lowercase.
    'ear pain', 'earpain', 'ear_pain', 'EarPain', 'ear-pain' all → 'earpain'.
    Used for fuzzy cache matching regardless of spacing/formatting.
    """
    return re.sub(r"[^a-z0-9]", "", text.lower())


def _load_cached_ids(output_path: str) -> set[str]:
    """Return set of NORMALIZED symptom_ids already saved in the output file."""
    if not os.path.exists(output_path):
        return set()
    try:
        with open(output_path, "r", encoding="utf-8") as fh:
            existing = json.load(fh)
        return {
            _normalize(s["symptom_id"])
            for s in existing["symptom_decision_tree"]["symptoms"]
        }
    except Exception:
        return set()


# ══════════════════════════════════════════════════════════════════════════════
#  ENTRY POINT
# ══════════════════════════════════════════════════════════════════════════════
if __name__ == "__main__":
    print("\n" + "█" * 60)
    print("  MEDICAL RAG DECISION TREE GENERATOR")
    print("  Source   : MedlinePlus (https://medlineplus.gov/)")
    print("  Search   : Tavily API (advanced web search)")
    print("  RAG      : TF-IDF in-memory vector store")
    print("  LLM      : Cerebras API")
    print("█" * 60)

    symptoms = get_symptoms_from_user()

    output_path = r"c:\Users\DELL\Desktop\RAG\generated_decision_tree.json"

    # ── Skip symptoms that are already cached ─────────────────────────────────
    cached_ids = _load_cached_ids(output_path)   # normalized (no separators)
    new_symptoms, skipped_symptoms = [], []
    for s in symptoms:
        if _normalize(s) in cached_ids:
            skipped_symptoms.append(s)
        else:
            new_symptoms.append(s)

    if skipped_symptoms:
        print("\n" + "─" * 60)
        print("  [Cache] Already processed — skipping RAG for:")
        for s in skipped_symptoms:
            print(f"    • {s}")
        print("─" * 60)

    if not new_symptoms:
        print("\n  All entered symptoms are already cached. Nothing to do.\n")
        sys.exit(0)

    result = run_pipeline(new_symptoms)

    # ── Merge new symptoms into existing file (avoid overwriting old data) ────
    if os.path.exists(output_path):
        with open(output_path, "r", encoding="utf-8") as fh:
            existing = json.load(fh)
        existing_ids = {
            s["symptom_id"]
            for s in existing["symptom_decision_tree"]["symptoms"]
        }
        new_nodes = result["symptom_decision_tree"]["symptoms"]
        added, skipped = 0, 0
        for node in new_nodes:
            if node["symptom_id"] not in existing_ids:
                existing["symptom_decision_tree"]["symptoms"].append(node)
                added += 1
            else:
                # overwrite with freshly generated version
                existing["symptom_decision_tree"]["symptoms"] = [
                    node if s["symptom_id"] == node["symptom_id"] else s
                    for s in existing["symptom_decision_tree"]["symptoms"]
                ]
                skipped += 1
        # update meta date
        existing["symptom_decision_tree"]["meta"]["generated_date"] = str(date.today())
        result = existing
        print(f"  [Merge] Added {added} new, updated {skipped} existing symptom(s)")
    else:
        print("  [Merge] Creating new file")
    with open(output_path, "w", encoding="utf-8") as fh:
        json.dump(result, fh, indent=2, ensure_ascii=False)

    print("\n" + "█" * 60)
    n_ok = len(result["symptom_decision_tree"]["symptoms"])
    n_er = len(result["symptom_decision_tree"].get("errors", []))
    print(f"  ✓  Pipeline complete")
    print(f"  ✓  Symptoms generated : {n_ok}")
    if n_er:
        print(f"  ✗  Errors            : {n_er}")
    print(f"  ✓  Output saved to   : {output_path}")
    print("█" * 60 + "\n")

    sys.exit(0 if n_er == 0 else 1)
