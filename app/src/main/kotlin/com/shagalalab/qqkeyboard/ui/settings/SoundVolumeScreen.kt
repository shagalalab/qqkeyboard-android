package com.shagalalab.qqkeyboard.ui.settings

import android.content.Context
import android.media.AudioManager
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
import com.shagalalab.qqkeyboard.keyboard.model.SoundVolume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundVolumeScreen(
    selectedValue: SoundVolume,
    onSelect: (SoundVolume) -> Unit,
    onBackClick: () -> Unit,
) {
    val options = listOf(
        SelectionOption(SoundVolume.QUIET, stringResource(R.string.settings_sound_volume_quiet)),
        SelectionOption(SoundVolume.MEDIUM, stringResource(R.string.settings_sound_volume_medium)),
        SelectionOption(SoundVolume.LOUD, stringResource(R.string.settings_sound_volume_loud)),
    )

    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_sound_volume)) },
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
            onSelect = { volume ->
                audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volume.volume)
                onSelect(volume)
            },
            modifier = Modifier.padding(contentPadding),
        )
    }
}
