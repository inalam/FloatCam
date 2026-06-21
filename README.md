# FloatCam

FloatCam is a lightweight, fully local Android IP Camera viewer built entirely with Kotlin, Jetpack Compose, and Media3. It focuses on zero tracking, zero ads, and pure functionality with a modern, AMOLED Black UI.

### Why build a custom app?
We built FloatCam because the built-in [IP Webcam](https://play.google.com/store/apps/details?id=com.pas.webcam) web view **does not support Picture-in-Picture (PiP)**. FloatCam solves this by providing a dedicated app that can float over your other apps while you monitor your camera.

### Web Version
Looking for a desktop solution? Check out the [FloatCam Chrome Extension](https://github.com/inalam/FloatCam-ChromeExtension).

## Features

- **MJPEG Video Streaming:** Utilizes an optimized Android `WebView` to render MJPEG streams flawlessly with zero native buffering lag. Full pinch-to-zoom support is included.
- **Headless Audio:** Uses AndroidX Media3 (ExoPlayer) in the background to seamlessly stream `.wav` audio without interrupting the video feed.
- **Picture-in-Picture (PiP):** Supports both manual (floating button) and automatic (swiping home) Picture-in-Picture mode, allowing you to monitor your camera while using other apps.
- **Instant Mute Toggle:** A floating button instantly cuts the audio feed volume without dropping the stream connection.
- **Zero Ads & Telemetry:** Absolutely zero external tracking SDKs. It requests only the `INTERNET` permission for local network access.

## Stream Configuration
The app allows you to dynamically set your camera's IP address directly from the settings dialog within the app. By default, it connects to:
- **Base URL:** `http://192.168.1.35:8080`
- **Video Stream:** `/video` endpoint
- **Audio Stream:** `/audio.wav` endpoint

You can also open the camera's web panel directly from the app's settings menu.

## Cloud Build (GitHub Actions)
FloatCam is configured with a GitHub Actions workflow (`.github/workflows/build.yml`) that compiles the APK completely in the cloud and automatically publishes it.

1. Push your changes to the `main` or `master` branch.
2. The **Build Android APK** workflow will automatically run.
3. Once completed, go to the **Releases** section on the right side of your GitHub repository.
4. Download the compiled `.apk` directly from the latest release and sideload it onto your Android device.
