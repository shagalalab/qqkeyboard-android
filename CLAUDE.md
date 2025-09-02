# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

QqKeyboard is an Android virtual keyboard application built with Jetpack Compose. It provides dual-language support for Latin (Q) and Cyrillic (Қ) Karakalpak keyboard layouts, with emoji support, smart typing features, and customizable feedback.

## Development Commands

### Build and Development
- `./gradlew build` - Build the entire project
- `./gradlew assembleDebug` - Build debug APK
- `./gradlew assembleRelease` - Build release APK
- `./gradlew installDebug` - Install debug build on connected device
- `./gradlew clean` - Clean build artifacts

### Testing
- `./gradlew test` - Run unit tests for all variants
- `./gradlew testDebugUnitTest` - Run debug unit tests only
- `./gradlew connectedAndroidTest` - Run instrumentation tests on connected device
- `./gradlew connectedDebugAndroidTest` - Run debug instrumentation tests

### Code Quality
- `./gradlew lint` - Run lint analysis on default variant
- `./gradlew lintDebug` - Run lint on debug variant
- `./gradlew lintFix` - Auto-fix lint issues where possible

## Architecture Overview

### Core Components

**QqKeyboardService** (`keyboard/service/QqKeyboardService.kt`)
- Main InputMethodService implementation that extends Android's input method framework
- Integrates Compose UI with Android's keyboard lifecycle
- Manages LifecycleOwner and SavedStateRegistryOwner for proper state management
- Initializes KeyboardViewModel and handles input connection setup

**KeyboardViewModel** (`keyboard/viewmodel/KeyboardViewModel.kt`)
- Central state management for keyboard functionality
- Handles all key press logic, layout switching, and smart features
- Manages shift states, auto-capitalization, and emoji tracking
- Implements double-space to period conversion and caps lock via double-tap shift

**QqKeyboard** (`keyboard/compose/QqKeyboard.kt`)
- Main Compose UI component that renders the keyboard interface
- Orchestrates different keyboard modes (alphabetic, numeric, symbolic, emoji)
- Handles layout switching and visual state transitions

### State Management

**KeyboardState** (`keyboard/model/KeyboardState.kt`)
- Immutable state class representing current keyboard configuration
- Tracks layout type (Latin/Cyrillic), mode (alphabetic/numeric/symbolic), shift state, caps lock, and emoji popup state

**KeyboardPreferences** (`keyboard/preferences/KeyboardPreferences.kt`)
- SharedPreferences wrapper for persistent keyboard settings
- Stores last used layout, recent emojis, and user preferences

### UI Components

**KeyButton** (`keyboard/compose/KeyButton.kt`)
- Individual key button component with haptic feedback and long-press support
- Handles visual states and click animations

**KeyboardLayout** (`keyboard/compose/KeyboardLayout.kt`)
- Layout component that arranges keys in rows
- Manages different keyboard layouts (QWERTY variations for different languages)

**EmojiLayout** (`keyboard/compose/EmojiLayout.kt`)
- Specialized component for emoji picker interface
- Categories emojis and tracks recent usage

### Data Layer

**KeyboardMappings** (`keyboard/data/KeyboardMappings.kt`)
- Static data defining key layouts for different languages and modes
- Contains Latin and Cyrillic keyboard layouts, numeric layouts, and symbol layouts

**EmojiData** (`keyboard/data/EmojiData.kt`)
- Emoji categories and definitions
- Organized by categories (people, nature, objects, etc.)

### Smart Features

- **Double-space to period**: Automatically converts double-space to period-space
- **Auto-capitalization**: Capitalizes after sentence-ending punctuation
- **Caps lock**: Double-tap shift for caps lock mode  
- **Smart deletion**: Handles emoji deletion correctly using grapheme cluster detection
- **Layout persistence**: Remembers last used language layout
- **Recent emoji tracking**: Maintains list of frequently used emojis

### Architecture Patterns

- **MVVM**: ViewModel manages business logic, Compose UI observes state changes
- **Unidirectional data flow**: UI sends events to ViewModel, ViewModel updates state
- **Composition over inheritance**: Compose UI built with reusable components
- **State hoisting**: State managed at appropriate component levels

## Package Structure

```
com.shagalalab.qqkeyboard/
├── keyboard/
│   ├── compose/          # Compose UI components
│   ├── data/            # Static data and mappings
│   ├── feedback/        # Haptic and audio feedback
│   ├── model/           # Data classes and enums
│   ├── preferences/     # Settings and persistence
│   ├── service/         # InputMethodService implementation
│   ├── theme/           # Keyboard-specific theming
│   ├── utils/           # Utility classes (emoji handling)
│   └── viewmodel/       # Business logic and state management
├── ui/
│   ├── settings/        # Settings screens
│   └── theme/           # App-wide theme and styling
└── MainActivity.kt      # Main launcher activity
```

## Key Technical Details

- **Target SDK**: 36 (Android 15)
- **Min SDK**: 26 (Android 8.0)
- **Kotlin Version**: 2.0.21
- **Compose BOM**: 2024.09.00
- **Build System**: Gradle with Kotlin DSL
- **Testing**: JUnit 4, AndroidX Test, Espresso

## Development Notes

- The keyboard supports two primary layouts: Latin (QQ) and Cyrillic (ҚҚ) for Karakalpak language input
- All UI is built with Jetpack Compose, no XML layouts used
- State management follows Compose best practices with `mutableStateOf` and state hoisting
- The InputMethodService integration requires careful lifecycle management to work properly with Compose
- Emoji handling uses Unicode grapheme cluster detection for proper deletion behavior
- The app includes a test interface in MainActivity for keyboard testing during development