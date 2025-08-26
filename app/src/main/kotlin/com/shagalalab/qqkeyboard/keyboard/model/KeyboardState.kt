package com.shagalalab.qqkeyboard.keyboard.model

enum class LayoutType {
    LATIN,
    CYRILLIC
}

enum class ShiftState {
    OFF,
    ON,
    CAPS_LOCK
}

enum class KeyboardMode {
    ALPHABETIC,
    NUMERIC,
    EMOJI
}

data class KeyboardState(
    val layoutType: LayoutType = LayoutType.LATIN,
    val shiftState: ShiftState = ShiftState.OFF,
    val keyboardMode: KeyboardMode = KeyboardMode.ALPHABETIC,
    val isShiftLocked: Boolean = false
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

    fun switchLayout(): KeyboardState {
        return copy(
            layoutType = when (layoutType) {
                LayoutType.LATIN -> LayoutType.CYRILLIC
                LayoutType.CYRILLIC -> LayoutType.LATIN
            }
        )
    }

    fun switchMode(mode: KeyboardMode): KeyboardState {
        return copy(keyboardMode = mode)
    }

    fun resetShift(): KeyboardState {
        return if (shiftState == ShiftState.ON && !isShiftLocked) {
            copy(shiftState = ShiftState.OFF)
        } else {
            this
        }
    }
}
