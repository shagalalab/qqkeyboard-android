package com.shagalalab.qqkeyboard.keyboard.feedback

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences

class FeedbackManager(context: Context, prefs: KeyboardPreferences) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private val hasVibrator = vibrator.hasVibrator()

    private var soundEnabled = prefs.soundEnabled
    private var vibrationEnabled = prefs.vibrationEnabled
    private var vibrationStrength = prefs.vibrationStrength

    fun refreshSettings(prefs: KeyboardPreferences) {
        soundEnabled = prefs.soundEnabled
        vibrationEnabled = prefs.vibrationEnabled
        vibrationStrength = prefs.vibrationStrength
    }

    fun playKeyPressSound() {
        if (soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, SYSTEM_DEFAULT_VOLUME)
        }
    }

    fun playKeyPressVibration() {
        if (vibrationEnabled && hasVibrator) {
            val strength = vibrationStrength
            vibrator.vibrate(
                VibrationEffect.createOneShot(strength.durationMs, strength.amplitude)
            )
        }
    }

    fun playKeyPressFeedback() {
        playKeyPressSound()
        playKeyPressVibration()
    }

    fun playBackspaceSound() {
        if (soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE, SYSTEM_DEFAULT_VOLUME)
        }
    }

    fun playBackspaceVibration() {
        if (vibrationEnabled && hasVibrator) {
            val strength = vibrationStrength
            vibrator.vibrate(
                VibrationEffect.createOneShot(strength.durationMs + 10L, strength.amplitude)
            )
        }
    }

    fun playBackspaceFeedback() {
        playBackspaceSound()
        playBackspaceVibration()
    }

    fun playSpacebarSound() {
        if (soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR, SYSTEM_DEFAULT_VOLUME)
        }
    }

    fun playSpacebarFeedback() {
        playSpacebarSound()
        playKeyPressVibration()
    }

    fun playReturnSound() {
        if (soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN, SYSTEM_DEFAULT_VOLUME)
        }
    }

    fun playReturnFeedback() {
        playReturnSound()
        playKeyPressVibration()
    }

    private companion object {
        /**
         * Sentinel accepted by [AudioManager.playSoundEffect] meaning "use the device's default
         * effect volume" (derived from the framework's `config_soundEffectVolumeDb` resource), so
         * loudness is unchanged from the single-argument overload. We pass it explicitly because
         * the single-argument overload returns early when the system-wide touch sounds setting
         * (`Settings.System.SOUND_EFFECTS_ENABLED`) is off, which silently suppresses key sounds
         * even though the user enabled them in our own settings. The two-argument overload skips
         * that check.
         */
        const val SYSTEM_DEFAULT_VOLUME = -1f
    }
}
