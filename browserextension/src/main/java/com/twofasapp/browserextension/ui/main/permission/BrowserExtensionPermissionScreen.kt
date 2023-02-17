package com.twofasapp.browserextension.ui.main.permission

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.twofasapp.browserextension.ui.main.RequestPermission
import com.twofasapp.design.compose.ButtonHeight
import com.twofasapp.design.compose.ButtonShape
import com.twofasapp.design.compose.ButtonTextColor
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.navigation.SettingsDirections
import com.twofasapp.navigation.SettingsRouter
import com.twofasapp.resources.R
import org.koin.androidx.compose.get

@Composable
internal fun BrowserExtensionPermissionScreen(
    router: SettingsRouter = get(),
) {
    // Launched only on >= TIRAMISU
    var askForPermission by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Toolbar(title = stringResource(id = R.string.browser__browser_extension)) {
                router.navigate(SettingsDirections.GoBack)
            }
        }
    ) { padding ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
        ) {
            val (content, pair) = createRefs()

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(parent.top)
                        bottom.linkTo(pair.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.browser_extension_permission_image),
                    contentDescription = null,
                    modifier = Modifier
                        .height(130.dp)
                        .offset(y = (-16).dp)
                )

                Text(
                    text = stringResource(id = R.string.browser__push_notifications_title),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )

                Text(
                    text = stringResource(id = R.string.browser__push_notifications_content),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }

            Button(
                onClick = { askForPermission = true },
                shape = ButtonShape(),
                modifier = Modifier
                    .height(ButtonHeight())
                    .constrainAs(pair) {
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }) {
                Text(text = stringResource(id = R.string.commons__continue).uppercase(), color = ButtonTextColor())
            }

            if (askForPermission) {
                RequestPermission(
                    permission = Manifest.permission.POST_NOTIFICATIONS,
                    onGranted = { router.navigateBack() },
                    onDismiss = {
                        askForPermission = false
                        router.navigateBack()
                    },
                    rationaleTitle = stringResource(id = R.string.browser__push_notifications_title),
                    rationaleText = stringResource(id = R.string.browser__push_notifications_content),
                )
            }
        }
    }
}