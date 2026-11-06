# Reel Monster (Level 1: manual)

Floating monster over Instagram/YouTube. Tap it after each reel (+1).
Counts are stored per day on the phone. No internet, no login.

## Build without Android Studio (Linux/Mac; on Windows use WSL or the same tools)
1. Install JDK 17.
2. Install Android command-line tools, then:
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   export ANDROID_HOME=<your sdk folder>
   echo "sdk.dir=$ANDROID_HOME" > local.properties
3. Create the Gradle wrapper once (needs Gradle installed): gradle wrapper --gradle-version 8.7
4. ./gradlew assembleDebug
5. Phone: enable Developer options + USB debugging, plug in, then
   adb install -r app/build/outputs/apk/debug/app-debug.apk

## First run
Open Reel Monster -> allow "Display over other apps" -> Start the monster.
Drag the monster anywhere. Tap = +1. Then open Instagram or YouTube.
