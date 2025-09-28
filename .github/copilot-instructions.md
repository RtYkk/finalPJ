# Repository custom instructions

## Project snapshot
- Single offline library-management app packaged as one APK inside the `android/` Gradle project, built with Kotlin and Jetpack Compose.
- User and admin roles coexist in the same application; all business data lives only in the local Room database.
- Barcode flows rely on CameraX + ML Kit and the limited network capability is strictly for fetching ISBN metadata and persisting it locally.
- Architecture references live in `libdocs/`; keep them out of runtime builds.

## Non-negotiable constraints
- Kotlin + Jetpack Compose are required; avoid alternative UI stacks or non-Kotlin modules.
- Persist everything with Room and keep the app fully offline-first; never sync patrons, loans, or inventory to remote services.
- Rely on CameraX + ML Kit for barcode scanning—no substitutions.
- Scope network calls to ISBN lookups only and store results locally before use.
- Guard privacy-sensitive values: validate `StudentId` with `^\d{8}$` and accept ISBN-13 only with checksum verification.

## Instruction map
- Platform-specific rules, package layout, data/transaction flows, and build commands: [android/.instructions.md](../android/.instructions.md)

## Quick build & quality checklist
- Run from `android/`: `./gradlew assembleDebug`, `./gradlew installDebug`, `./gradlew testDebugUnitTest`, `./gradlew connectedDebugAndroidTest`, `./gradlew lint`.
- Retry with `./gradlew clean assembleDebug` if KSP or Room schema generation stalls.
- Ensure `assembleDebug` and `lint` succeed before opening PRs and summarize their outputs.

## Forbidden additions
- No alternate scanning SDKs/ORMs, analytics, ads, remote config, or background sync components.
- Never transmit user, loan, or inventory data to external services.