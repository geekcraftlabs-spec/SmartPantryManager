# Smart Pantry Manager

## Overview

Smart Pantry Manager is an Android application written in Java that helps a
household reduce food waste by tracking the ingredients they currently have in
their pantry and suggesting only the recipes they can cook **right now** with
those exact ingredients - no shopping trip, no "almost there" suggestions.

## Core concept - the strict-matching rule

A recipe is shown under **"You can cook these now"** only if the pantry
contains **at least the required quantity of every ingredient** that recipe
needs. The matching logic:

- Normalises ingredient names (singular/plural, common aliases like
  aubergine to eggplant).
- Normalises units and compares in a base dimension - for example
  `2 tbsp olive oil` satisfies `30 ml olive oil`.
- Sums multiple pantry entries for the same ingredient before comparing.
- Excludes partial matches from the main list entirely.
- Optionally surfaces recipes missing **exactly one** ingredient under a
  clearly separated "Almost there" section (never mixed with the strict list).

## Features

- **Pantry list** - RecyclerView backed by SQLite, with add, edit and delete.
- **Add/Edit Ingredient** - form with validation on name, quantity, unit and
  an optional ISO expiry date.
- **Suggested Recipes** - strict-match list plus a separate "almost there"
  list, refreshed on resume so pantry edits show immediately.
- **Recipe Detail** - full ingredient list and method for a selected recipe.
- **Settings** - persisted preferences for expiry alerts and a default unit.
- **20 seeded recipes** - loaded on first run only.

## Database choice - SQLite via SQLiteOpenHelper

Reasons:

1. The app is offline-first - there is no user account, no cross-device sync,
   and no server to maintain. Local persistence is the correct fit.
2. SQLite is bundled with Android and requires no additional dependency or
   service to deploy.
3. The strict-matching algorithm runs on a full join of two small tables - a
   local, indexed SQLite database comfortably handles this on-device.
4. Firebase and hosted PostgreSQL would add network latency and
   infrastructure cost for a use case that is fundamentally local.

Schema:

| Table | Purpose |
| --- | --- |
| ingredients | The user's pantry (full CRUD) |
| recipes | The seeded recipe collection (read-only) |
| recipe_ingredients | The required ingredients of each recipe (read-only) |

## Screens

1. **Pantry** - list, edit, delete, empty state, FAB to add.
2. **Add / Edit Ingredient** - validated form, cancelled by back.
3. **Suggested Recipes** - two clearly separated lists, empty state when
   nothing matches.
4. **Recipe Detail** - full ingredient list and method, opened by tapping a
   recipe card.
5. **Settings** - expiry-alert toggle and default-unit preference, saved to
   SharedPreferences.

## Build and run

### Prerequisites

- JDK 17 (`java -version` should show 17.x)
- Android SDK command-line tools, with `platforms;android-34` and
  `build-tools;34.0.0` installed
- Environment variables:
  - `ANDROID_HOME=C:\android-sdk`
  - `ANDROID_SDK_ROOT=C:\android-sdk`
  - `JAVA_HOME` pointing at the JDK 17 install

### Build

    gradlew.bat assembleDebug

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

### Install on a device or emulator

    adb install -r app/build/outputs/apk/debug/app-debug.apk
    adb shell am start -n com.example.smartpantry/.ui.MainActivity

### Emulator (optional)

    sdkmanager "emulator" "system-images;android-34;google_apis;x86_64"
    avdmanager create avd -n PantryPixel -k "system-images;android-34;google_apis;x86_64" -d "pixel_6"
    emulator -avd PantryPixel -gpu host -no-snapshot-load

## Project structure

    app/src/main/java/com/example/smartpantry/
      model/       Ingredient, Recipe, RecipeIngredient
      data/        PantryDbHelper, RecipeSeeder
      matching/    IngredientNormalizer, PantryMatcher, MatchResult
      adapters/    IngredientAdapter, RecipeAdapter
      ui/          MainActivity, PantryFragment, SuggestedRecipesFragment,
                   SettingsFragment, AddEditIngredientActivity, RecipeDetailActivity

## Video demonstration

A 5-7 minute walkthrough video is included in the submission ZIP. It covers:

1. GitHub commit history
2. Live app demo including a full CRUD cycle on pantry items and a
   demonstrated change in the strict-match list
3. Code walkthrough of the activity lifecycle, PantryDbHelper, the
   RecyclerView adapter and the PantryMatcher strict-matching algorithm
4. Database justification

## Author

Geekcraft Labs.