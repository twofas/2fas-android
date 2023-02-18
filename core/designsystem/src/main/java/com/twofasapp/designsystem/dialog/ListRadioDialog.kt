package com.twofasapp.designsystem.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@Composable
fun ListRadioDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (Int, String) -> Unit = { _, _ -> },
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = title,
    ) {

        Column(Modifier.selectableGroup()) {
            options.forEachIndexed { index, text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(index, text)
                                onDismissRequest()
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = DialogPadding),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null,
                        colors =  RadioButtonDefaults.colors(
                            selectedColor = TwTheme.color.primary,
                            unselectedColor = TwTheme.color.onSurfaceSecondary,
                        )
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}