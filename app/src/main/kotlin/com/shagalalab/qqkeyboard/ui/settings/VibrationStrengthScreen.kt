package com.shagalalab.qqkeyboard.ui.settings

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.shagalalab.qqkeyboard.R
import com.shagalalab.qqkeyboard.keyboard.model.VibrationStrength

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibrationStrengthScreen(
    selectedValue: VibrationStrength,
    onSelect: (VibrationStrength) -> Unit,
    onBackClick: () -> Unit,
) {
    val options = listOf(
        SelectionOption(VibrationStrength.LIGHT, stringResource(R.string.settings_vibration_light)),
        SelectionOption(VibrationStrength.MEDIUM, stringResource(R.string.settings_vibration_medium)),
        SelectionOption(VibrationStrength.STRONG, stringResource(R.string.settings_vibration_strong)),
    )

    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_vibration_intensity)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        RadioOptionList(
            options = options,
            selectedValue = selectedValue,
            onSelect = { strength ->
                vibrator.vibrate(VibrationEffect.createOneShot(strength.durationMs, strength.amplitude))
                onSelect(strength)
            },
            modifier = Modifier.padding(contentPadding),
        )
    }
}
