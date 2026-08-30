package com.twofasapp.feature.home.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.feature.home.ui.editservice.EditServiceScreenRoute
import com.twofasapp.feature.home.ui.notifications.NotificationsScreen
import com.twofasapp.feature.home.ui.services.ServicesScreen

@Composable
fun ServicesRoute() {
    ServicesScreen()
}

@Composable
fun NotificationsRoute() {
    NotificationsScreen()
}

@Composable
fun EditServiceRoute(
    serviceId: Long,
    openAuth: (successCallback: () -> Unit) -> Unit,
) {
    EditServiceScreenRoute(
        serviceId = serviceId,
        openAuth = openAuth,
    )
}