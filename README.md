# Ehitedium — Standalone Visual Demo

This is a fresh Android Java project reconstructed from the described feature list. It is an autonomous visual demo with stylized test mannequins; it does not integrate with, inspect, or modify Standoff 2.

## Features
- Stylized test mannequins rendered with Android Canvas
- Toggleable outline boxes, colored fill, labels, and night background
- HEX color input, quick palette, and alpha slider
- Save/load local JSON configuration
- Floating round cat control to hide/show the settings panel
- GitHub Actions workflow for debug APK builds

## Build
The workflow uses JDK 17 and Gradle 8.9. Android Gradle Plugin 8.7.3 and compile SDK 35 are configured in the project. On GitHub, push the project to the `main` branch or manually run the `Android Debug Build` workflow. Download `ehitedium-demo-debug` from the run's Artifacts section.

## Gradle Wrapper
This source archive intentionally does not include generated `gradlew` / `gradlew.bat` scripts or the wrapper JAR. GitHub Actions installs Gradle 8.9 directly, so the workflow does not require the wrapper. If you want a standard wrapper for local builds, run `gradle wrapper --gradle-version 8.9` in a Gradle-enabled environment and commit the generated wrapper files.

## Notes
- The cat icon is drawn by Java code so no external `cat_icon.jpg` is required.
- The app does not request network access.
- This project has not been built or run on a physical device in this environment; verify the first Actions run.
