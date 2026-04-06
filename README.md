# 🍽️ RecipeDiscover — Android App

A production-ready **Recipe Finder** Android application built with modern Android
development best practices.

---

## 🏗️ Architecture

```
com.recipefinder.app
├── core/                      # Shared utilities, constants
│   ├── constants/AppConstants.kt
│   └── util/
│       ├── Resource.kt        # Loading / Success / Error sealed class
│       └── Extensions.kt     # Flow helpers, formatters
│
├── data/                      # Data layer (framework-dependent)
│   ├── local/
│   │   ├── dao/               # RecipeDao, FavoriteDao
│   │   ├── database/          # Room DB + TypeConverters
│   │   └── entity/            # RecipeEntity, FavoriteEntity
│   ├── remote/
│   │   ├── api/               # Retrofit service interface
│   │   ├── dto/               # JSON response models
│   │   └── interceptor/       # MockInterceptor (swap for real API)
│   ├── mapper/                # DTO ↔ Entity ↔ Domain mappers
│   └── repository/            # RecipeRepositoryImpl (offline-first)
│
├── domain/                    # Domain layer (pure Kotlin, zero Android deps)
│   ├── model/                 # Recipe, RecipeFilter, Difficulty, SortOrder
│   ├── repository/            # RecipeRepository interface
│   └── usecase/               # One use-case per action (6 total)
│
├── presentation/              # Presentation layer (Compose + ViewModel)
│   ├── navigation/            # NavGraph, Screen sealed class
│   ├── home/                  # HomeScreen + ViewModel + UiState
│   ├── detail/                # RecipeDetailScreen + ViewModel + UiState
│   ├── favorites/             # FavoritesScreen + ViewModel + UiState
│   ├── search/                # SearchScreen + ViewModel + UiState
│   └── components/            # Shared Composables (RecipeCard, SearchBar…)
│
├── di/                        # Hilt DI modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
│
└── ui/theme/                  # Material 3 theming
    ├── Color.kt               # Full brand palette (light + dark)
    ├── Theme.kt               # RecipeFinderTheme (dynamic color support)
    ├── Type.kt                # Typography scale
    └── Shape.kt               # Corner radius tokens
```

---

## 🚀 Getting Started

### 1. Clone & open in Android Studio
```
Android Studio Hedgehog (2023.1.1) or newer
```

### 2. Sync Gradle
The project uses a **version catalog** (`gradle/libs.versions.toml`).
Everything resolves automatically — no manual dependency management needed.

### 3. Run
The app ships with a **MockInterceptor** so it works completely offline
without any API key.

### Switching to real Spoonacular API
1. Register at https://spoonacular.com/food-api
2. Delete `MockInterceptor` from `NetworkModule.kt`
3. Add an `ApiKeyInterceptor` that appends `?apiKey=YOUR_KEY` to every request

---

## 🧩 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt 2.51 |
| Local DB | Room 2.6 |
| Networking | Retrofit 2.11 + OkHttp 4.12 |
| Image Loading | Coil 2.7 |
| Async | Coroutines + Flow |
| Navigation | Navigation Compose 2.8 |
| Build | Gradle 8.5 + Kotlin DSL + Version Catalog |

---

## ✨ Features

- **Home** — Responsive recipe grid with collapse-on-scroll title bar
- **Search** — Debounced search with history suggestions
- **Filters** — Difficulty (Easy / Medium / Hard) + Sort (Name, Cook Time, Likes)
- **Detail** — Full recipe with hero image, ingredients list, numbered steps
- **Favourites** — Room-backed, reactive, swipe-to-remove
- **Offline-first** — Cached recipes always available, errors surfaced as Snackbars
- **Dark mode** — Full Material You dynamic theming on Android 12+
- **Edge-to-edge** — True full-bleed layout with proper inset handling
- **Animations** — Shared-element card transitions, animated content switches

---

## 🔜 Next Iteration (planned)

- Pagination (Paging 3)
- Recipe categories / tags carousel
- Shared Element Transitions (Compose 1.7)
- Add-your-own recipe (local Room insert)
- Nutritional info charts
- Ingredient quantity scaler
