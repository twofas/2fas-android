package com.twofasapp.feature.trash.ui.dispose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.TextButton
import com.twofasapp.core.design.foundation.checked.Switch
import com.twofasapp.core.design.foundation.image.Image
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.theme.RoundedShape12
import com.twofasapp.feature.trash.R
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DisposeScreen(
    viewModel: DisposeViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContent(
        uiState = uiState,
        onDeleteClick = {
            viewModel.delete()
            navigateBack()
        },
        onFinish = navigateBack,
    )
}

@Composable
private fun ScreenContent(
    uiState: DisposeUiState,
    onDeleteClick: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var checked by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = "") }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = uiState.serviceName,
                    color = MdtTheme.color.onSurface,
                    style = MdtTheme.typo.regular.xl2,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = TwLocale.strings.disposeBody1,
                    color = MdtTheme.color.onSurface,
                    style = MdtTheme.typo.regular.sm,
                    textAlign = TextAlign.Center,
                )

                Image(
                    painter = painterResource(id = R.drawable.img_dispose),
                    modifier = Modifier.height(120.dp),
                )

                Text(
                    text = TwLocale.strings.disposeBody2 + "\n" + TwLocale.strings.disposeBody3.format(
                        uiState.serviceName,
                        uiState.serviceName,
                    ),
                    color = MdtTheme.color.onSurface,
                    style = MdtTheme.typo.regular.sm,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedShape12)
                    .background(MdtTheme.color.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = checked,
                        onCheckedChange = { checked = checked.not() },
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = TwLocale.strings.disposeConfirm,
                        color = MdtTheme.color.onSurface,
                        style = MdtTheme.typo.regular.base,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    text = TwLocale.strings.disposeCta,
                    onClick = onDeleteClick,
                    enabled = checked,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(text = TwLocale.strings.commonCancel, onClick = onFinish)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ScreenContent(
        DisposeUiState(
            serviceName = "ServiceName",
        ),
    )
}