# CLAUDE.md — ZenTrack Meditation App

## Project Overview
ZenTrack is an Android meditation app built with Jetpack Compose + MVVM architecture.
Core features: meditation timer with configurable duration, session history log, total meditation time dashboard.

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose with Material3
- **Architecture:** MVVM (View → ViewModel → Repository → Data Source)
- **DI:** Hilt with KSP (NOT kapt)
- **Database:** Room (local SQLite)
- **Async:** Kotlin Coroutines + Flow
- **Navigation:** Navigation Compose 2.8+ with type-safe routes (@Serializable data objects/classes)
- **State:** StateFlow for UI state, SharedFlow (replay=0) for one-shot events
- **Min SDK:** 26 | **Target/Compile SDK:** 35

## Package Structure
```
com.zentrack.app/
├── ZenTrackApp.kt                  // @HiltAndroidApp
├── MainActivity.kt                 // @AndroidEntryPoint, hosts NavHost
├── di/
│   └── DatabaseModule.kt           // @Module providing Room DB + DAOs
├── data/
│   ├── local/
│   │   ├── ZenTrackDatabase.kt     // @Database
│   │   ├── MeditationDao.kt        // @Dao interface
│   │   └── entity/
│   │       └── MeditationEntity.kt // @Entity
│   └── repository/
│       └── MeditationRepository.kt // Single source of truth
├── domain/
│   └── model/
│       └── Meditation.kt           // Domain model + mapper extensions
├── ui/
│   ├── navigation/
│   │   └── ZenTrackNavGraph.kt     // NavHost with type-safe routes
│   ├── theme/
│   │   ├── Theme.kt
│   │   ├── Color.kt
│   │   └── Type.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   └── HomeUiState.kt
│   ├── timer/
│   │   ├── TimerScreen.kt
│   │   ├── TimerViewModel.kt
│   │   └── TimerUiState.kt
│   └── history/
│       ├── HistoryScreen.kt
│       ├── HistoryViewModel.kt
│       └── HistoryUiState.kt
```

## Architecture Rules (STRICT)

### State Management
- Every ViewModel exposes `val uiState: StateFlow<XUiState>` (immutable, via `.asStateFlow()`)
- NEVER expose `MutableStateFlow` publicly
- Use `_uiState.update { it.copy(...) }` for atomic state updates
- UI state is a single data class per screen (e.g., `HomeUiState`) with all fields needed to render

### One-Shot Events
- Use `MutableSharedFlow<UiEvent>(replay = 0, extraBufferCapacity = 1)` for navigation, toasts, snackbars
- Collect events in `LaunchedEffect(Unit)` inside the Screen composable
- NEVER use StateFlow for events — it re-emits on configuration change

### View Layer (Compose)
- Use `collectAsStateWithLifecycle()` (NOT `collectAsState()`) to collect flows
- Every Screen composable has two parts:
    1. `XScreen()` — connects to ViewModel, collects state, handles events
    2. `XContent()` — pure UI function that takes state + lambdas, no ViewModel reference (previewable)
- Navigation lambdas are passed down as parameters, screens never hold `navController`
- NEVER pass Context or Android framework references to ViewModels

### Data Layer
- Room entities live in `data/local/entity/` with `@Entity` annotation
- Domain models live in `domain/model/` with NO Android annotations
- Mapper extension functions (`toDomain()`, `toEntity()`) live in the domain model file
- Repository takes DAO via `@Inject constructor`, exposes `Flow<DomainModel>` (never Entity)
- Room DAO returns `Flow<List<Entity>>` for observable queries, `suspend fun` for writes
- Store timestamps as `Long` (epoch millis), convert to display format in UI layer only

### Dependency Injection
- App class: `@HiltAndroidApp`
- Activity: `@AndroidEntryPoint`
- ViewModels: `@HiltViewModel` + `@Inject constructor`
- Repository: `@Inject constructor` (no @Provides needed, Hilt auto-resolves)
- Database + DAOs: provided via `@Module @InstallIn(SingletonComponent::class) object DatabaseModule`
- Use `@Singleton` for Database and DAO providers
- Use `@ApplicationContext` when Context is needed (never Activity context)

