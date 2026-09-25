# ZenFlow (Android)

Android version of [ZenFlow](https://github.com/shadowfax-505/Zenflow), a focus
timer and screen-time tracker.

## Features

- Focus sessions with a timer, saved to a local history
- Reminders
- App usage tracking — each app is rated *OK*, *cut back* or *seriously
  addicted* based on foreground time, with configurable thresholds
- User and admin login screens

## Stack

Java · Android SDK 34 (min 24) · Room · Firebase Analytics

## Build

1. Create a Firebase project, register the app id `com.zenflow.mobile`, and
   put the downloaded `google-services.json` in `app/`.
2. Open the project in Android Studio, or run:

   ```bash
   ./gradlew assembleDebug
   ```

Usage tracking needs the *Usage access* permission, granted from system
settings on first launch.
