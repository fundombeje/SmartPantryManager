# Smart Pantry Manager

An Android app written in Java that helps reduce food waste. The user tracks
the ingredients they currently have at home, and the app suggests only the
recipes they can cook right now, with no missing ingredients.

Built for Mobile App Development 700 (Richfield Graduate Institute of Technology).

## Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list screen
- 15-20 pre-loaded recipes
- Suggested Recipes screen using strict matching: a recipe appears only if
  every ingredient is in the pantry in the required quantity
- Recipe detail screen
- Settings screen

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

## Setup and Run Instructions
1. Install Android Studio.
2. Clone the repository:
   `git clone https://github.com/fundombeje/SmartPantryManager.git`
3. Open the project folder in Android Studio and wait for Gradle sync to finish.
4. Select an emulator (e.g. Pixel, API 34) or connect a physical device.
5. Click **Run**.

Minimum SDK: API 24.

## Author
Fundo Mbeje,402411028
