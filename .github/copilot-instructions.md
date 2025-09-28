# Repository custom instructions (concise)

## Overview
Single offline Android app (Kotlin) for a library system. Two roles in one APK (User/Admin). Local data only via Room. Barcode flows with CameraX + ML Kit. Limited network only to fetch book metadata by ISBN and then persist locally.

## Constraints
- Kotlin only; coroutines + Flow encouraged.
- Storage: Room (Entities/DAO/Database). Borrow/return must be transactional.
- Scanning: CameraX + ML Kit Barcode Scanning only.
- Student ID: exactly 8 digits (regex: ^\d{8}$).
- Networking: allowed only for ISBN → book metadata; no user/loan data leaves the device.

## Build/Test/Lint
- Build: ./gradlew assembleDebug
- Install: ./gradlew installDebug
- Unit tests: ./gradlew testDebugUnitTest
- Instrumented (if any): ./gradlew connectedDebugAndroidTest
- Lint: ./gradlew lint
- If KSP/Room issues: ./gradlew clean assembleDebug

## Layout (high level)
- app/data (Room: entities, dao, db, repositories)
- app/domain (use cases, optional)
- app/ui (Compose or Views, navigation, ViewModels)
- app/scan (CameraX + ML Kit analyzer)
- app/network (Retrofit/OkHttp services, DTOs, mappers)
- app/common (validation, time, helpers)

## Validation & transactions
- Student ID must match ^\d{8}$ at all entry points.
- ISBN-13 is 13 digits; prefer checksum validation.
- Borrow: availableCount > 0, decrement, insert Loan(status=BORROWED).
- Return: set status=RETURNED, increment availableCount.
- Keep availableCount within [0, copyCount].

## Networking (ISBN only)
- Use Retrofit + OkHttp with short timeouts and small cache.
- Flow: check Room → if missing, fetch by ISBN → map → upsert → render.
- No API calls for students/loans/inventory; avoid API keys if possible.

## Do not add
- No alternate scanners/ORMs; no analytics/ads/remote config; no background sync.

## PRs
- Ensure assembleDebug + lint pass; update Room migrations on schema changes.
- Note any scanning/networking changes and their validation impact.