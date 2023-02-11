package com.twofasapp.services.ui.deleteservice

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.design.compose.ButtonHeight
import com.twofasapp.design.compose.ButtonShape
import com.twofasapp.design.compose.ButtonTextColor
import com.twofasapp.design.compose.Toolbar
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.navigation.ServiceRouter
import com.twofasapp.resources.R
import com.twofasapp.services.ui.ServiceViewModel
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DeleteServiceScreen(
    viewModel: ServiceViewModel = getViewModel(),
    router: ServiceRouter = get()
) {

    val service = viewModel.uiState.collectAsState().value.service
    val activity = LocalContext.current as? Activity

    Scaffold(
        topBar = {
            Toolbar(
                title = "",
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            ) { router.navigateBack() }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = CenterHorizontally,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete_trash),
                    contentDescription = null,
                    modifier = Modifier.height(150.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(id = R.string.delete_service_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h6.copy(fontSize = 22.sp),
                    color = MaterialTheme.colors.textPrimary,
                )

                Text(
                    text = service.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.h6.copy(fontSize = 18.sp),
                    color = MaterialTheme.colors.textPrimary,
                )

                Text(
                    text = stringResource(id = R.string.delete_service_msg, service.name),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.textPrimary,
                )
            }

            Button(
                onClick = {
                    viewModel.delete()
                    activity?.finish()
                },
                shape = ButtonShape(),
                modifier = Modifier.height(ButtonHeight())

            ) {
                Text(text = stringResource(id = R.string.delete_service_cta), color = ButtonTextColor())
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { router.navigateBack() },
                shape = ButtonShape(),
                modifier = Modifier.height(ButtonHeight())
            ) {
                Text(text = stringResource(id = R.string.commons__cancel))
            }
        }
    }
}

@Preview
@Composable
fun DeleteServiceScreenPreview() {
    DeleteServiceScreen()
}
