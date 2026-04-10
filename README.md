# 🍽️ MealMate

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Java-orange)
![Architecture](https://img.shields.io/badge/Architecture-MVP-purple)
![Reactive](https://img.shields.io/badge/RxJava3-Enabled-red)
![Database](https://img.shields.io/badge/Local-Room-blue)
![Backend](https://img.shields.io/badge/Backend-Firebase-yellow)
![License](https://img.shields.io/badge/License-MIT-gold)

**Meal Discovery & Planning App for Android**

FoodPlanner (MealMate) is a full-featured Android application that allows users to discover meals, explore categories and countries, save favorites, and build personalized meal plans — with offline-first support and smart synchronization.

---

## 📚 Table of Contents
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Data Flow](#-data-flow)
- [Offline & Sync](#-offline--sync)
- [App Flow](#-app-flow)
- [Setup](#️-setup)
- [API](#-api)
- [Challenges & Solutions](#️-challenges)
- [Future Improvements](#-future-improvements)
- [License](#-license)

---

## ✨ Features

### 🔐 Authentication
- Firebase Authentication (Login / Sign up)
- Persistent session management
- Guest mode support

### 🍽 Meal Discovery
- Meal of the Day
- “For You” personalized meals
- Browse by categories & countries
- Full meal details (ingredients / steps / video)
- Smart search functionality

### ❤️ Favorites
- Add / remove favorite meals
- Offline support using Room

### 📅 Meal Planning
- Weekly meal planner
- Add/remove meals per day
- Organized plan structure

### 🌐 Offline & Sync
- Local Room database
- Connectivity observer
- Pending actions system
- Auto-sync when internet returns

### 🎨 UI / UX
- Material Design
- Dark / Light mode
- Multi-language (Arabic / English)
- Smooth and clean UI

---

## 📱 Screenshots

| Splash | Onboarding | Login |
|---|---|---|
| ![](screenshots/splash.png) | ![](screenshots/onboarding.png) | ![](screenshots/login.png) |

| Home Dark | Home Light | Search |
|---|---|---|
| ![](screenshots/home_dark.png) | ![](screenshots/home.png) | ![](screenshots/search.png) |

| Meal Details | Favorites | Plans |
|---|---|---|
| ![](screenshots/details.png) | ![](screenshots/favorites.png) | ![](screenshots/plans.png) |

| Profile |
|---|
| ![](screenshots/profile.png) |

---

## 🏗 Architecture

```mermaid
flowchart TD
    UI[UI Layer] --> DOMAIN[Domain Layer]
    DOMAIN --> DATA[Data Layer]
    DATA --> ROOM[Room Database]
    DATA --> FIREBASE[Firebase]
    DATA --> API[Remote API]