### Navigation
- Define routes as `@Serializable data object` (no args) or `@Serializable data class` (with args)
- Use `composable<RouteType> { }` in NavHost
- Pass navigation args via route properties, read in ViewModel via `SavedStateHandle`

## Data Model

### MeditationEntity (Room table: "meditations")
| Column | Type | Notes |
|--------|------|-------|
| id | Long | PrimaryKey, autoGenerate |
| duration_minutes | Int | Session length in minutes |
| completed_at | Long | Epoch millis timestamp |

### Key Queries
- `getAllMeditations()`: `Flow<List<MeditationEntity>>` ORDER BY completed_at DESC
- `getTotalMinutes()`: `Flow<Int>` using `COALESCE(SUM(duration_minutes), 0)`
- `insert()`: `suspend fun`
- `delete()`: `suspend fun`

## Features Spec

### Home Screen
- Shows total meditation minutes and session count in a summary card
- Duration selector: chips for 5, 10, 15, 20, 30 minutes
- "Comenzar Meditación" button → validates > 0, navigates to TimerScreen
- "Ver historial completo" button → navigates to HistoryScreen

### Timer Screen
- Circular progress indicator (240.dp) showing remaining time
- MM:SS display in center
- Controls: Start/Pause/Resume/Reset depending on state
- On completion: auto-saves session via repository, shows "¡Completada!"
- Timer uses `delay(1_000L)` in `viewModelScope.launch`, cancellable via `Job`

### History Screen
- LazyColumn listing all past sessions (date + duration)
- Swipe-to-delete or delete button per item
- Total stats at the top

## Build Commands
```bash
./gradlew assembleDebug        # Build debug APK
./gradlew test                 # Run unit tests
./gradlew connectedAndroidTest # Run instrumented tests
./gradlew lint                 # Run lint checks
```

## Dependencies (add to module-level build.gradle.kts)
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

// Key dependencies:
// Compose BOM: androidx.compose:compose-bom:2024.12.01
// Material3: androidx.compose.material3:material3
// ViewModel Compose: androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7
// Lifecycle Runtime Compose: androidx.lifecycle:lifecycle-runtime-compose:2.8.7
// Navigation Compose: androidx.navigation:navigation-compose:2.8.5
// Hilt Android: com.google.dagger:hilt-android:2.52
// Hilt Compiler (KSP): com.google.dagger:hilt-compiler:2.52
// Hilt Navigation Compose: androidx.hilt:hilt-navigation-compose:1.2.0
// Room Runtime: androidx.room:room-runtime:2.6.1
// Room KTX: androidx.room:room-ktx:2.6.1
// Room Compiler (KSP): androidx.room:room-compiler:2.6.1
// Coroutines Android: org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0
// Kotlinx Serialization: org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3
```

## Version Catalog (libs.versions.toml)
```toml
[versions]
agp = "8.7.3"
kotlin = "2.0.21"
hilt = "2.52"
ksp = "2.0.21-1.0.27"

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

## Code Style
- Spanish for user-facing strings, English for code (variable names, comments in code)
- Use `sealed interface` for event types (not sealed class)
- Use `data object` for events without parameters
- Prefer `when` exhaustive matching over if/else chains
- Format durations as "MM:SS" using `"%02d:%02d".format(minutes, seconds)`
- Format dates using `java.time.Instant.ofEpochMilli()` + `DateTimeFormatter` for display

## Common Pitfalls to Avoid
- Do NOT use `kapt` — use `ksp` for all annotation processing
- Do NOT use `LiveData` — use `StateFlow` / `SharedFlow`
- Do NOT use `collectAsState()` — use `collectAsStateWithLifecycle()`
- Do NOT put business logic in Composables
- Do NOT reference `navController` inside Screen content composables
- Do NOT use `Channel` for UI events — use `SharedFlow(replay=0)`
- Do NOT forget `@AndroidEntryPoint` on MainActivity
- Do NOT forget to register `ZenTrackApp` in AndroidManifest.xml `android:name`
- Room `SUM()` returns null on empty table — always wrap with `COALESCE(..., 0)`
