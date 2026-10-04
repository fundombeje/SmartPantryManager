# Smart Pantry Manager

A Java Android app that helps reduce food waste. The user keeps track of the
ingredients they have at home, and the app suggests only the recipes they can
cook right now: every ingredient must already be in the pantry, in at least the
required quantity. No shopping trip required.

Built for Mobile App Development 700, Richfield Graduate Institute of Technology.

## Features
- Pantry management: add, edit and delete items (name, quantity, unit, optional
  expiry date) with input validation
- Swipe an item left or right to delete it, with an Undo option
- 19 recipes pre-loaded on first launch
- Suggested Recipes screen using strict matching, with a clear message when
  nothing matches
- Recipe detail screen with the full ingredient list and method
- Settings screen: highlight items that are expiring within 3 days or expired
- All data persists after the app is closed and reopened

## The strict-matching rule
A recipe is suggested only if every ingredient it needs is in the pantry, in at
least the required quantity. A recipe missing even one ingredient is never shown.

To cope with everyday messiness, the matcher:
- ignores capital letters and treats singular and plural names as the same
  (Tomatoes = tomato)
- converts units to a base unit, so 1 kg of rice covers a recipe that needs
  200 g (kg to g, l to ml)
- adds up duplicate pantry entries of the same ingredient

Weight, volume and counts are never mixed: 200 g cannot be satisfied by 2 pcs.

## Database
**Room (SQLite)**, stored locally on the device.

Why I chose SQLIte:

- No Firebase account/configuration
- No server
- No REST API
- No internet dependency
- Database lives inside the Android application
- Easy to demonstrate CRUD
- Easy to demonstrate persistence
- Perfectly appropriate for a pantry application

Tables: `pantry_items`, `recipes` and `recipe_ingredients` (each ingredient row
links to its recipe by `recipeId` and is deleted along with the recipe).

## Technology
- Java, built with Android Studio
- Room persistence library (SQLite)
- RecyclerView with custom adapters
- Intents and a toolbar menu for navigation
- SharedPreferences for settings
- JUnit unit tests for the matching and expiry logic
- Min SDK 24, target SDK 37

No maps, location services or internet access are used.

## Project structure
- `data/`: Room entities, DAOs, AppDatabase and the recipe seeder
- `logic/`: RecipeMatcher, IngredientNormalizer and ExpiryHelper
- Main package: the activities (pantry list, add/edit, suggested recipes,
  recipe detail, settings), the adapters and AppSettings
- `app/src/test/`: unit tests for the matcher and the expiry helper

## Setup and run instructions
1. Install Android Studio and an Android SDK.
2. Clone the repository:
   `git clone https://github.com/[your-username]/SmartPantryManager.git`
3. Open the project folder in Android Studio and wait for Gradle sync to finish
   (internet is needed once to download dependencies).
4. Start an emulator from Device Manager (for example a Pixel 8, API 24 or
   higher) or connect a phone with USB debugging. Developed and tested on a
   Pixel 8 emulator, API 37.
5. Click Run. The recipes are added to the database automatically on first launch.
6. To run the unit tests, right-click `app/src/test` and choose Run Tests.

## Quick demo
1. Add: bread 4 pcs, eggs 3 pcs, butter 250 g, salt 1 kg
2. Open Suggested recipes (toolbar). Scrambled Eggs and Fried Egg Sandwich appear.
3. Add cheese 100 g. Cheese Toastie and Cheese Omelette join the list.
4. Swipe the cheese away. Those two recipes disappear again.
.

## Author
Fundo Mbeje, 402411028
