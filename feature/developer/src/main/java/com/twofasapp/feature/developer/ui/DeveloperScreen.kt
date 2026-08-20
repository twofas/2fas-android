/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.feature.developer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.toastShort
import com.twofasapp.feature.developer.ui.sections.BuildSection
import com.twofasapp.feature.developer.ui.sections.ColorsSection
import com.twofasapp.feature.developer.ui.sections.ServicesSection
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DeveloperScreen(
    viewModel: DeveloperViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Content(
        uiState = uiState,
        onGenerateServices = { count ->
            viewModel.generateServices(count) {
            }
        },
        onDeleteAllServices = {
            viewModel.deleteAllServices {
            }
        },
    )
}

@Composable
private fun Content(
    uiState: DeveloperUiState,
    onGenerateServices: (Int) -> Unit = {},
    onDeleteAllServices: () -> Unit = {},
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Services", "Build", "Colors")

    Scaffold(
        topBar = { TopAppBar(title = "Developer") },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .padding(padding),
        ) {
            ScrollableTabRow(
                modifier = Modifier.background(MdtTheme.color.background),
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        modifier = Modifier.background(MdtTheme.color.background),
                        text = {
                            Text(
                                text = title,
                                style = MdtTheme.typo.sm.medium,
                                color = if (selectedTabIndex == index) MdtTheme.color.primary else MdtTheme.color.onSurfaceVariant,
                            )
                        },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> ServicesSection(
                    uiState = uiState,
                    onGenerateServices = onGenerateServices,
                    onDeleteAllServices = onDeleteAllServices,
                )

                1 -> BuildSection(
                    uiState = uiState,
                )

                2 -> ColorsSection()
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = DeveloperUiState(),
        )
    }
}