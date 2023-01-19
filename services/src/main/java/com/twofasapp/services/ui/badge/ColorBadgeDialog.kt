package com.twofasapp.services.ui.badge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.twofasapp.design.compose.dialogs.internal.BaseDialog
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.prefs.model.Tint
import com.twofasapp.services.view.toColor

@Composable
internal fun ColorBadgeDialog(
    selected: Tint,
    onDismiss: () -> Unit = {},
    onSelected: (Tint) -> Unit = {}
) {
    BaseDialog(
        applyContentModifier = false,
        onDismiss = onDismiss,
    ) {

        LazyColumn {
            items(Tint.values().toList(), key = { it.name }) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelected(it)
                            onDismiss.invoke()
                        }
                        .padding(vertical = 12.dp, horizontal = 24.dp)
                ) {

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(CenterVertically)
                            .clip(CircleShape)
                            .border(
                                BorderStroke(if (it == selected) 50.dp else 5.dp, SolidColor(it.toColor())), CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    Text(
                        text = it.displayName,
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.textPrimary),
                        modifier = Modifier
                            .align(CenterVertically)
                            .wrapContentWidth()
                    )
                }
            }
        }
    }
}