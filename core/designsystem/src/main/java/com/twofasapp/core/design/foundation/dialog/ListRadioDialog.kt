/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewText
import com.twofasapp.core.design.foundation.preview.PreviewTextLong
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.theme.DialogPadding

@Composable
fun ListRadioDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    body: String? = null,
    icon: Painter? = null,
    options: List<String>,
    selectedOption: String = "",
    selectedIndex: Int? = null,
    onOptionSelected: (Int, String) -> Unit = { _, _ -> },
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        contentScrollable = false,
        title = title,
        body = body,
        icon = icon,
    ) {
        LazyColumn(Modifier.selectableGroup()) {
            options.forEachIndexed { index, text ->
                item {
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
                                role = Role.RadioButton,
                            )
                            .padding(horizontal = DialogPadding),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            modifier = Modifier.testTag("listRadioDialogOption$index"),
                            selected = if (selectedIndex != null) {
                                index == selectedIndex
                            } else {
                                (text == selectedOption)
                            },
                            onClick = null,
                        )
                        Text(
                            text = text,
                            color = MdtTheme.color.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        ListRadioDialog(
            onDismissRequest = { },
            options = listOf("Test 1", "Test 2", "Test 3"),
            selectedOption = "Test 1",
            title = PreviewText,
            body = PreviewTextLong,
        )
    }
}