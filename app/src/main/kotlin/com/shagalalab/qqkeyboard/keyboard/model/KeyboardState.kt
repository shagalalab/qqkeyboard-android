package com.shagalalab.qqkeyboard.keyboard.model

enum class KeyboardLayout {
    LATIN,            // Alphabetic Latin (persistable)
    CYRILLIC,         // Alphabetic Cyrillic (persistable)
    NUMERIC,          // Numbers (reset to LATIN on restart)
    SYMBOLIC,         // Symbols (reset to LATIN on restart)
    NUMBER_PAD,       // Compact 4×4 numpad for Number/NumberDecimal fields
    NUMBER_PASSWORD,  // Centered PIN pad for NumberPassword fields
    PHONE             // Phone numpad with telecom letter hints
}

enum class TopRowMode {
    EXTRA_LETTERS,
    NUMBERS
}

/** Which language layout the keyboard starts in each time it opens. */
enum class DefaultLayoutMode {
    LAST_USED,  // Whatever language the user switched to most recently
    LATIN,
    CYRILLIC
}

enum class KeyboardHeight { SHORT, DEFAULT, LANDSCAPE }

enum class VibrationStrength(val amplitude: Int, val durationMs: Long) {
    LIGHT(80, 18L),
    MEDIUM(160, 30L),
    STRONG(250, 48L)
}

/**
 * Gain applied to key press sounds, as the linear 0f..1f scalar
 * `AudioManager.playSoundEffect` expects.
 *
 * Levels are spaced in decibels rather than linearly, because loudness is perceived
 * logarithmically; a linear ladder would feel lopsided. [MEDIUM] is the AOSP default effect gain
 * (`config_soundEffectVolumeDb` = -16 dB), so it matches how the keyboard sounded before this
 * setting existed. The system stream volume still applies on top of this value.
 *
 * [LOUD] is the full-scale maximum the API accepts rather than an evenly spaced ~8 dB step above
 * [MEDIUM]: on at least one device that step was barely audible, which suggests the effect stream
 * is compressed near the top of its range. If [LOUD] still sounds close to [MEDIUM] on some
 * device, no further tuning here can help — that would call for bundling our own samples and
 * playing them through a SoundPool instead.
 */
enum class SoundVolume(val volume: Float) {
    QUIET(0.06f),   // ~-24 dB
    MEDIUM(0.16f),  // ~-16 dB
    LOUD(1.0f)      // 0 dB — full scale
}

enum class ShiftState {
    OFF,
    ON,
    CAPS_LOCK
}

data class KeyboardState(
    val layout: KeyboardLayout = KeyboardLayout.LATIN,
    val shiftState: ShiftState = ShiftState.OFF,
    val isEmojiShown: Boolean = false,
    val isShiftLocked: Boolean = false,
) {
    val shouldShowUpperCase: Boolean
        get() = shiftState == ShiftState.ON || shiftState == ShiftState.CAPS_LOCK

    fun toggleShift(): KeyboardState {
        return when (shiftState) {
            ShiftState.OFF -> copy(shiftState = ShiftState.ON)
            ShiftState.ON -> if (isShiftLocked) copy(shiftState = ShiftState.CAPS_LOCK) else copy(shiftState = ShiftState.OFF)
            ShiftState.CAPS_LOCK -> copy(shiftState = ShiftState.OFF, isShiftLocked = false)
        }
    }

    fun doubleTapShift(): KeyboardState {
        return copy(shiftState = ShiftState.CAPS_LOCK, isShiftLocked = true)
    }

    fun switchLanguage(): KeyboardState {
        return copy(
            layout = when (layout) {
                KeyboardLayout.LATIN -> KeyboardLayout.CYRILLIC
                KeyboardLayout.CYRILLIC -> KeyboardLayout.LATIN
                // For numeric/symbolic, this method shouldn't be called directly
                // ViewModel handles language switching for non-language layouts
                else -> KeyboardLayout.LATIN
            }
        )
    }

    fun switchToLayout(newLayout: KeyboardLayout): KeyboardState {
        return copy(layout = newLayout)
    }

    fun resetShift(): KeyboardState {
        return if (shiftState == ShiftState.ON && !isShiftLocked) {
            copy(shiftState = ShiftState.OFF)
        } else {
            this
        }
    }

    fun enableAutoCapitalization(): KeyboardState {
        return if (shiftState == ShiftState.OFF) {
            copy(shiftState = ShiftState.ON)
        } else {
            this
        }
    }

    fun toggleEmojiPopup(): KeyboardState {
        return copy(isEmojiShown = !isEmojiShown)
    }
}
