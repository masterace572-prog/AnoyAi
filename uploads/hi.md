# Product Requirements Document (PRD)
## AI Chat Assistant - Multi-Provider Kotlin Android App

---

## 1. Product Overview

**Product Name:** ChatFlow (working title)

**Vision:** A clean, minimal Android chat application that lets users connect their own API keys from multiple AI providers (Gemini, Groq, DeepSeek, OpenRouter), switch between models seamlessly, view AI "thinking" process separately from final responses, and handle code blocks with syntax highlighting and one-tap copy — similar to ChatGPT, Claude, and Gemini apps.

**Platform:** Android (Kotlin, Jetpack Compose)

**Target Users:** Developers, power users, and AI enthusiasts who use multiple AI providers and want a unified, self-hosted-key chat client.

---

## 2. Goals & Objectives

| Goal | Description |
|---|---|
| Multi-provider support | Connect Gemini, Groq, DeepSeek, OpenRouter APIs using user-supplied keys |
| Model switching | Switch models mid-conversation or per new chat |
| Reasoning visibility | Show "thinking/reasoning" tokens separately (collapsible) when model supports it |
| Code handling | Syntax-highlighted code blocks with copy button |
| Free model filter | Toggle to show only free-tier models per provider |
| Clean UI/UX | Minimal, distraction-free, dark/light theme |
| Local-first & private | API keys stored securely on-device only |

---

## 3. Core Features

### 3.1 API Key Management
- Add/Edit/Delete API keys per provider (Gemini, Groq, DeepSeek, OpenRouter)
- Multiple keys per provider supported (label them e.g. "Groq - Personal")
- Keys stored encrypted locally (EncryptedSharedPreferences / Jetpack Security)
- Validate key on save (test ping call)
- Option to mask/unmask key in UI

### 3.2 Provider & Model Management
- Fetch available models dynamically from provider (where API supports listing, e.g., OpenRouter `/models`, Groq `/models`)
- For providers without list endpoints, maintain a static curated model list (editable/updatable via remote config JSON)
- "Free Models Only" toggle:
  - OpenRouter: filter models with `:free` suffix or `pricing.prompt == 0`
  - Groq: mark known free-tier models
  - Gemini: mark free-tier (Flash models under free quota)
  - DeepSeek: mark free/promo models
- Model metadata shown: context length, supports vision, supports reasoning, price/token

### 3.3 Chat Interface
- New Chat / Chat History (local storage via Room DB)
- Chat list sidebar (drawer) grouped by date
- Rename / delete / pin conversations
- Per-chat model selector (switch model mid-conversation)
- Multi-turn context maintained per conversation
- Streaming responses (token-by-token) via SSE/streaming API where supported
- Stop generation button
- Regenerate response button
- Edit user message & resubmit

### 3.4 Thinking / Reasoning Display
- For models that expose reasoning (e.g., DeepSeek-R1, OpenRouter reasoning models, Gemini thinking models):
  - Parse `reasoning`/`thinking` field from streamed response separately
  - Display in a collapsible "🧠 Thinking..." expandable card above final answer
  - Auto-collapse after response completes (user can expand)
  - Different subtle background/style than main answer bubble

### 3.5 Code Block Handling
- Detect fenced code blocks (```lang ... ```) in markdown response
- Render with syntax highlighting (language auto-detect or from fence tag)
- Header bar per code block: language label + Copy button
- Copy button → clipboard + toast/snackbar confirmation
- Optional: "Run" placeholder button (future feature, disabled for MVP)
- Line numbers toggle (optional setting)

### 3.6 Markdown Rendering
- Full markdown support: headers, bold/italic, lists, tables, blockquotes, links
- Inline code styling
- Math rendering (optional, LaTeX via KaTeX-like lib) — Phase 2

### 3.7 Settings
- Theme: Light / Dark / System
- Font size adjustment
- Default provider/model selection
- Manage API keys screen
- Free-models-only global toggle (also per-chat override)
- Clear all chats / export chats (JSON)
- About / version info

