package com.twofasapp.feature.home.ui.services.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.feature.items.ServiceCardSimple
import com.twofasapp.core.design.feature.items.asState
import com.twofasapp.core.design.foundation.dialog.BaseDialog
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.locale.MdtLocale

@Composable
internal fun DeleteServicesDialog(
    onDismissRequest: () -> Unit,
    services: List<Service>,
    onConfirm: () -> Unit = {},
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        icon = MdtIcons.Delete,
        title = MdtLocale.strings.commonDelete,
        body = MdtLocale.strings.servicesDeleteSelected.format(services.size),
        positive = MdtLocale.strings.commonDelete,
        negative = MdtLocale.strings.commonCancel,
        onPositiveClick = onConfirm,
        contentScrollable = false,
    ) {
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
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        DeleteServicesDialog(
            onDismissRequest = {},
            services = listOf(
                Service.Preview.copy(id = 1L, name = "Google", info = "john.doe@gmail.com"),
                Service.Preview.copy(id = 2L, name = "GitHub", info = "johndoe"),
                Service.Preview.copy(id = 3L, name = "Amazon", info = "john.doe@gmail.com"),
            ),
        )
    }
}