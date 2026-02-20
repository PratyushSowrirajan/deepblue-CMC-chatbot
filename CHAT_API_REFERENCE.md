# Chat API Endpoints - Quick Reference

## Base URL
`http://localhost:8000`

---

## 1️⃣ Start Chat
**Endpoint:** `GET /chat/start`

**Description:** Initialize a new chat session and get welcome message

**Request:** None (GET endpoint)

**Response:**
```json
{
  "session_id": "chat_e8b43e0bb1",
  "message": "Hi Gowtham! I'm Remy. How can I help you today?",
  "is_first": true
}
```

**Note:** Response uses `session_id`, but subsequent requests use `chat_session_id`

**Example:**
```bash
curl http://localhost:8000/chat/start
```

---

## 2️⃣ Continue Chat
**Endpoint:** `POST /chat/message`

**Description:** Send a message with conversation history to get AI response

**Request Body:**
```json
{
  "chat_session_id": "chat_12345",
  "history": [
    {
      "role": "assistant",
      "content": "Hi Gowtham! I'm Remy. How can I help you today?"
    },
    {
      "role": "user",
      "content": "I still feel weak after fever."
    }
  ]
}
```

**Response:**
```json
{
  "message": "That's common after viral infections. Are you experiencing dizziness or fatigue?"
}
```

**Important Notes:**
- `history` must include ALL previous messages in the conversation
- Last message in `history` must have `role: "user"` (the current user message)
- Previous messages maintain context for stateless API
- Frontend sends complete history with each request

**Example:**
```bash
curl -X POST http://localhost:8000/chat/message \
  -H "Content-Type: application/json" \
  -d '{
    "chat_session_id": "chat_12345",
    "history": [
      {"role": "assistant", "content": "Hi Gowtham! I'\''m Remy. How can I help you today?"},
      {"role": "user", "content": "I still feel weak after fever."}
    ]
  }'
```

---

## 3️⃣ End Chat
**Endpoint:** `POST /chat/end`

**Description:** Terminate a chat session

**Request Body:**
```json
{
  "chat_session_id": "chat_12345"
}
```

**Response:**
```json
{}
```

**Example:**
```bash
curl -X POST http://localhost:8000/chat/end \
  -H "Content-Type: application/json" \
  -d '{"chat_session_id": "chat_12345"}'
```

---

## 🔄 Conversation Flow

1. **Frontend calls** `GET /chat/start`
   - Receives `session_id` and welcome message
   - Displays welcome message to user

2. **User types message**
   - Frontend builds `history` array with:
     - Welcome message (role: "assistant")
     - User's message (role: "user")
   - Calls `POST /chat/message` with history

3. **AI responds**
   - Backend processes with full context
   - Returns AI response

4. **Next user message**
   - Frontend adds AI response to history
   - Adds new user message to history
   - Calls `POST /chat/message` again with updated history

5. **End conversation**
   - Frontend calls `POST /chat/end`
   - Session terminated

---

## 🔑 Configuration

**API Key:** Configured in `.env` file
```
CHATBOT_CEREBRAS_API_KEY=csk-5tmvfx29vmkjrcjyvrfv4pvwydp5ke6vfxed6pmt4m2drxkv
```

**Model:** `llama3.1-8b` (Cerebras)

---

## 🚫 Separation from Decision Tree System

These chat endpoints are **completely separate** from the existing clinical decision tree system:

| Feature | Chat API | Decision Tree |
|---------|----------|---------------|
| Endpoints | `/chat/*` | `/assessment/*` |
| API Key | `CHATBOT_CEREBRAS_API_KEY` | `CEREBRAS_API_KEY` |
| Purpose | General chatbot | Medical assessment |
| State | Stateless (history sent each time) | Session-based |

---

## 📚 API Documentation

Interactive docs available at: `http://localhost:8000/docs`

---

## ✅ Testing Results

All endpoints tested and working:
- ✅ `GET /chat/start` - Returns welcome message
- ✅ `POST /chat/message` - AI responds with context
- ✅ `POST /chat/end` - Session ends successfully
- ✅ Cerebras API integration working
- ✅ Conversation history maintained properly
