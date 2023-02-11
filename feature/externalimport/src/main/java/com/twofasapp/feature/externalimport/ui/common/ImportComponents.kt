package com.twofasapp.feature.externalimport.ui.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwButton
import com.twofasapp.designsystem.common.TwTopAppBar

@Composable
internal fun ImportFileScaffold(
    title: String,
    image: Painter,
    description: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    Scaffold(
        topBar = { TwTopAppBar(title) }
    ) { padding ->

        Column(
            Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.height(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                description()
            }

            Box(modifier = Modifier.align(CenterHorizontally)) {
                actions()
            }
        }
    }
}

@Composable
internal fun ImportDescription(text: String) {
    Text(
        text = text,
        style = TwTheme.typo.body1,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 32.dp)
    )
}

@Composable
internal fun ImportFilePickerButton(
    text: String,
    fileType: String = "*/*",
    onFilePicked: (Uri) -> Unit = {},
) {
    val activity = LocalContext.current as Activity

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { onFilePicked.invoke(it) }
    }

    TwButton(
        text = text,
        onClick = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                .apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = fileType
                }

            try {
                filePickerLauncher.launch(intent)
            } catch (e: Exception) {

                try {
                    intent.action = Intent.ACTION_GET_CONTENT
                    filePickerLauncher.launch(intent)
                } catch (e: Exception) {
                    Toast.makeText(activity, "System error! Could not launch file provider!", Toast.LENGTH_SHORT).show()
                }
            }
        },
        modifier = Modifier
            .padding(bottom = 24.dp, top = 8.dp)
            .height(48.dp)
    )
}