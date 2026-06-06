package com.shagalalab.qqkeyboard.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            onSelect = onSelect,
            modifier = Modifier.fillMaxSize().padding(contentPadding),
        )
    }
}
