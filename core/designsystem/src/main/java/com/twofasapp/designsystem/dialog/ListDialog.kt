package com.twofasapp.designsystem.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
fun ListDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    options: List<String>,
    onOptionSelected: (Int) -> Unit = {},
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        Column(Modifier.selectableGroup()) {
            options.forEachIndexed { index, text ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable { onOptionSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TwTheme.color.onSurfacePrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = DialogPadding)

                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ListDialog(
        onDismissRequest = { },
        options = listOf("Test 1", "Test 2")
    )
}