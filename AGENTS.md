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
- Don't build as it would always fail

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
- Follow MVI and Clean Architecture patterns
- Be creative, oftentimes libraries are new or have new functionalities, so you won't have solutions online
- Write code following Clean Architecture principles (separate data, domain, presentation layers).
- Use Jetpack Compose and Material 3 for UI/Material3 Expressive.
- Inject dependencies using Koin (dependency injection).
- Integrate with backend via Ktor Client and map DTOs to domain models.
- Ensure type safety, null safety, error handling, and code readability.
- Use StateFlow/Flow for state management in ViewModels.
- Apply repository and use case patterns in the domain layer.
- Optimize recompositions in Compose – use remember, rememberUpdatedState, and avoid unnecessary recomposables.
- Pass only necessary parameters to composables, maintain code clarity and modularity.
- Generate production-ready code for Android/KMM projects.

Keep these conventions in mind when modifying or adding code.
