package com.shagalalab.qqkeyboard

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation 3 destinations hosted by [MainActivity]. */
sealed interface MainNavKey : NavKey

@Serializable data object Home : MainNavKey
@Serializable data object About : MainNavKey
@Serializable data object Help : MainNavKey
@Serializable data object TestKeyboard : MainNavKey
