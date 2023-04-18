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
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwImage
import com.twofasapp.designsystem.common.TwSwitch
import com.twofasapp.designsystem.common.TwTextButton
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.feature.trash.R
import com.twofasapp.locale.TwLocale
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DisposeRoute(
    navigateBack: () -> Unit,
    viewModel: DisposeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposeScreen(
        uiState = uiState,
        onDeleteClick = {
            viewModel.delete()
            navigateBack()
        },
        onFinish = navigateBack
    )
}

@Composable
private fun DisposeScreen(
    uiState: DisposeUiState,
    onDeleteClick: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var checked by remember { mutableStateOf(false) }

    Scaffold(topBar = { TwTopAppBar(titleText = "") }) { padding ->
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
                    color = TwTheme.color.onSurfacePrimary,
                    style = TwTheme.typo.h3,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = TwLocale.strings.disposeBody1,
                    color = TwTheme.color.onSurfacePrimary,
                    style = TwTheme.typo.body3,
                    textAlign = TextAlign.Center,
                )

                TwImage(
                    painter = painterResource(id = R.drawable.img_dispose),
                    modifier = Modifier.height(TwTheme.dimen.commonContentImageHeight),
                )

                Text(
                    text = TwLocale.strings.disposeBody2 + "\n" + TwLocale.strings.disposeBody3.format(uiState.serviceName, uiState.serviceName),
                    color = TwTheme.color.onSurfacePrimary,
                    style = TwTheme.typo.body3,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(TwTheme.shape.roundedDefault)
                    .background(TwTheme.color.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TwSwitch(
                        checked = checked,
                        onCheckedChange = { checked = checked.not() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = TwLocale.strings.disposeConfirm,
                        color = TwTheme.color.onSurfacePrimary,
                        style = TwTheme.typo.body1,
                        textAlign = TextAlign.Center,
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))
                TwButton(
                    text = TwLocale.strings.disposeCta,
                    onClick = onDeleteClick,
                    enabled = checked,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            TwTextButton(text = TwLocale.strings.commonCancel, onClick = onFinish)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DisposeScreen(
        DisposeUiState(
            serviceName = "ServiceName"
        )
    )
}