### 3.8 Message Actions
- Copy full message text
- Copy code blocks individually
- Share message
- Delete message
- Retry/regenerate
- Edit & resend (user messages)

---

## 4. Non-Functional Requirements

| Requirement | Detail |
|---|---|
| Performance | Smooth streaming UI, no jank on scroll (LazyColumn optimization) |
| Security | API keys encrypted at rest; no keys sent to any server except the respective provider's official endpoint |
| Offline handling | Cached chat history viewable offline; graceful error when no network |
| Scalability | Provider architecture must allow adding new providers easily (plugin-style interface) |
| Privacy | No analytics on chat content; optional crash reporting only |
| Accessibility | Font scaling, color contrast compliant (WCAG AA) |

---

## 5. Technical Architecture

### 5.1 Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture (Data / Domain / Presentation layers)
- **DI:** Hilt
- **Networking:** Retrofit + OkHttp (SSE streaming via OkHttp EventSource or manual buffered reader)
- **Local DB:** Room (chats, messages, models cache)
- **Secure Storage:** EncryptedSharedPreferences / Jetpack Security Crypto (API keys)
- **Markdown/Code rendering:** `markwon` library + `Prism4j` or `CodeView` for syntax highlighting
- **Async:** Kotlin Coroutines + Flow
- **Navigation:** Jetpack Navigation Compose

### 5.2 Module Structure
```
app/
 ├── data/
 │   ├── remote/
 │   │   ├── providers/
 │   │   │   ├── GeminiApi.kt
 │   │   │   ├── GroqApi.kt
 │   │   │   ├── DeepSeekApi.kt
 │   │   │   └── OpenRouterApi.kt
 │   │   └── dto/
 │   ├── local/
 │   │   ├── db/ (Room: ChatEntity, MessageEntity, ApiKeyEntity, ModelCacheEntity)
 │   │   └── secure/ (EncryptedPrefs for keys)
 │   └── repository/
 │       ├── ChatRepositoryImpl.kt
 │       ├── ApiKeyRepositoryImpl.kt
 │       └── ModelRepositoryImpl.kt
 │
 ├── domain/
 │   ├── model/ (Chat, Message, AiModel, ApiKey)
 │   ├── repository/ (interfaces)
 │   └── usecase/
 │       ├── SendMessageUseCase.kt
 │       ├── StreamResponseUseCase.kt
 │       ├── GetModelsUseCase.kt
 │       └── SaveApiKeyUseCase.kt
 │
 ├── presentation/
 │   ├── chat/ (ChatScreen, ChatViewModel)
 │   ├── settings/ (SettingsScreen, ApiKeyScreen)
 │   ├── modelpicker/ (ModelPickerBottomSheet)
 │   ├── components/ (MessageBubble, CodeBlockView, ThinkingCard, MarkdownText)
 │   └── theme/ (Color.kt, Type.kt, Theme.kt)
 │
 └── di/ (Hilt Modules)
```

### 5.3 Provider Abstraction (Key Design Pattern)

```kotlin
interface AiProvider {
    val id: String                 // "gemini", "groq", "deepseek", "openrouter"
    val displayName: String
    fun buildRequest(apiKey: String, model: String, messages: List<ChatMessage>, stream: Boolean): Request
    fun parseStreamChunk(rawChunk: String): ParsedChunk? // returns content or reasoning piece
    suspend fun fetchModels(apiKey: String): List<AiModel>
}

data class ParsedChunk(
    val contentDelta: String? = null,
    val reasoningDelta: String? = null,
    val isDone: Boolean = false
)

data class AiModel(
    val id: String,
    val provider: String,
    val displayName: String,
    val isFree: Boolean,
    val supportsReasoning: Boolean,
    val contextLength: Int
)
```

