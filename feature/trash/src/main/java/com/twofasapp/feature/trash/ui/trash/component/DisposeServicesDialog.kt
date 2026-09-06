package com.twofasapp.feature.trash.ui.trash.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceCardSimple
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.foundation.checked.Switch
import com.twofasapp.core.design.foundation.dialog.BaseDialog
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.theme.DialogPadding
import com.twofasapp.locale.MdtLocale

@Composable
internal fun DisposeServicesDialog(
    onDismissRequest: () -> Unit,
    services: List<Service>,
    onConfirm: () -> Unit = {},
) {
    var disposalConfirmed by remember { mutableStateOf(false) }

    BaseDialog(
        onDismissRequest = onDismissRequest,
        positive = MdtLocale.strings.disposeCta,
        negative = MdtLocale.strings.commonCancel,
        positiveEnabled = disposalConfirmed,
        onPositiveClick = onConfirm,
        contentScrollable = false,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DialogPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MdtTheme.color.errorContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = MdtIcons.DeleteForever,
                    tint = MdtTheme.color.onErrorContainer,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = MdtLocale.strings.commonDelete,
                style = MdtTheme.typo.xl.semiBold,
                color = MdtTheme.color.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = MdtLocale.strings.disposeBody1,
                style = MdtTheme.typo.sm.normal,
                color = MdtTheme.color.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(services, key = { it.id }) { service ->
                ServiceCardSimple(
                    state = service.asState(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { disposalConfirmed = disposalConfirmed.not() }
                .padding(horizontal = DialogPadding, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = MdtLocale.strings.disposeConfirm,
                style = MdtTheme.typo.sm.normal,
                color = MdtTheme.color.onSurface,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = disposalConfirmed,
                onCheckedChange = { disposalConfirmed = it },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        DisposeServicesDialog(
            onDismissRequest = {},
            services = listOf(
                Service.Preview.copy(id = 1L, name = "Google", info = "john.doe@gmail.com"),
                Service.Preview.copy(id = 2L, name = "GitHub", info = "johndoe"),
                Service.Preview.copy(id = 3L, name = "Amazon", info = "john.doe@gmail.com"),
            ),
        )
    }
}