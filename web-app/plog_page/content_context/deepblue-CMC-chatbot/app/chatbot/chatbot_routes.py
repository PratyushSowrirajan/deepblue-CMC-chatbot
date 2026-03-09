"""
Chatbot API Routes
FastAPI endpoints for the chatbot feature

Architecture:
- LLM is stateless. Context must be injected every call.
- Profile data + reports are stored in PostgreSQL at /chat/start.
- Every /chat/message loads them from DB and builds the full prompt.
- System prompt (Remy identity + rules) is always server-side.
- /chat/end hard-deletes the session from Postgres.
"""

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Any, Optional
from app.chatbot.chatbot_client import chatbot_client
from app.chatbot.chatbot_config import CHATBOT_SYSTEM_PROMPT
from app.chatbot.chatbot_db import create_chat_session, get_chat_session, delete_chat_session


# Create router
router = APIRouter(prefix="/chat", tags=["Chat"])

# Fallback message if LLM fails
FALLBACK_MESSAGE = "I'm sorry, something went wrong. Please try again."


# ─────────────────────────────
# Request/Response Models
# ─────────────────────────────

class ProfileEntry(BaseModel):
    """Single profile Q&A entry"""
    question: str
    answer: str


class StartChatRequest(BaseModel):
    """Request for start chat endpoint"""
    profile_data: List[ProfileEntry]
    reports: List[Dict[str, Any]]


class StartChatResponse(BaseModel):
    """Response for start chat endpoint"""
    session_id: str
    message: str
    is_first: bool = True


class ChatMessage(BaseModel):
    """Single chat message"""
    role: str  # "user" or "assistant"
    content: str


class ContinueChatRequest(BaseModel):
    """Request for continue chat endpoint"""
    session_id: str
    history: List[ChatMessage]


class ContinueChatResponse(BaseModel):
    """Response for continue chat endpoint"""
    message: str


class EndChatRequest(BaseModel):
    """Request for end chat endpoint"""
    session_id: str


class EndChatResponse(BaseModel):
    """Response for end chat endpoint"""
    status: str = "ended"


# ─────────────────────────────
# Prompt Construction Helpers
# ─────────────────────────────

def extract_patient_name(profile_data: list) -> str:
    """Extract patient name from profile Q&A list."""
    for entry in profile_data:
        q = entry.get("question", "").lower()
        if "name" in q:
            return entry.get("answer", "").strip()
    return "there"


def build_profile_summary(profile_data: list) -> str:
    """Convert profile Q&A array into a structured summary for the LLM."""
    if not profile_data:
        return ""

    lines = ["Patient Profile:"]
    for entry in profile_data:
        q = entry.get("question", "")
        a = entry.get("answer", "")
        # Strip the question mark format; present as facts
        label = q.replace("?", "").replace("What is your ", "").replace("What is ", "").strip()
        lines.append(f"- {label}: {a}")

    return "\n".join(lines)


def build_report_context(reports: list) -> str:
    """
    Build report context for the LLM.
    - If a main report exists (is_main=true): detailed summary as focal point.
    - Non-main reports: brief medical history line.
    """
    if not reports:
        return ""

    main_report = None
    other_reports = []

    for r in reports:
        if r.get("is_main", False):
            main_report = r
        else:
            other_reports.append(r)

    sections = []

    # ── Main report (detailed) ──
    if main_report:
        rd = main_report.get("report_data", {})
        sections.append(
            "── CURRENT ASSESSMENT REPORT (Primary Topic) ──\n"
            "This conversation is a continuation of a medical assessment report.\n"
            "The user may ask clarifications, question accuracy, or seek explanation.\n"
            "Treat this report as the primary topic unless the user shifts topic.\n"
        )

        urgency = rd.get("urgency_level", "unknown")
        sections.append(f"Urgency Level: {urgency}")

        summary = rd.get("summary", [])
        if summary:
            sections.append("Summary: " + " ".join(summary))

        causes = rd.get("possible_causes", [])
        if causes:
            cause_lines = []
            for c in causes:
                title = c.get("title", "Unknown")
                severity = c.get("severity", "unknown")
                prob = c.get("probability", 0)
                short = c.get("short_description", "")
                cause_lines.append(f"  - {title} ({severity}, {int(prob * 100)}% probability): {short}")

                # Include actionable detail
                detail = c.get("detail", {})
                what_to_do = detail.get("what_you_can_do_now", [])
                if what_to_do:
                    cause_lines.append("    What patient can do: " + "; ".join(what_to_do))

                warning = detail.get("warning", "")
                if warning:
                    cause_lines.append(f"    ⚠ Warning: {warning}")

            sections.append("Possible Causes:\n" + "\n".join(cause_lines))

        advice = rd.get("advice", [])
        if advice:
            sections.append("Advice: " + "; ".join(advice))

    # ── Non-main reports (brief history) ──
    if other_reports:
        history_lines = ["Past Medical Reports:"]
        for r in other_reports:
            rd = r.get("report_data", {})
            date = r.get("generated_at", "unknown date")
            summary = rd.get("summary", [])
            urgency = rd.get("urgency_level", "")
            brief = summary[0] if summary else "No summary"
            history_lines.append(f"  - {date}: {brief} (Urgency: {urgency})")
        sections.append("\n".join(history_lines))

    return "\n\n".join(sections)