Each provider implements this interface with its own request/response schema:
- **Gemini:** `generativelanguage.googleapis.com/v1beta/models/{model}:streamGenerateContent`
- **Groq:** OpenAI-compatible `/openai/v1/chat/completions` (stream=true, SSE)
- **DeepSeek:** OpenAI-compatible endpoint + `reasoning_content` field in stream
- **OpenRouter:** OpenAI-compatible, supports `:free` model suffix, `reasoning` field for supported models

### 5.4 Data Models (Room Entities)

```kotlin
@Entity
data class ChatEntity(
    @PrimaryKey val id: String,
    val title: String,
    val providerId: String,
    val modelId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val pinned: Boolean = false
)

@Entity
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val role: String,           // "user" | "assistant" | "system"
    val content: String,
    val reasoning: String?,     // thinking content, nullable
    val modelUsed: String?,
    val timestamp: Long,
    val isError: Boolean = false
)

@Entity
data class ApiKeyEntity(
    @PrimaryKey val id: String,
    val providerId: String,
    val label: String,
    val encryptedKey: String,
    val isActive: Boolean = true,
    val addedAt: Long
)

@Entity
data class ModelCacheEntity(
    @PrimaryKey val id: String,
    val providerId: String,
    val displayName: String,
    val isFree: Boolean,
    val supportsReasoning: Boolean,
    val contextLength: Int,
    val lastFetched: Long
)
```

---

## 6. UI/UX Design Specification

### 6.1 Design Principles
- Minimal, generous whitespace, no clutter
- Rounded bubbles (16dp radius), soft shadows
- Monochrome + single accent color (customizable, default indigo/violet)
- Typography: Inter / Roboto, 15sp body text
- Dark mode as default (AMOLED-friendly true black option)

### 6.2 Key Screens

**1. Chat List (Home/Drawer)**
- Search bar at top
- "+ New Chat" button (FAB or top button)
- List of past chats grouped: Today / Yesterday / Last 7 days / Older
- Swipe to delete, long-press for rename/pin

**2. Chat Screen**
```
┌─────────────────────────────────┐
│ ☰  Chat Title      [Model ▾] ⋮  │  <- top bar with model switcher
├─────────────────────────────────┤
│                                  │
│  🧠 Thinking... (tap to expand) │  <- collapsible reasoning card
│  ┌──────────────────────────┐  │
│                                  │
│  Assistant response text...     │
│  ┌─────────────────────────┐   │
│  │ python           📋 Copy │   │  <- code block with copy
│  │ def hello():             │   │
│  │     print("hi")          │   │
│  └─────────────────────────┘   │
│                                  │
│              User message here →│  <- right aligned user bubble
│                                  │
├─────────────────────────────────┤
│ [＋] [Type a message...]    [➤] │  <- input bar
└─────────────────────────────────┘
```

**3. Model Picker (Bottom Sheet)**
- Tabs: Gemini | Groq | DeepSeek | OpenRouter | All
- Toggle switch: "Free models only" (top right)
- Each model card: name, context length badge, reasoning icon if supported, free/paid tag
- Search bar to filter models

**4. API Key Management Screen**
- List of saved keys grouped by provider with status dot (active/inactive/invalid)
- "+ Add API Key" → dialog: Select Provider → Enter Label → Paste Key → Validate → Save
- Eye icon to reveal/hide key
- Delete with confirmation

**5. Settings Screen**
- Appearance (theme, font size, accent color)
- Default Provider/Model
- API Keys (link to management screen)
- Data (export, clear chats)
- About

### 6.3 Color Palette (Suggested)
```
Light Mode:
- Background: #FFFFFF
- Surface: #F7F7F8
- Primary Accent: #6366F1 (Indigo)
- Text Primary: #1A1A1A
- Text Secondary: #6B7280
- Code BG: #F1F1F3

Dark Mode:
- Background: #0F0F10
- Surface: #1A1A1C
- Primary Accent: #818CF8
- Text Primary: #E5E5E7
- Text Secondary: #9CA3AF
- Code BG: #18181B
```

