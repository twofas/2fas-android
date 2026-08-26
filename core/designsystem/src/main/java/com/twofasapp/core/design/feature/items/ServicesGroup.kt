package com.twofasapp.core.design.feature.items

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
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.menu.DropdownMenu
import com.twofasapp.core.design.foundation.menu.DropdownMenuItem
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
            Divider(color = MdtTheme.color.divider)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MdtTheme.color.background)
                .clickable(enabled = editMode.not() && count > 0) { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.width(64.dp)) {
                Text(
                    text = count.toString(),
                    color = MdtTheme.color.onSurfaceSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(min = 28.dp)
                        .border(1.5.dp, MdtTheme.color.surfaceVariant, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                )
            }

            Text(
                text = name,
                color = MdtTheme.color.onSurfacePrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )

            if (editMode && id != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        painter = MdtIcons.ArrowUpward,
                        onClick = onMoveUpClick,
                    )
                    IconButton(
                        painter = MdtIcons.ArrowDownward,
                        onClick = onMoveDownClick,
                    )
                    DropdownMenu(
                        expanded = dropdownVisible,
                        onDismissRequest = { dropdownVisible = false },
                        anchor = {
                            IconButton(
                                painter = MdtIcons.More,
                                onClick = { dropdownVisible = true },
                            )
                        },
                    ) {
                        DropdownMenuItem(
                            text = TwLocale.strings.commonEdit,
                            icon = MdtIcons.Edit,
                            onClick = {
                                dropdownVisible = false
                                onEditClick()
                            },
                        )
                        DropdownMenuItem(
                            text = TwLocale.strings.commonDelete,
                            icon = MdtIcons.Delete,
                            contentColor = MdtTheme.color.accentRed,
                            onClick = {
                                dropdownVisible = false
                                onDeleteClick()
                            },
                        )
                    }
                }
            } else if (editMode.not() && count > 0) {
                IconButton(
                    painter = if (expanded) {
                        MdtIcons.ChevronUp
                    } else {
                        MdtIcons.ChevronDown
                    },
                    onClick = { onExpandClick(expanded.not()) },
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