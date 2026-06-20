# FloatCam

FloatCam is a lightweight, fully local Android IP Camera viewer built entirely with Kotlin, Jetpack Compose, and Media3. It focuses on zero tracking, zero ads, and pure functionality with a modern, AMOLED Black UI.

### Why build a custom app?
We built FloatCam because the built-in IP Webcam web view **does not support Picture-in-Picture (PiP)**. FloatCam solves this by providing a dedicated app that can float over your other apps while you monitor your camera.

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
FloatCam is configured with a GitHub Actions workflow (`.github/workflows/build.yml`) that compiles the APK completely in the cloud.

1. Push this repository to GitHub.
2. Go to the **Actions** tab on your GitHub repository page.
3. The **Build Android APK** workflow will automatically run.
4. Once completed, scroll to the bottom of the workflow summary and download the `floatcam-debug` artifact (which will be a `.zip` file).
5. Extract the `.zip` to retrieve your compiled `.apk` and sideload it onto your Android device.

> **Note:** GitHub Actions natively compresses all artifacts into a `.zip` file to save server bandwidth. You must extract it before installing.
