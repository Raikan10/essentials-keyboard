# Peyokeys

Voice + AI drafting keyboard for Android. Peyokeys is an Input Method (IME) that lets you:
- Hold “Transcribe” to dictate (speech-to-text via Cactus STT)
- Hold “AI Draft” to say what you want written; a local LM streams the draft into the text field
- Download/switch STT and LM models from the app

## How it works
- App module contains:
  - `PeyoKeysService` (InputMethodService): custom QWERTY/Numbers layouts, mic handling, streaming text insertion, simple capitalization logic.
  - `MainActivity`: model manager UI (tabs for Voice and LM), permissions, and shortcut to keyboard settings.
- Uses the Cactus SDK (`com.cactuscompute:cactus`) for STT (`CactusSTT`) and LM (`CactusLM`). Models are downloaded on-device from the SDK.

## Requirements
- Android Studio (latest stable)
- Android SDK; device/emulator API 28+
- JDK 11+ (AGP may require 17 in Android Studio settings)

## Build & install
```sh
./gradlew assembleDebug     # build APK
./gradlew installDebug      # install to connected device/emulator
```
APK: `app/build/outputs/apk/debug/`.

## Enable the keyboard
On device: Settings → System → Languages & input → On-screen keyboard → Manage keyboards → enable “Peyokeys”, then select it as the current keyboard.
You can also tap the in-app “Enable Keyboard” button to jump to settings.

## Use it
- Transcribe: open any text field, hold the “Transcribe” button, speak, then release; text is inserted.
- AI Draft: hold “AI Draft”, speak your instruction (e.g., “Draft a polite reply about…”) and release; the LM streams the draft into the field. The service filters think-tags and handles partial UTF‑8 tokens.
- Switch 123/ABC to change layouts; Shift auto-capitalizes after sentence endings/newlines.

## Manage models (in app)
- Voice Models tab: download/select STT model (Whisper variants, etc.).
- LM Models tab: download/select LM. First use initializes the selected model.

## Permissions
- Microphone: RECORD_AUDIO (requested on first use or via in-app permission flow).
- Internet: declared for SDK/model fetching.

## Troubleshooting
- No text after speaking: ensure mic permission is granted, and the selected STT/LM models are downloaded.
- Keyboard not showing: verify it’s enabled and selected as current input method.

## Project layout
- `app/` Android application/IME
- `app/src/main/java/com/cactus/peyokeys/` – `PeyoKeysService.kt`, `MainActivity.kt`
- `app/src/main/res/layout/` – `keyboard.xml`, `keyboard_numbers.xml`, `activity_main.xml`
- `app/src/main/AndroidManifest.xml` – service + permissions
