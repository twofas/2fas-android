/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.modal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.checked.CheckIcon
import com.twofasapp.core.design.foundation.preview.PreviewTheme

data class SelectOption(
    val name: String,
    val key: String,
) {
    companion object {
        val None = SelectOption("", "")
    }
}

@Composable
fun ModalList(
    onDismissRequest: () -> Unit,
    headerText: String? = null,
    headerProperties: ModalHeaderProperties = ModalHeaderProperties(),
    header: @Composable (ColumnScope.((() -> Unit) -> Unit) -> Unit)? = {
        ModalHeader(
            titleText = headerText,
            showDragHandle = headerProperties.showDragHandle,
            showCloseButton = headerProperties.showCloseButton,
            onCloseClick = onDismissRequest,
        )
    },
    selected: SelectOption?,
    options: List<SelectOption>,
    onSelectChanged: (SelectOption) -> Unit = {},
) {
    Modal(
        onDismissRequest = onDismissRequest,
        headerText = headerText,
        header = header,
    ) { dismissAction ->
        Content(
            selected = selected,
            options = options,
            onSelectChanged = {
                dismissAction { onSelectChanged(it) }
            },
        )
    }
}

@Composable
private fun Content(
    selected: SelectOption?,
    options: List<SelectOption>,
    onSelectChanged: (SelectOption) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        options.forEach { option ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("modalListOption_${option.key}")
                        .clickable { onSelectChanged(option) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = option.name,
                        style = MdtTheme.typo.sm.semiBold,
                        modifier = Modifier.weight(1f),
                    )

                    CheckIcon(
                        checked = option.key == selected?.key,
                        size = 24.dp,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val options = buildList {
        repeat(5) {
            add(SelectOption(key = it.toString(), name = "Option $it"))
        }
    }

    PreviewTheme {
        Content(
            options = options,
            selected = options.first(),
        )
    }
}