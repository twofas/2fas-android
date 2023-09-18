package com.twofasapp.services.ui.badge

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.design.compose.dialogs.internal.BaseDialog
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.service.asColor

@Composable
internal fun ColorBadgeDialog(
    selected: Service.Tint,
    onDismiss: () -> Unit = {},
    onSelected: (Service.Tint) -> Unit = {}
) {
    BaseDialog(
        applyContentModifier = false,
        onDismiss = onDismiss,
    ) {

        LazyColumn {
            items(Service.Tint.values().toList(), key = { it.name }) {

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
                                BorderStroke(if (it == selected) 50.dp else 5.dp, SolidColor(it.asColor())), CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    Text(
                        stringResource(
                            id = when (it) {
                                Service.Tint.Default -> com.twofasapp.resources.R.string.color__neutral
                                Service.Tint.LightBlue -> com.twofasapp.resources.R.string.color__light_blue
                                Service.Tint.Indigo -> com.twofasapp.resources.R.string.color__indigo
                                Service.Tint.Purple -> com.twofasapp.resources.R.string.color__purple
                                Service.Tint.Turquoise -> com.twofasapp.resources.R.string.color__turquoise
                                Service.Tint.Green -> com.twofasapp.resources.R.string.color__green
                                Service.Tint.Red -> com.twofasapp.resources.R.string.color__red
                                Service.Tint.Orange -> com.twofasapp.resources.R.string.color__orange
                                Service.Tint.Yellow -> com.twofasapp.resources.R.string.color__yellow
                                Service.Tint.Pink -> com.twofasapp.resources.R.string.color__pink
                                Service.Tint.Brown -> com.twofasapp.resources.R.string.color__brown
                            }
                        ),
                        style = MaterialTheme.typography.bodyLarge.copy(color = TwTheme.color.onSurfacePrimary),
                        modifier = Modifier
                            .align(CenterVertically)
                            .wrapContentWidth()
                    )
                }
            }
        }
    }
}