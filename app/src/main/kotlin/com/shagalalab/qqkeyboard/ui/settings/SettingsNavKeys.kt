package com.shagalalab.qqkeyboard.ui.settings

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation 3 destinations hosted by SettingsActivity. */
sealed interface SettingsNavKey : NavKey

@Serializable data object Settings : SettingsNavKey
@Serializable data object ThemeSelection : SettingsNavKey
@Serializable data object HeightSelection : SettingsNavKey
@Serializable data object VibrationSelection : SettingsNavKey
@Serializable data object TopRowSelection : SettingsNavKey
