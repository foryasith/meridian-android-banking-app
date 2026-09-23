# Meridian — UI-Only Android Project

Matches your lab requirement exactly: **no backend functionality, no
navigation logic, no click handlers.** Every Kotlin file does one thing:
`setContentView(...)`.

## What changed vs. the earlier version

- **`themes.xml`**: `navy_dark` is now baked directly into `Meridian.Button.Primary`,
  so every button using that style (Continue, Confirm Transfer, Done, Retry)
  is automatically navy with white text — no per-button color edits needed,
  which is what caused the earlier mismatch across screens.
- **All 8 Kotlin activities**: stripped down to bare `setContentView` calls.
  No `findViewById`, no `setOnClickListener`, no `Intent` navigation anywhere.
- **All 8 layouts**: unchanged from the high-fidelity design — Splash, Login,
  Dashboard, Transfer, Confirmation, Success, Error, Support.

## Setup

1. Unzip and open the `Meridian-UIOnly` folder in Android Studio (**File → Open**).
2. Click **Sync Now** when prompted — Gradle wrapper generates automatically.
3. Open any layout file in `res/layout/` and click the **Design** or **Split**
   tab (not Code) to preview it visually, or run on an emulator to see each
   screen render (tapping buttons will do nothing, by design).

## Adding your real logo

Drop your PNG into `res/drawable/` (lowercase filename, e.g. `logo.png`), then
replace `@drawable/ic_logo_arrow` with `@drawable/logo` in `activity_splash.xml`,
`activity_login.xml`, and `activity_dashboard.xml`.
