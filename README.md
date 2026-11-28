# Mercy AI

Mercy AI is an early-stage Android assistant project by Xia-Tech.

## Project status

**Development build — not ready for production or F-Droid submission.**

The current starter app includes:
- A dark-mode, four-screen Android interface.
- On-device note storage using private app preferences.
- Optional speech-to-text input through Android's installed speech-recognition service.
- System document and image pickers.

Not implemented yet:
- AI model or assistant backend.
- OCR / image text recognition.
- General file reading, editing, or management.
- Device-setting controls.

The interface does not claim these unfinished features are available.

## Android compatibility

- Minimum Android version: Android 8.1 (API 27).
- Java source compatibility: Java 17.
- No third-party runtime libraries are currently required.

## Build

A GitHub Actions workflow is configured to compile the debug APK when changes are pushed to this branch.

Open the project in a compatible Android Gradle Plugin / Gradle environment and build the debug APK:

```sh
gradle assembleDebug
```

A Gradle wrapper will be added after the project's build toolchain is pinned and verified.

## Privacy

Saved notes are stored in the app's private SharedPreferences storage on the device. The current build does not include an AI network client or analytics SDK. Android's speech-recognition service may be provided by the device manufacturer or another installed service; its own privacy behavior is outside this app's control.

## License

Mercy AI is intended to be distributed under GNU GPL-3.0-or-later. The complete license text must be included before release.
