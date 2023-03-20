package com.twofasapp.designsystem.group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwDropdownMenu
import com.twofasapp.designsystem.common.TwDropdownMenuItem
import com.twofasapp.designsystem.common.TwIconButton
import com.twofasapp.locale.TwLocale

@Composable
fun ServicesGroup(
    id: String?,
    name: String,
    count: Int,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    editMode: Boolean = false,
    onClick: () -> Unit = {},
    onExpandClick: (Boolean) -> Unit = {},
    onMoveUpClick: () -> Unit = {},
    onMoveDownClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {

    var dropdownVisible by remember { mutableStateOf(false) }

    Column(modifier) {
        if (id != null) {
            Divider(color = TwTheme.color.divider)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(TwTheme.color.background)
                .clickable(enabled = editMode.not()) { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.width(64.dp)) {
                Text(
                    text = count.toString(),
                    color = TwTheme.color.onSurfaceSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(min = 28.dp)
                        .border(1.5.dp, TwTheme.color.surfaceVariant, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                )
            }

            Text(
                text = name,
                color = TwTheme.color.onSurfacePrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (editMode && id != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TwIconButton(
                        painter = TwIcons.ArrowUpward,
                        onClick = onMoveUpClick
                    )
                    TwIconButton(
                        painter = TwIcons.ArrowDownward,
                        onClick = onMoveDownClick,
                    )
                    TwDropdownMenu(
                        expanded = dropdownVisible,
                        onDismissRequest = { dropdownVisible = false },
                        anchor = {
                            TwIconButton(
                                painter = TwIcons.More,
                                onClick = { dropdownVisible = true },
                            )
                        }
                    ) {
                        TwDropdownMenuItem(
                            text = TwLocale.strings.commonEdit,
                            icon = TwIcons.Edit,
                            onClick = {
                                dropdownVisible = false
                                onEditClick()
                            }
                        )
                        TwDropdownMenuItem(
                            text = TwLocale.strings.commonDelete,
                            icon = TwIcons.Delete,
                            contentColor = TwTheme.color.accentRed,
                            onClick = {
                                dropdownVisible = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            } else if (editMode.not()) {
                TwIconButton(
                    painter = if (expanded) {
                        TwIcons.ChevronUp
                    } else {
                        TwIcons.ChevronDown
                    },
                    onClick = { onExpandClick(expanded.not()) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ServicesGroup(id = "", name = "Expanded", count = 999, expanded = true)
        ServicesGroup(id = "", name = "Collapsed", count = 999, expanded = false)
        ServicesGroup(id = "", name = "Edit", count = 999, editMode = true)
    }
}
