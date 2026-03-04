"""
gemini_vision.py
================
Medical image analysis using the Google Gemini API.

Flow:
  1. Receive raw image bytes from the assessment endpoint.
  2. Fetch prior assessment answers, user profile, and medical history from DB.
  3. Send image + context to Gemini 1.5 Flash for medical analysis.
  4. Persist the resulting analysis text to assessment_sessions.vision_analysis.
  5. The app always receives "image received" — the analysis is stored silently.
"""

import base64
import asyncio
from typing import Optional

import google.generativeai as genai

# ─────────────────────────────
# Configuration
# ─────────────────────────────
GEMINI_API_KEY = "AIzaSyAhDPQJzwlEr_uxDkrz1rlxejEQB-PzsCU"
GEMINI_MODEL   = "gemini-1.5-flash"

genai.configure(api_key=GEMINI_API_KEY)


def _build_prompt(
    prior_answers: list,
    user_profile: dict,
    medical_data: dict,
    chief_complaint: str,
) -> str:
    """Build a rich contextual prompt for Gemini image analysis."""

    lines = [
        "You are an expert medical AI assistant. A patient has shared a medical image "
        "as part of their symptom assessment. Analyse the image thoroughly and correlate "
        "your findings with the clinical context provided below.",
        "",
        "=== CHIEF COMPLAINT ===",
        chief_complaint or "Not specified",
        "",
    ]

    # Prior assessment Q&A
    if prior_answers:
        lines.append("=== PRIOR ASSESSMENT ANSWERS ===")
        for entry in prior_answers:
            q  = entry.get("question_text", entry.get("question", ""))
            a  = entry.get("answer_json", entry.get("answer", ""))
            if isinstance(a, dict):
                a = a.get("value") or a.get("selected_option_label") or str(a)
            lines.append(f"Q: {q}")
            lines.append(f"A: {a}")
        lines.append("")

    # User profile
    if user_profile:
        lines.append("=== USER PROFILE ===")
        for k, v in user_profile.items():
            if v:
                lines.append(f"{k}: {v}")
        lines.append("")

    # Medical history
    if medical_data:
        lines.append("=== MEDICAL HISTORY ===")
        for k, v in medical_data.items():
            if v:
                lines.append(f"{k}: {v}")
        lines.append("")

    lines += [
        "=== TASK ===",
        "Provide a structured medical image analysis covering:",
        "1. What is visible in the image (objective description).",
        "2. Potential medical significance of the findings.",
        "3. Correlation with the reported chief complaint and symptom history.",
        "4. Any visible red flags or warning signs.",
        "5. Recommended next steps based solely on what is visible.",
        "",
        "Keep the response concise, clinical, and patient-friendly. "
        "Do NOT make a definitive diagnosis. Use terms like 'may suggest', "
        "'consistent with', 'warrants evaluation for'.",
    ]

    return "\n".join(lines)


async def analyze_image_with_gemini(
    image_bytes: bytes,
    image_content_type: str,
    prior_answers: list,
    user_profile: dict,
    medical_data: dict,
    chief_complaint: str,
) -> str:
    """
    Send image + clinical context to Gemini and return the analysis text.

    Args:
        image_bytes:        Raw image bytes from the uploaded file.
        image_content_type: MIME type (e.g. "image/jpeg", "image/png").
        prior_answers:      List of {question_text, answer_json} dicts from assessment_db.
        user_profile:       Dict of user profile fields (age, gender, etc.).
        medical_data:       Dict of user medical history fields.
        chief_complaint:    Free-text chief complaint entered by the patient.

    Returns:
        Gemini's analysis as a plain-text string, or an error message.
    """
    try:
        model  = genai.GenerativeModel(GEMINI_MODEL)
        prompt = _build_prompt(prior_answers, user_profile, medical_data, chief_complaint)

        image_part = {
            "mime_type": image_content_type or "image/jpeg",
            "data": image_bytes,
        }

        # Run the blocking Gemini call in a thread pool so the async loop isn't blocked
        loop     = asyncio.get_event_loop()
        response = await loop.run_in_executor(
            None,
            lambda: model.generate_content([image_part, prompt])
        )

        analysis = response.text.strip()
        print(f"[GEMINI VISION] Analysis complete ({len(analysis)} chars)")
        return analysis

    except Exception as exc:
        error_msg = f"[GEMINI VISION] Analysis failed: {exc}"
        print(error_msg)
        return f"Image analysis unavailable: {exc}"
