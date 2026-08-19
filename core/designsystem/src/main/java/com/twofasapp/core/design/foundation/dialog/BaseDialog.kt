/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.TextButton
import com.twofasapp.core.design.foundation.preview.PreviewColumn
import com.twofasapp.core.design.foundation.preview.PreviewText
import com.twofasapp.core.design.foundation.preview.PreviewTextLong
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.theme.DialogPadding
import com.twofasapp.core.design.theme.DialogShape

private val IconPadding = PaddingValues(bottom = 16.dp)
private val TitlePadding = PaddingValues(bottom = 16.dp)
private val TextPadding = PaddingValues(bottom = 24.dp)

private val MinWidth = 280.dp
private val MaxWidth = 560.dp

enum class ActionsAlignment { Horizontal, Vertical }

@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    body: String? = null,
    bodyAnnotated: AnnotatedString? = null,
    positive: String? = null,
    negative: String? = null,
    neutral: String? = null,
    icon: Painter? = null,
    iconColor: Color = Color.Unspecified,
    // Retained from the previous BaseDialog (not in the reference): keeps PasswordDialog /
    // BackupScreen clickable annotated body working. When non-null, bodyAnnotated is rendered
    // via ClickableText.
    onBodyClick: ((Int) -> Unit)? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    onNeutralClick: (() -> Unit)? = null,
    positiveEnabled: Boolean = true,
    negativeEnabled: Boolean = true,
    neutralEnabled: Boolean = true,
    positiveColor: Color = Color.Unspecified,
    negativeColor: Color = Color.Unspecified,
    neutralColor: Color = Color.Unspecified,
    dismissOnPositive: Boolean = true,
    dismissOnNegative: Boolean = true,
    dismissOnNeutral: Boolean = true,
    contentScrollable: Boolean = true,
    actionsAlignment: ActionsAlignment = ActionsAlignment.Horizontal,
    shape: Shape = DialogShape,
    containerColor: Color = MdtTheme.color.surfaceContainerLow,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit = {},
) {
    // Adapted: the reference's shouldAutoHideOnLock / LocalAuthStatus / AuthStatus auto-hide
    // is omitted — no auth-status infrastructure exists in this project yet.
    val showActions = positive != null || negative != null || neutral != null
    val centered = icon != null

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
        ) {
            Column(
                modifier = Modifier
                    .sizeIn(minWidth = MinWidth, maxWidth = MaxWidth)
                    .padding(
                        top = DialogPadding,
                        bottom = if (showActions) {
                            8.dp
                        } else {
                            DialogPadding
                        },
                    ),
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = if (iconColor == Color.Unspecified) MdtTheme.color.secondary else iconColor,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                            .size(24.dp),
                    )
                }

                if (title != null) {
                    Title(
                        text = title,
                        centered = centered,
                    )
                }

                if (contentScrollable) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(weight = 1f, fill = false),
                    ) {
                        if (body != null) {
                            Body(text = body)
                        } else if (bodyAnnotated != null) {
                            BodyAnnotated(text = bodyAnnotated, onBodyClick = onBodyClick)
                        }

                        content()
                    }
                } else {
                    if (body != null) {
                        Body(text = body)
                    } else if (bodyAnnotated != null) {
                        BodyAnnotated(text = bodyAnnotated, onBodyClick = onBodyClick)
                    }

                    content()
                }

                if (showActions) {
                    when (actionsAlignment) {
                        ActionsAlignment.Horizontal -> {
                            ActionsHorizontal(
                                positive = positive,
                                negative = negative,
                                neutral = neutral,
                                onPositiveClick = {
                                    onPositiveClick?.invoke()
                                    if (dismissOnPositive) {
                                        onDismissRequest()
                                    }
                                },
                                onNegativeClick = {
                                    onNegativeClick?.invoke()
                                    if (dismissOnNegative) {
                                        onDismissRequest()
                                    }
                                },
                                onNeutralClick = {
                                    onNeutralClick?.invoke()
                                    if (dismissOnNeutral) {
                                        onDismissRequest()
                                    }
                                },
                                positiveColor = positiveColor,
                                negativeColor = negativeColor,
                                neutralColor = neutralColor,
                                positiveEnabled = positiveEnabled,
                                negativeEnabled = negativeEnabled,
                                neutralEnabled = neutralEnabled,
                            )
                        }

                        ActionsAlignment.Vertical -> {
                            ActionsVertical(
                                positive = positive,
                                negative = negative,
                                neutral = neutral,
                                onPositiveClick = {
                                    onPositiveClick?.invoke()
                                    if (dismissOnPositive) {
                                        onDismissRequest()
                                    }
                                },
                                onNegativeClick = {
                                    onNegativeClick?.invoke()
                                    if (dismissOnNegative) {
                                        onDismissRequest()
                                    }
                                },
                                onNeutralClick = {
                                    onNeutralClick?.invoke()
                                    if (dismissOnNeutral) {
                                        onDismissRequest()
                                    }
                                },
                                positiveColor = positiveColor,
                                negativeColor = negativeColor,
                                neutralColor = neutralColor,
                                positiveEnabled = positiveEnabled,
                                negativeEnabled = negativeEnabled,
                                neutralEnabled = neutralEnabled,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Title(
    text: String,
    centered: Boolean,
) {
    Text(
        text = text,
        style = MdtTheme.typo.semiBold.xl,
        color = MdtTheme.color.onSurface,
        textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DialogPadding)
            .padding(TitlePadding),
    )
}

@Composable
private fun Body(
    text: String,
) {
    Text(
        text = text,
        style = MdtTheme.typo.regular.sm.copy(fontSize = 15.sp),
        color = MdtTheme.color.onSurfaceVariant,
        modifier = Modifier
            .padding(horizontal = DialogPadding)
            .padding(TitlePadding),
    )
}

@Composable
private fun BodyAnnotated(
    text: AnnotatedString,
    onBodyClick: ((Int) -> Unit)? = null,
) {
    if (onBodyClick != null) {
        ClickableText(
            text = text,
            onClick = { onBodyClick(it) },
            style = MdtTheme.typo.regular.sm.copy(
                fontSize = 15.sp,
                color = MdtTheme.color.onSurfaceVariant,
            ),
            modifier = Modifier
                .padding(horizontal = DialogPadding)
                .padding(TitlePadding),
        )
    } else {
        Text(
            text = text,
            style = MdtTheme.typo.regular.sm.copy(fontSize = 15.sp),
            color = MdtTheme.color.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = DialogPadding)
                .padding(TitlePadding),
        )
    }
}

@Composable
private fun ActionsHorizontal(
    positive: String? = null,
    negative: String? = null,
    neutral: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    onNeutralClick: (() -> Unit)? = null,
    positiveColor: Color = Color.Unspecified,
    negativeColor: Color = Color.Unspecified,
    neutralColor: Color = Color.Unspecified,
    positiveEnabled: Boolean = true,
    negativeEnabled: Boolean = true,
    neutralEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        if (neutral != null) {
            Spacer(modifier = Modifier.width(16.dp))

            TextButton(
                text = neutral,
                enabled = neutralEnabled,
                colors = if (neutralColor == Color.Unspecified) {
                    ButtonDefaults.textButtonColors()
                } else {
                    ButtonDefaults.textButtonColors()
                        .copy(contentColor = neutralColor)
                },
                onClick = { onNeutralClick?.invoke() },
                modifier = Modifier.testTag("dialogNeutral"),
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        if (negative != null) {
            TextButton(
                text = negative,
                enabled = negativeEnabled,
                colors = if (negativeColor == Color.Unspecified) {
                    ButtonDefaults.textButtonColors()
                } else {
                    ButtonDefaults.textButtonColors()
                        .copy(contentColor = negativeColor)
                },
                onClick = { onNegativeClick?.invoke() },
                modifier = Modifier.testTag("dialogNegative"),
            )

            Spacer(modifier = Modifier.width(8.dp))
        }
        if (positive != null) {
            TextButton(
                text = positive,
                enabled = positiveEnabled,
                colors = if (positiveColor == Color.Unspecified) {
                    ButtonDefaults.textButtonColors()
                } else {
                    ButtonDefaults.textButtonColors()
                        .copy(contentColor = positiveColor)
                },
                onClick = { onPositiveClick?.invoke() },
                modifier = Modifier.testTag("dialogPositive"),
            )

            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
private fun ActionsVertical(
    positive: String? = null,
    negative: String? = null,
    neutral: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null,
    onNeutralClick: (() -> Unit)? = null,
    positiveColor: Color = Color.Unspecified,
    negativeColor: Color = Color.Unspecified,
    neutralColor: Color = Color.Unspecified,
    positiveEnabled: Boolean = true,
    negativeEnabled: Boolean = true,
    neutralEnabled: Boolean = true,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 16.dp, start = 16.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.End,
    ) {
        if (positive != null) {
            TextButton(
                text = positive,
                enabled = positiveEnabled,
                colors = if (positiveColor == Color.Unspecified) {
                    ButtonDefaults.textButtonColors()
                } else {
                    ButtonDefaults.textButtonColors()
                        .copy(contentColor = positiveColor)
                },
                onClick = { onPositiveClick?.invoke() },
                modifier = Modifier.testTag("dialogPositive"),
            )
        }

        if (neutral != null) {
            TextButton(
                text = neutral,
                enabled = neutralEnabled,
                colors = if (neutralColor == Color.Unspecified) {
                    ButtonDefaults.textButtonColors()
                } else {
                    ButtonDefaults.textButtonColors()
                        .copy(contentColor = neutralColor)
                },
                onClick = { onNeutralClick?.invoke() },
                modifier = Modifier.testTag("dialogNeutral"),
            )
        }

        if (negative != null) {
            TextButton(
                text = negative,
                enabled = negativeEnabled,
                colors = if (negativeColor == Color.Unspecified) {
                    ButtonDefaults.textButtonColors()
                } else {
                    ButtonDefaults.textButtonColors()
                        .copy(contentColor = negativeColor)
                },
                onClick = { onNegativeClick?.invoke() },
                modifier = Modifier.testTag("dialogNegative"),
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        BaseDialog(
            onDismissRequest = { },
            title = PreviewText,
            body = PreviewTextLong,
            positive = "Ok",
            negative = "Cancel",
            positiveEnabled = false,
        )
    }
}

@Preview
@Composable
private fun PreviewButtonsHorizontal() {
    PreviewColumn {
        BaseDialog(
            onDismissRequest = { },
            title = PreviewText,
            body = PreviewTextLong,
            positive = "Ok",
            negative = "Cancel",
            neutral = "Neutral",
            icon = MdtIcons.Placeholder,
            positiveEnabled = false,
        )
    }
}

@Preview
@Composable
private fun PreviewButtonsVertical() {
    PreviewColumn {
        BaseDialog(
            onDismissRequest = { },
            title = PreviewText,
            body = PreviewTextLong,
            positive = "Positive",
            negative = "Negative",
            neutral = "Neutral",
            icon = MdtIcons.Placeholder,
            positiveEnabled = false,
            actionsAlignment = ActionsAlignment.Vertical,
        )
    }
}