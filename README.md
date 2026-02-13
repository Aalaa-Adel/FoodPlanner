🍽️ FoodPlanner (MealMate) — Meal Discovery & Planning App

FoodPlanner is an Android application that helps users discover meals, explore categories & countries, save favorites, and build a personal meal plan — with a smooth UI, offline-friendly UX, and smart sync when the connection comes back 🔄.

Built with clean architecture mindset (UI / Data / Domain), modern Android components, and Firebase authentication.

✨ Features
✅ Core

Authentication: Login / Sign up with Firebase Auth

Home Experience

Meal of the Day

“For You” meals

Browse Categories & Countries

Recipe Details screen (SafeArgs navigation with MealsItem)

Favorites: Save meals to favorites ❤️

Plan: Build and manage your meal plan 📅

Profile

Backup & Restore

Logout flow

Language switch (Arabic / English)

Dark mode toggle 🌙

🌐 Network-aware (Online / Offline)

Real-time network tracking using ConnectivityObserver

Background sync of pending actions when connection returns (RxJava)

🎨 UI/UX

Material Design components

Custom snackbars (AppSnack) with:

Success / Error / Info states

Dark / Light theming support

Rounded + stroked background styling

🧱 Tech Stack

Language: Java (UI layer)

Architecture: MVP + Repository pattern (Data/Domain separation)

Navigation: Jetpack Navigation Component (Root/Auth/Main graphs)

Reactive: RxJava3

Image Loading: Glide

Auth: Firebase Authentication

Local Persistence: Room database (AppDatabase)

Preferences: SharedPreferences (SharedPreferencesHelper, SessionManager)

UI: Material Components (Material3 buttons, dialogs, cards)

🗺️ Navigation Graphs

Project navigation is split into:

root_graph → Splash → routes to Auth/Main

auth_graph → onboarding → login/signup → main

main_graph → home/search/favorites/plan/profile + listing/details

📂 Project Structure (High-level)
com.aalaa.foodplanner
│
├── ui
│   ├── home
│   ├── profile
│   ├── authentication
│   ├── common (AppSnack, LanguageHelper, ...)
│   └── ...
│
├── data
│   ├── repository
│   ├── datasource (remote/local)
│   ├── network (ConnectivityObserver)
│   └── firebase (FirebaseModule)
│
├── domain
│   ├── models
│   ├── repository interfaces
│   └── usecase (SyncPolicy)
│
└── datasource
    └── db (Room - AppDatabase, DAO)
