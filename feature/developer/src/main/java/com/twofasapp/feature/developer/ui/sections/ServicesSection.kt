/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.ui.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.theme.RoundedShape16
import com.twofasapp.feature.developer.ui.DeveloperUiState

@Composable
internal fun ServicesSection(
    uiState: DeveloperUiState,
    onGenerateRandomServices: (Int) -> Unit = {},
    onGenerateSupportedServices: (Int?) -> Unit = {},
    onTrashServices: (Int?) -> Unit = {},
    onEmptyTrash: () -> Unit = {},
) {
    var showGenerateRandomMenu by remember { mutableStateOf(false) }
    var showGenerateSupportedMenu by remember { mutableStateOf(false) }
    var showTrashMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
    ) {
        OptionHeader(
            text = "Vault",
            contentPadding = OptionHeaderContentPaddingFirst,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Services",
                count = uiState.servicesCount,
                icon = MdtIcons.Home,
                tint = MdtTheme.color.primary,
            )

            StatCard(
                label = "In Trash",
                count = uiState.trashedServicesCount,
                icon = MdtIcons.Delete,
                tint = MdtTheme.color.tertiary,
            )
        }

        OptionHeader(
            text = "Actions",
        )

        Box(
            contentAlignment = Alignment.TopEnd,
        ) {
            OptionEntry(
                title = "Generate random services",
                subtitle = "Add random \"Service ######\" entries",
                icon = MdtIcons.Refresh,
                onClick = { showGenerateRandomMenu = true },
            )

            DropdownMenu(
                expanded = showGenerateRandomMenu,
                onDismissRequest = { showGenerateRandomMenu = false },
                offset = DpOffset(x = (-16).dp, y = 0.dp),
            ) {
                listOf(1, 10, 100, 1000).forEach { count ->
                    DropdownMenuItem(
                        text = { Text("$count service${if (count > 1) "s" else ""}") },
                        onClick = {
                            showGenerateRandomMenu = false
                            onGenerateRandomServices(count)
                        },
                    )
                }
            }
        }

        Box(
            contentAlignment = Alignment.TopEnd,
        ) {
            OptionEntry(
                title = "Generate services",
                subtitle = "Add real services with icons",
                icon = MdtIcons.Add,
                onClick = { showGenerateSupportedMenu = true },
            )

            DropdownMenu(
                expanded = showGenerateSupportedMenu,
                onDismissRequest = { showGenerateSupportedMenu = false },
                offset = DpOffset(x = (-16).dp, y = 0.dp),
            ) {
                listOf(1, 10, 100, 1000).forEach { count ->
                    DropdownMenuItem(
                        text = { Text("$count service${if (count > 1) "s" else ""}") },
                        onClick = {
                            showGenerateSupportedMenu = false
                            onGenerateSupportedServices(count)
                        },
                    )
                }

                DropdownMenuItem(
                    text = { Text("All (${uiState.supportedServicesCount})") },
                    onClick = {
                        showGenerateSupportedMenu = false
                        onGenerateSupportedServices(null)
                    },
                )
            }
        }

        Box(
            contentAlignment = Alignment.TopEnd,
        ) {
            OptionEntry(
                title = "Trash services",
                subtitle = "Move services to the trash",
                icon = MdtIcons.Delete,
                enabled = uiState.servicesCount > 0,
                onClick = { showTrashMenu = true },
            )

            DropdownMenu(
                expanded = showTrashMenu,
                onDismissRequest = { showTrashMenu = false },
                offset = DpOffset(x = (-16).dp, y = 0.dp),
            ) {
                listOf(1, 10, 100)
                    .filter { it <= uiState.servicesCount }
                    .forEach { count ->
                        DropdownMenuItem(
                            text = { Text("$count service${if (count > 1) "s" else ""}") },
                            onClick = {
                                showTrashMenu = false
                                onTrashServices(count)
                            },
                        )
                    }

                DropdownMenuItem(
                    text = { Text("All (${uiState.servicesCount})") },
                    onClick = {
                        showTrashMenu = false
                        onTrashServices(null)
                    },
                )
            }
        }

        OptionEntry(
            title = "Empty trash",
            subtitle = "Permanently delete trashed services",
            icon = MdtIcons.DeleteForever,
            enabled = uiState.trashedServicesCount > 0,
            onClick = { onEmptyTrash() },
        )
    }
}

@Composable
private fun RowScope.StatCard(
    label: String,
    count: Int,
    icon: Painter,
    tint: Color,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedShape16)
            .background(MdtTheme.color.surfaceContainer)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp),
            )

            Text(
                text = label,
                style = MdtTheme.typo.sm.medium,
                color = MdtTheme.color.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = count.toString(),
            style = MdtTheme.typo.xl3.semiBold,
            color = MdtTheme.color.onSurface,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        ServicesSection(
            uiState = DeveloperUiState(
                servicesCount = 123,
                trashedServicesCount = 4,
            ),
        )
    }
}