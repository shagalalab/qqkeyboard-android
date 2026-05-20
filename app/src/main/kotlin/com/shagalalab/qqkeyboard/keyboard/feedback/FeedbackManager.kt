package com.shagalalab.qqkeyboard.keyboard.feedback

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.shagalalab.qqkeyboard.keyboard.preferences.KeyboardPreferences

class FeedbackManager(private val context: Context) {

    private val preferences = KeyboardPreferences(context)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun playKeyPressSound() {
        if (preferences.soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
        }
    }

    fun playKeyPressVibration() {
        if (preferences.vibrationEnabled && vibrator.hasVibrator()) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    KEY_PRESS_VIBRATION_DURATION,
                    preferences.vibrationStrength.amplitude
                )
            )
        }
    }

    fun playKeyPressFeedback() {
        playKeyPressSound()
        playKeyPressVibration()
    }

    fun playBackspaceSound() {
        if (preferences.soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE)
        }
    }

    fun playBackspaceVibration() {
        if (preferences.vibrationEnabled && vibrator.hasVibrator()) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    BACKSPACE_VIBRATION_DURATION,
                    preferences.vibrationStrength.amplitude
                )
            )
        }
    }

    fun playBackspaceFeedback() {
        playBackspaceSound()
        playBackspaceVibration()
    }

    fun playSpacebarSound() {
        if (preferences.soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR)
        }
    }

    fun playSpacebarFeedback() {
        playSpacebarSound()
        playKeyPressVibration()
    }

    fun playReturnSound() {
        if (preferences.soundEnabled) {
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN)
        }
    }

    fun playReturnFeedback() {
        playReturnSound()
        playKeyPressVibration()
    }

    companion object {
        private const val KEY_PRESS_VIBRATION_DURATION = 30L
        private const val BACKSPACE_VIBRATION_DURATION = 40L
    }
}
