/*
 * SPDX-License-Identifier: BUSL-1.1
 *
 * Copyright © 2025 Two Factor Authentication Service, Inc.
 * Licensed under the Business Source License 1.1
 * See LICENSE file for full terms
 */

package com.twofasapp.core.design.foundation.text

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.foundation.image.AsyncImage
import com.twofasapp.core.design.foundation.preview.PreviewColumn
import com.twofasapp.core.design.foundation.preview.PreviewText

@Composable
fun TextIcon(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    leadingIconUrl: String? = null,
    leadingIconSize: Dp = 18.dp,
    leadingIconTint: Color = Color.Unspecified,
    leadingIconSpacer: Dp = 4.dp,
    trailingIcon: Painter? = null,
    trailingIconUrl: String? = null,
    trailingIconSize: Dp = 18.dp,
    trailingIconTint: Color = Color.Unspecified,
    trailingIconSpacer: Dp = 4.dp,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
    fillWidth: Boolean = false,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = when (textAlign) {
            TextAlign.Start -> Arrangement.Start
            TextAlign.Left -> Arrangement.Start
            TextAlign.End -> Arrangement.End
            TextAlign.Right -> Arrangement.End
            TextAlign.Center -> Arrangement.Center
            else -> Arrangement.Start
        },
    ) {
        if (leadingIcon != null) {
            if (leadingIconTint == Color.Unspecified) {
                Image(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(leadingIconSize),
                )
            } else {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    tint = leadingIconTint,
                    modifier = Modifier.size(leadingIconSize),
                )
            }
            Spacer(modifier = Modifier.width(leadingIconSpacer))
        }

        if (leadingIconUrl != null) {
            AsyncImage(
                url = leadingIconUrl,
                modifier = Modifier.size(leadingIconSize),
            )

            Spacer(modifier = Modifier.width(leadingIconSpacer))
        }

        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style,
            modifier = if (fillWidth) {
                Modifier.weight(1f)
            } else {
                Modifier
            },
        )

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(trailingIconSpacer))

            if (trailingIconTint == Color.Unspecified) {
                Image(
                    painter = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(trailingIconSize),
                )
            } else {
                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    tint = trailingIconTint,
                    modifier = Modifier.size(trailingIconSize),
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        if (trailingIconUrl != null) {
            AsyncImage(
                url = trailingIconUrl,
                modifier = Modifier.size(trailingIconSize),
            )

            Spacer(modifier = Modifier.width(trailingIconSpacer))
        }
    }
}

@Composable
fun TextIcon(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    leadingIconSize: Dp = 18.dp,
    leadingIconTint: Color = Color.Unspecified,
    leadingIconSpacer: Dp = 4.dp,
    trailingIcon: Painter? = null,
    trailingIconSize: Dp = 18.dp,
    trailingIconTint: Color = Color.Unspecified,
    trailingIconSpacer: Dp = 4.dp,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = when (textAlign) {
            TextAlign.Start -> Arrangement.Start
            TextAlign.Left -> Arrangement.Start
            TextAlign.End -> Arrangement.End
            TextAlign.Right -> Arrangement.End
            TextAlign.Center -> Arrangement.Center
            else -> Arrangement.Start
        },
    ) {
        if (leadingIcon != null) {
            if (leadingIconTint == Color.Unspecified) {
                Image(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(leadingIconSize),
                )
            } else {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    tint = leadingIconTint,
                    modifier = Modifier.size(leadingIconSize),
                )
            }
            Spacer(modifier = Modifier.width(leadingIconSpacer))
        }

        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style,
        )

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(trailingIconSpacer))

            if (trailingIconTint == Color.Unspecified) {
                Image(
                    painter = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(trailingIconSize),
                )
            } else {
                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    tint = trailingIconTint,
                    modifier = Modifier.size(trailingIconSize),
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewColumn {
        TextIcon(
            text = PreviewText,
            leadingIcon = MdtIcons.Placeholder,
            trailingIcon = MdtIcons.Placeholder,
            modifier = Modifier.height(64.dp),
        )
        TextIcon(
            text = PreviewText,
            leadingIcon = MdtIcons.Placeholder,
            trailingIcon = MdtIcons.Placeholder,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
        )
    }
}