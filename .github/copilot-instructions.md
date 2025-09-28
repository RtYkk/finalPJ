# Repository custom instructions

## Project snapshot
- Single offline library-management app packaged as one APK inside the `android/` Gradle project, built with Kotlin and Jetpack Compose.
- User and admin roles coexist in the same application; all business data lives only in the local Room database.
- Barcode flows rely on CameraX + ML Kit; the limited network capability is strictly for fetching book metadata by ISBN and persisting it locally right away.
- The `libdocs/` folder contains architecture/flow diagrams for reference; do not load them directly inside the app.
- Use material design principles and follow Android development best practices.

## Current code layout
- `android/app/src/main/java/jlu/kemiko/libman/MainActivity.kt`: app entry point with Material 3 + Compose enabled.
- `android/app/src/main/java/jlu/kemiko/libman/ui/theme/`: Compose theme and design-system placeholders.
- Add the following subpackages under `jlu.kemiko.libman` as the project grows:
  - `data`: Room entities, DAO, database, repositories; handle offline-first and transactional operations.
  - `domain`: Optional use-case layer encapsulating business rules (borrow, return, search, etc.).
  - `ui`: Compose screens, navigation, and ViewModels, organized by feature (loans, inventory, scanning, ...).
  - `scan`: CameraX preview, ML Kit barcode analyzer, and scanning state machine.
  - `network`: Retrofit/OkHttp definitions, DTOs, and mappers for ISBN metadata.
  - `common`: Shared utilities such as validation helpers, time/formatting, and result wrappers.

## Technology guidelines
- Kotlin only; prefer coroutines + Flow for async work and data streams.
- UI must use Jetpack Compose + Material 3 with unidirectional data flow and proper state hoisting.
- Dependency injection (e.g., Hilt/Koin) can be introduced later; update this file if you add it.

## Data and transaction rules
- Persist everything with Room; borrowing/returning and other stock mutations must execute inside a single transaction.
- Validate `StudentId` against `^\d{8}$` at every entry point (UI, use cases, data layer).
- Only accept ISBN-13 and prefer checksum validation.
- Borrow flow: when `availableCount > 0`, decrement it and insert `Loan(status = BORROWED)`.
- Return flow: update `Loan.status = RETURNED` and increment `availableCount`.
- Always keep `availableCount` within `[0, copyCount]`.

## Scanning and networking
- CameraX + ML Kit Barcode Scanning are mandatory; do not replace them with other SDKs (e.g., ZXing).
- ISBN lookup sequence: check Room → if missing call the remote API → on success upsert locally → render.
- Network traffic is restricted to ISBN metadata; never send student, loan, or inventory data off device.
- Configure Retrofit/OkHttp with short timeouts and a small cache; avoid extra dependencies or API keys.

## Build / test / quality
- Run commands from the `android/` directory:
  - Build: `./gradlew assembleDebug`
  - Install: `./gradlew installDebug`
  - Unit tests: `./gradlew testDebugUnitTest`
  - Instrumented tests: `./gradlew connectedDebugAndroidTest`
  - Lint: `./gradlew lint`
- If KSP or Room schema generation stalls, retry with `./gradlew clean assembleDebug`.

## PR and code-review checklist
- Ensure `assembleDebug` and `lint` pass; include a short summary of the outputs in the PR description.
- Update Room migrations and add tests or notes whenever the schema changes.
- Document how any scanning or networking change affects permissions, validation, and offline behavior.
- Follow Kotlin code-style guidelines; avoid unrelated reformatting or large-scale reordering.

## Forbidden additions
- No alternative scanning SDKs/ORMs, analytics, ads, remote config, or background sync components.
- Never transmit user, loan, or inventory data to external services.