def build_full_system_prompt(profile_data: list, reports: list) -> str:
    """
    Assemble the complete system prompt:
    [Base Remy Rules] + [Profile Summary] + [Report Context]
    """
    parts = [CHATBOT_SYSTEM_PROMPT.strip()]

    profile_summary = build_profile_summary(profile_data)
    if profile_summary:
        parts.append(profile_summary)

    report_context = build_report_context(reports)
    if report_context:
        parts.append(report_context)

    return "\n\n".join(parts)


# ─────────────────────────────
# API Endpoints
# ─────────────────────────────

@router.post("/start", response_model=StartChatResponse)
async def start_chat(request: StartChatRequest):
    """
    Start a new chat session with full medical context.

    1. Store profile_data and reports in PostgreSQL as JSONB.
    2. Build full system prompt with profile + reports context.
    3. Call LLM to generate a personalized welcome message.

    **Returns:**
    - `session_id`: Unique session identifier (UUID)
    - `message`: LLM-generated welcome message (uses patient name)
    - `is_first`: Always true for initial message
    """
    try:
        # Store profile_data and reports as raw JSONB
        profile_data_raw = [entry.model_dump() for entry in request.profile_data]
        reports_raw = request.reports

        session_id = create_chat_session(
            profile_data=profile_data_raw,
            reports=reports_raw
        )

        # Build the full system prompt with all context
        full_system_prompt = build_full_system_prompt(profile_data_raw, reports_raw)

        # Extract name for the start instruction
        patient_name = extract_patient_name(profile_data_raw)

        # Detect main report
        has_main_report = any(r.get("is_main", False) for r in reports_raw)

        # Instruct LLM to generate first message
        if has_main_report:
            start_instruction = (
                f"Start the conversation. Greet the patient by their name ({patient_name}). "
                f"Introduce yourself as Remy. Reference their recent assessment report briefly "
                f"and ask how you can help them understand or follow up on it. "
                f"Keep it warm, concise — 2-3 sentences max."
            )
        else:
            start_instruction = (
                f"Start the conversation. Greet the patient by their name ({patient_name}). "
                f"Introduce yourself as Remy. Ask how you can help them today. "
                f"Keep it warm, concise — 2-3 sentences max."
            )

        # Call LLM for the welcome message
        try:
            welcome_message = chatbot_client.generate_response(
                user_message=start_instruction,
                system_prompt_override=full_system_prompt
            )
        except Exception:
            # Fallback if LLM fails — never return empty
            if has_main_report:
                welcome_message = f"Hi {patient_name}! I'm Remy. Based on your recent report, how can I help you today?"
            else:
                welcome_message = f"Hi {patient_name}! I'm Remy. How can I help you today?"

        return StartChatResponse(
            session_id=session_id,
            message=welcome_message,
            is_first=True
        )

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to start chat: {str(e)}"
        )


@router.post("/message", response_model=ContinueChatResponse)
async def continue_chat(request: ContinueChatRequest):
    """
    Continue an existing chat conversation.

    1. Validate session exists in DB.
    2. Load stored profile_data + reports from Postgres.
    3. Build full system prompt with medical context.
    4. Append conversation history.
    5. Call LLM and return response.

    Frontend does NOT resend profile or reports — backend loads them every time.
    """
    try:
        # Validate history
        if not request.history or len(request.history) == 0:
            raise HTTPException(status_code=400, detail="History cannot be empty")

        # Last message must be from user
        last_message = request.history[-1]
        if last_message.role != "user":
            raise HTTPException(status_code=400, detail="Last message in history must be from user")

        # Load session from DB (profile + reports)
        session = get_chat_session(request.session_id)
        if not session:
            raise HTTPException(status_code=404, detail=f"Chat session not found: {request.session_id}")

        # Build full system prompt with profile + report context from DB
        profile_data = session.get("profile_data", [])
        reports = session.get("reports", [])
        full_system_prompt = build_full_system_prompt(profile_data, reports)

        # Build conversation history (exclude last message — sent separately)
        conversation_history = [
            {"role": msg.role, "content": msg.content}
            for msg in request.history[:-1]
        ]

        # Call LLM with full context
        try:
            response_msg = chatbot_client.generate_response(
                user_message=last_message.content,
                conversation_history=conversation_history if conversation_history else None,
                system_prompt_override=full_system_prompt
            )
        except Exception:
            response_msg = FALLBACK_MESSAGE

        return ContinueChatResponse(message=response_msg)

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Chat error: {str(e)}")


@router.post("/end", response_model=EndChatResponse)
async def end_chat(request: EndChatRequest):
    """
    End a chat session.

    Hard-deletes the session from PostgreSQL — profile, reports, everything.
    Next chat must be completely fresh.

    **Returns:**
    - `status`: "ended"
    """
    try:
        deleted = delete_chat_session(request.session_id)
        if not deleted:
            raise HTTPException(
                status_code=404,
                detail=f"Chat session not found: {request.session_id}"
            )

        return EndChatResponse(status="ended")

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Failed to end chat: {str(e)}"
        )


@router.get("/health")
async def health_check():
    """
    Health check endpoint for the chatbot service
    """
    try:
        if not chatbot_client.api_key:
            return {"status": "error", "message": "API key not configured"}

        return {
            "status": "healthy",
            "service": "chatbot",
            "model": chatbot_client.model
        }
    except Exception as e:
        return {"status": "error", "message": str(e)}
