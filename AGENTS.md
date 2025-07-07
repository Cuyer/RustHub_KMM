# Guidelines for Codex Agents

This repository hosts a Kotlin Multiplatform Mobile (KMM) project. The Android application is written with Jetpack Compose and uses Koin for dependency injection, Ktor for networking and SQLDelight for persistence. iOS targets use the same shared code with expect/actual implementations.

## Coding Style

- Use **four spaces** for indentation.
- Keep braces on the **same line** as the declaration.
- Try to keep line length under **100 characters**.
- Prefer meaningful commit messages starting with a short summary (e.g. `Refactor:` or `Fix:`) followed by details if needed.
- Kotlin files may omit a trailing newline, but adding one is preferred for consistency.

## Build & Test

- Don't run tests as they don't pass.

## Project Structure

- `androidApp/` – Android specific code and resources.
- `iosApp/` – iOS project using the shared framework.
- `shared/` – Common Kotlin code with expect/actual sources for each platform.
- `gradle/libs.versions.toml` – Central place for dependency versions.

## ViewModel & Flow Conventions

- Derive all view models from `BaseViewModel` to gain access to `coroutineScope`.
- Expose screen state using a dedicated data class wrapped in a `MutableStateFlow`.
- Convert the mutable flow to an immutable `state` with `stateIn` and
  `SharingStarted.WhileSubscribed(5_000L)`.
- One-off events (navigation, snackbars) should use a `Channel` and `receiveAsFlow()`.
- When executing use cases, apply `.onStart { ... }` to set loading flags and
  `.onCompletion { ... }` to clear them.
- Handle errors with `.catch { e -> ... }` and collect using `collectLatest` when appropriate.

Keep these conventions in mind when modifying or adding code.
