/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.feature.developer.ui.DeveloperUiState

@Composable
internal fun ServicesSection(
    uiState: DeveloperUiState,
    onGenerateServices: (Int) -> Unit = {},
    onDeleteAllServices: () -> Unit = {},
) {
    var showGenerateMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Services in Vault: ${uiState.servicesCount}",
            style = MdtTheme.typo.base.semiBold,
            color = MdtTheme.color.tertiary,
            modifier = Modifier
                .padding(top = 24.dp, bottom = 12.dp, start = 18.dp)
                .fillMaxWidth(),
        )

        Box(
            contentAlignment = Alignment.TopEnd,
        ) {
            OptionEntry(
                title = "Generate Services",
                icon = MdtIcons.Refresh,
                onClick = { showGenerateMenu = true },
            )

            DropdownMenu(
                expanded = showGenerateMenu,
                onDismissRequest = { showGenerateMenu = false },
                offset = DpOffset(x = (-16).dp, y = 0.dp),
            ) {
                listOf(1, 10, 100, 1000).forEach { count ->
                    DropdownMenuItem(
                        text = { Text("$count service${if (count > 1) "s" else ""}") },
                        onClick = {
                            showGenerateMenu = false
                            onGenerateServices(count)
                        },
                    )
                }
            }
        }

        OptionEntry(
            title = "Delete all services",
            icon = MdtIcons.Delete,
            iconTint = MdtTheme.color.error,
            titleColor = MdtTheme.color.error,
            onClick = { onDeleteAllServices() },
        )
    }
}