---

## 7. User Flows

### 7.1 First Launch Flow
1. Welcome screen → "Add your first API key"
2. Select provider → paste key → validate → save
3. Land on empty chat screen with default model selected

### 7.2 Sending a Message Flow
1. User types message → taps send
2. Message appended to chat (Room DB + UI)
3. Request sent to selected provider with streaming enabled
4. If reasoning chunks arrive → populate "Thinking" card in real-time
5. Content chunks stream into response bubble in real-time
6. On completion → save full message (content + reasoning) to DB
7. Markdown parsed → code blocks rendered with copy buttons

### 7.3 Switching Models Mid-Chat
1. Tap model name in top bar → bottom sheet opens
2. Select new provider/model → sheet closes
3. Next message uses new model (chat history/context preserved and sent to new model)

### 7.4 Free Models Filter
1. Toggle switch in Model Picker or Settings
2. Model list re-filters instantly (client-side filter on cached model metadata)

---

## 8. API Integration Notes

| Provider | Auth | Streaming | Reasoning Support | Free Models |
|---|---|---|---|---|
| **Gemini** | `x-goog-api-key` header or query param | Yes (SSE) | Gemini 2.0 Flash Thinking exposes thoughts | Flash models free tier |
| **Groq** | Bearer token | Yes (SSE, OpenAI format) | Some models (deepseek-r1-distill on Groq) | Most Groq models free tier |
| **DeepSeek** | Bearer token | Yes (SSE, OpenAI format) | `reasoning_content` field (R1 model) | deepseek-chat has free quota |
| **OpenRouter** | Bearer token | Yes (SSE, OpenAI format) | `reasoning` field for supported models | Models with `:free` suffix |

**Standard Chat Completion Request (OpenAI-compatible providers):**
```json
POST /chat/completions
{
  "model": "deepseek-reasoner",
  "messages": [{"role": "user", "content": "..."}],
  "stream": true
}
```

**Streamed chunk parsing (SSE):**
```
data: {"choices":[{"delta":{"reasoning_content":"Let me think..."}}]}
data: {"choices":[{"delta":{"content":"The answer is..."}}]}
data: [DONE]
```

---

## 9. MVP Scope (Phase 1)

✅ Must-have for v1.0:
- API key add/manage (4 providers)
- Basic chat with streaming
- Model picker with free-only toggle
- Markdown + code block rendering with copy
- Thinking/reasoning collapsible display
- Chat history (local, Room DB)
- Light/dark theme

🔜 Phase 2 (post-MVP):
- Model fetch auto-refresh from providers
- Image/vision input support
- Export chat as PDF/Markdown
- Custom system prompts per chat
- Voice input
- Multi-language UI
- Cloud backup (optional, encrypted)
- LaTeX/math rendering
- Attachments (PDF/image analysis)

---

## 10. Success Metrics
- Time to first message < 30s from install
- Crash-free session rate > 99%
- Average response streaming latency (perceived) acceptable per provider
- User retention: chat history usage indicates repeat engagement

---

## 11. Suggested Libraries (build.gradle)

```kotlin
dependencies {
    // Compose
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp2:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Markdown + Code highlighting
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:syntax-highlight:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
}
```

---

## 12. Next Steps

I can now generate the **actual Kotlin project code** based on this PRD. Suggested build order:

1. **Project setup** — Gradle config, theme, navigation skeleton
2. **API Key management** — DB + encrypted storage + UI screens
3. **Provider abstraction layer** — interfaces + one working provider (e.g., Groq, since OpenAI-compatible is easiest)
4. **Chat screen** — UI + streaming integration for one provider
5. **Add remaining providers** (Gemini, DeepSeek, OpenRouter)
6. **Thinking card + code block components**
7. **Model picker + free-filter logic**
8. **Settings screen + theming polish**
