# Guidelines for Codex Agents

This repository hosts a Kotlin Multiplatform Mobile (KMM) project. The Android application is written with Jetpack Compose and uses Koin for dependency injection, Ktor for networking and SQLDelight for persistence. iOS targets use the same shared code with expect/actual implementations.

## Coding Style

- Use **four spaces** for indentation.
- Keep braces on the **same line** as the declaration.
- Try to keep line length under **100 characters**.
- Prefer meaningful commit messages starting with a short summary (e.g. `Refactor:` or `Fix:`) followed by details if needed.
- Kotlin files may omit a trailing newline, but adding one is preferred for consistency.

## Build & Test

- All Gradle scripts use the Kotlin DSL (`*.gradle.kts`).
- Before opening a pull request run:
  ```
  ./gradlew test
  ```
  This runs unit tests for all modules. Some tasks may require the Android SDK.

## Project Structure

- `androidApp/` – Android specific code and resources.
- `iosApp/` – iOS project using the shared framework.
- `shared/` – Common Kotlin code with expect/actual sources for each platform.
- `gradle/libs.versions.toml` – Central place for dependency versions.

Keep these conventions in mind when modifying or adding code.
