package com.twofasapp.externalimport.ui.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.design.compose.ButtonShape
import com.twofasapp.design.compose.ButtonTextColor
import com.twofasapp.design.compose.Toolbar

@Composable
fun ImportFileScaffold(
    title: String,
    image: Painter,
    description: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = { Toolbar(title = title) { onBackClick.invoke() } }
    ) { padding ->

        Column(
            Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.height(100.dp)
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
fun ImportDescription(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1.copy(lineHeight = 22.sp, fontSize = 17.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 32.dp)
    )
}

@Composable
fun ImportFilePickerButton(
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

    Button(
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
        shape = ButtonShape(),
        modifier = Modifier
            .padding(bottom = 24.dp, top = 8.dp)
            .height(48.dp)
    ) {
        Text(text = text.uppercase(), color = ButtonTextColor())
    }

}