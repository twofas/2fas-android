package com.twofasapp.feature.home.navigation

import androidx.compose.runtime.Composable
import com.twofasapp.android.navigation.Navigator
import com.twofasapp.android.navigation.Screen
import com.twofasapp.android.viewmodel.ProvidesViewModelStoreOwner
import com.twofasapp.feature.home.ui.editservice.EditServiceScreen
import com.twofasapp.feature.home.ui.editservice.EditServiceViewModel
import com.twofasapp.feature.home.ui.editservice.changebrand.ChangeBrandScreen
import com.twofasapp.feature.home.ui.editservice.changelabel.ChangeLabelScreen
import com.twofasapp.feature.home.ui.editservice.domainassignment.DomainAssignmentScreen
import com.twofasapp.feature.home.ui.editservice.requesticon.RequestIconScreen
import com.twofasapp.feature.home.ui.notifications.NotificationsScreen
import com.twofasapp.feature.home.ui.services.HomeScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

// Edit-service screens share one EditServiceViewModel (unsaved edits live in it),
// so they all resolve it from the same store owner keyed by serviceId.
private fun editServiceOwnerKey(serviceId: Long) = "editservice_$serviceId"

@Composable
fun HomeRoute() {
    HomeScreen()
}

@Composable
fun NotificationsRoute() {
    NotificationsScreen()
}

@Composable
fun EditServiceRoute(
    serviceId: Long,
    openAuth: (successCallback: () -> Unit) -> Unit,
    navigator: Navigator = koinInject(),
) {
    ProvidesViewModelStoreOwner(ownerKey = editServiceOwnerKey(serviceId)) {
        val viewModel: EditServiceViewModel = koinViewModel { parametersOf(serviceId) }

        EditServiceScreen(
            onBackClick = { navigator.back() },
            onChangeBrandClick = { navigator.open(Screen.EditServiceChangeBrand(serviceId)) },
            onChangeLabelClick = { navigator.open(Screen.EditServiceChangeLabel(serviceId)) },
            onDomainAssignmentClick = { navigator.open(Screen.EditServiceDomainAssignment(serviceId)) },
            onSecurityClick = { navigator.open(Screen.Security) },
            onAuthenticateSecretClick = {
                openAuth {
                    viewModel.secretAuthenticated()
                }
            },
            onAuthenticateQrCodeClick = {
                openAuth {
                    viewModel.qrAuthenticated()
                }
            },
            viewModel = viewModel,
        )
    }
}

@Composable
fun EditServiceDomainAssignmentRoute(
    serviceId: Long,
) {
    ProvidesViewModelStoreOwner(ownerKey = editServiceOwnerKey(serviceId)) {
        DomainAssignmentScreen(
            viewModel = koinViewModel { parametersOf(serviceId) },
        )
    }
}

@Composable
fun EditServiceChangeBrandRoute(
    serviceId: Long,
    navigator: Navigator = koinInject(),
) {
    ProvidesViewModelStoreOwner(ownerKey = editServiceOwnerKey(serviceId)) {
        ChangeBrandScreen(
            close = { navigator.back() },
            onRequestIconClick = { navigator.open(Screen.EditServiceRequestIcon) },
            viewModel = koinViewModel { parametersOf(serviceId) },
        )
    }
}

@Composable
fun EditServiceChangeLabelRoute(
    serviceId: Long,
) {
    ProvidesViewModelStoreOwner(ownerKey = editServiceOwnerKey(serviceId)) {
        ChangeLabelScreen(
            viewModel = koinViewModel { parametersOf(serviceId) },
        )
    }
}

@Composable
fun EditServiceRequestIconRoute() {
    RequestIconScreen()
}