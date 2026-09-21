/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2026 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.modal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.modifiers.thenIfTrue
import com.twofasapp.core.design.foundation.preview.PreviewTextLong
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.theme.RoundedShape12
import kotlinx.coroutines.launch

@Composable
fun noInsets() = WindowInsets(0)

@Composable
fun DragHandle(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(44.dp)
            .height(4.dp)
            .clip(RoundedShape12)
            .background(MdtTheme.color.surfaceVariant.copy(alpha = 0.7f)),
    )
}

data class ModalHeaderProperties(
    val showDragHandle: Boolean = true,
    val showCloseButton: Boolean = false,
)

@Composable
private fun AuthStatusMonitor(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    setForceDismiss: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    // TODO: Fix authstatus
//    val authStatus = LocalAuthStatus.current
//
//    LaunchedEffect(authStatus) {
//        when (authStatus) {
//            AuthStatus.Invalid.AppBackgrounded -> Unit
//            AuthStatus.Invalid.NotAuthenticated,
//            AuthStatus.Invalid.SessionExpired,
//            -> {
//                setForceDismiss(true)
//                sheetState.hide()
//                if (sheetState.isVisible.not()) {
//                    onDismissRequest()
//                }
//            }
//
//            else -> Unit
//        }
//    }
}

@Composable
fun Modal(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    headerText: String? = null,
    headerProperties: ModalHeaderProperties = ModalHeaderProperties(),
    header: @Composable (ColumnScope.((onComplete: () -> Unit) -> Unit) -> Unit)? = {
        ModalHeader(
            titleText = headerText,
            showDragHandle = headerProperties.showDragHandle,
            showCloseButton = headerProperties.showCloseButton,
            onCloseClick = onDismissRequest,
        )
    }, // dismiss() callback as param
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    skipPartiallyExpanded: Boolean = true,
    dismissOnBackPress: Boolean = true,
    dismissOnSwipe: Boolean = true,
    animateContentSize: Boolean = false,
    confirmValueChange: ((SheetValue) -> Boolean)? = null,
    sheetState: SheetState? = null,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = MdtTheme.color.surface,
    contentColor: Color = contentColorFor(containerColor),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = dismissOnBackPress,
    ),
    content: @Composable ColumnScope.((onComplete: () -> Unit) -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var forceDismiss by remember { mutableStateOf(false) }
    val modalSheetState: SheetState = sheetState
        ?: rememberModalBottomSheetState(
            skipPartiallyExpanded = skipPartiallyExpanded,
            confirmValueChange = confirmValueChange ?: {
                // forceDismiss flag is used when modal is closed from "external" effect (eg. by clicking some button on a modal)
                when (it) {
                    SheetValue.Hidden -> if (dismissOnSwipe) true else forceDismiss
                    SheetValue.PartiallyExpanded -> true
                    SheetValue.Expanded -> true
                }
            },
        )

    // Monitor auth status in a separate composable to isolate recomposition
    AuthStatusMonitor(
        sheetState = modalSheetState,
        onDismissRequest = onDismissRequest,
        setForceDismiss = { forceDismiss = it },
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.statusBarsPadding(),
        sheetState = modalSheetState,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        scrimColor = scrimColor,
        dragHandle = null,
        contentWindowInsets = { noInsets() },
        properties = properties,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor)
                .navigationBarsPadding()
                .thenIfTrue(animateContentSize, Modifier.animateContentSize()),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            header?.invoke(this) { onComplete ->
                scope.launch {
                    forceDismiss = true
                    modalSheetState.hide()
                }.invokeOnCompletion {
                    if (modalSheetState.isVisible.not()) {
                        onComplete()
                        onDismissRequest()
                    }
                }
            }

            content.invoke(this) { onComplete ->
                scope.launch {
                    forceDismiss = true
                    modalSheetState.hide()
                }.invokeOnCompletion {
                    if (modalSheetState.isVisible.not()) {
                        onComplete()
                        onDismissRequest()
                    }
                }
            }
        }
    }
}

@Composable
fun ModalTitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = text,
        style = MdtTheme.typo.xl.bold,
        color = MdtTheme.color.onSurface,
        modifier = modifier,
    )
}

@Composable
fun ModalHeader(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    title: @Composable (RowScope.() -> Unit)? = null,
    showDragHandle: Boolean = true,
    showCloseButton: Boolean = false,
    onCloseClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 12.dp),
    ) {
        if (showDragHandle) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                DragHandle()
            }
        }

        if (titleText != null || title != null || showCloseButton) {
            Row(
                modifier = Modifier.padding(
                    top = when {
                        showDragHandle.not() && showCloseButton.not() -> 24.dp
                        showDragHandle.not() && showCloseButton -> 20.dp
                        showDragHandle && showCloseButton -> 4.dp
                        else -> 8.dp
                    },
                    bottom = when {
                        showCloseButton.not() -> 12.dp
                        showCloseButton -> 4.dp
                        else -> 0.dp
                    },
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (title != null) {
                    title()
                } else {
                    if (titleText != null) {
                        ModalTitle(
                            text = titleText,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (showCloseButton) {
                    IconButton(
                        modifier = Modifier.testTag("modalCloseButton"),
                        icon = MdtIcons.Close,
                        onClick = onCloseClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewModal(
    content: @Composable ColumnScope.() -> Unit,
) {
    PreviewTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(BottomSheetDefaults.ExpandedShape)
                .background(MdtTheme.color.surface),
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun PreviewNoHeader() {
    PreviewModal {
        ModalHeader(
            titleText = null,
            showDragHandle = false,
        )

        Text(
            text = PreviewTextLong,
            style = MdtTheme.typo.sm.normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewNoTitle() {
    PreviewModal {
        ModalHeader(
            titleText = null,
        )

        Text(
            text = PreviewTextLong,
            style = MdtTheme.typo.sm.normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewTitle() {
    PreviewModal {
        ModalHeader(
            titleText = "Title",
        )

        Text(
            text = PreviewTextLong,
            style = MdtTheme.typo.sm.normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewTitleWithClose() {
    PreviewModal {
        ModalHeader(
            titleText = "Title",
            showCloseButton = true,
        )

        Text(
            text = PreviewTextLong,
            style = MdtTheme.typo.sm.normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewTitleWithCloseWithoutDragHandle() {
    PreviewModal {
        ModalHeader(
            titleText = "Title",
            showDragHandle = false,
            showCloseButton = true,
        )

        Text(
            text = PreviewTextLong,
            style = MdtTheme.typo.sm.normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewTitleWithCloseAndContentInside() {
    PreviewModal {
        ModalHeader(
            titleText = "Title",
            showCloseButton = true,
            title = {
                Box(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Red)
                            .align(Alignment.CenterEnd),
                    )
                }
            },
        )

        Text(
            text = PreviewTextLong,
            style = MdtTheme.typo.sm.normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}