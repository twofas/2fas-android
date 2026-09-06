package com.twofasapp.feature.home.ui.editservice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.ServiceImageType
import com.twofasapp.core.design.feature.items.asColor
import com.twofasapp.core.design.feature.items.servicecard.ServiceCardImage
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.dialog.BaseDialog
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.InfoDialog
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.layout.ActionsRow
import com.twofasapp.core.design.foundation.lazy.listItem
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.copyToClipboard
import com.twofasapp.core.design.ktx.currentActivity
import com.twofasapp.core.design.theme.RoundedShape12
import com.twofasapp.core.design.theme.RoundedShape16
import com.twofasapp.data.services.domain.Group
import com.twofasapp.feature.home.ui.editservice.advancedsettings.AdvancedSettingsModal
import com.twofasapp.feature.home.ui.editservice.badge.ColorBadgeDialog
import com.twofasapp.locale.MdtLocale
import com.twofasapp.locale.R
import com.twofasapp.parsers.ServiceIcons

@Composable
internal fun EditServiceScreen(
    onBackClick: () -> Unit,
    onChangeBrandClick: () -> Unit,
    onChangeLabelClick: () -> Unit,
    onDomainAssignmentClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onAuthenticateSecretClick: () -> Unit,
    onAuthenticateQrCodeClick: () -> Unit,
    viewModel: EditServiceViewModel,
) {
    val uiState = viewModel.uiState.collectAsState().value
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    if (uiState.finish) {
        LaunchedEffect(Unit) {
            onBackClick()
        }
    }

    BackHandler {
        if (uiState.hasChanges) {
            showUnsavedChangesDialog = true
        } else {
            onBackClick()
        }
    }

    Content(
        uiState = uiState,
        onChangeBrandClick = onChangeBrandClick,
        onChangeLabelClick = onChangeLabelClick,
        onDomainAssignmentClick = onDomainAssignmentClick,
        onSecurityClick = onSecurityClick,
        onAuthenticateSecretClick = onAuthenticateSecretClick,
        onAuthenticateQrCodeClick = onAuthenticateQrCodeClick,
        onSaveClick = { viewModel.saveService() },
        onUpdateName = { text, isValid -> viewModel.updateName(text, isValid) },
        onUpdateInfo = { text, isValid -> viewModel.updateInfo(text, isValid) },
        onUpdateIconType = { imageType, labelText, labelColor -> viewModel.updateIconType(imageType, labelText, labelColor) },
        onUpdateGroup = { group -> viewModel.updateGroup(group) },
        onUpdateBadge = { tint -> viewModel.updateBadge(tint) },
        onToggleSecretVisibility = { viewModel.toggleSecretVisibility() },
        onToggleQrVisibility = { viewModel.toggleQrVisibility() },
    )

    if (showUnsavedChangesDialog) {
        ConfirmDialog(
            title = stringResource(id = R.string.tokens__service_unsaved_changes_title),
            body = stringResource(id = R.string.tokens__service_unsaved_changes),
            onDismissRequest = { showUnsavedChangesDialog = false },
            onPositive = { onBackClick() },
        )
    }
}

@Composable
private fun Content(
    uiState: EditServiceUiState,
    onChangeBrandClick: () -> Unit = {},
    onChangeLabelClick: () -> Unit = {},
    onDomainAssignmentClick: () -> Unit = {},
    onSecurityClick: () -> Unit = {},
    onAuthenticateSecretClick: () -> Unit = {},
    onAuthenticateQrCodeClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onUpdateName: (String, Boolean) -> Unit = { _, _ -> },
    onUpdateInfo: (String, Boolean) -> Unit = { _, _ -> },
    onUpdateIconType: (Service.ImageType, String?, Service.Tint?) -> Unit = { _, _, _ -> },
    onUpdateGroup: (Group?) -> Unit = {},
    onUpdateBadge: (Service.Tint) -> Unit = {},
    onToggleSecretVisibility: () -> Unit = {},
    onToggleQrVisibility: () -> Unit = {},
) {
    val service = uiState.service
    val activity = LocalContext.currentActivity
    val isSecretVisible = uiState.isSecretVisible
    var showInfoModal by remember { mutableStateOf(false) }
    var showBadgeDialog by remember { mutableStateOf(false) }
    var showGroupDialog by remember { mutableStateOf(false) }
    var showSecretNoLockDialog by remember { mutableStateOf(false) }
    var showQrNoLockDialog by remember { mutableStateOf(false) }

    val isBrandSelected = service.imageType == Service.ImageType.IconCollection
    val isLabelSelected = isBrandSelected.not()

    if (uiState.service.id != 0L) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(id = R.string.tokens__customize_service_title),
                    actions = {
                        Button(
                            text = stringResource(id = R.string.commons__save),
                            style = ButtonStyle.Text,
                            enabled = uiState.hasChanges && uiState.isInputNameValid && uiState.isInputInfoValid,
                            onClick = { onSaveClick() },
                        )
                    },
                )
            },
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MdtTheme.color.background)
                    .padding(top = padding.calculateTopPadding()),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                // Service information section
                listItem(EditServiceListItem.HeaderInfo) {
                    OptionHeader(
                        text = stringResource(R.string.tokens__service_information),
                        contentPadding = OptionHeaderContentPaddingFirst,
                    )
                }

                listItem(EditServiceListItem.InputName) {
                    TextField(
                        value = service.name,
                        labelText = stringResource(R.string.tokens__service_name),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                        onValueChange = { text ->
                            if (text.length <= 30) {
                                onUpdateName(text, text.isNotBlank())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                listItem(EditServiceListItem.InputSecret) {
                    TextField(
                        value = service.secret,
                        labelText = stringResource(R.string.tokens__service_key),
                        readOnly = true,
                        singleLine = true,
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                            capitalization = KeyboardCapitalization.None,
                        ),
                        visualTransformation = if (isSecretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            ActionsRow(
                                modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                            ) {
                                Icon(
                                    painter = MdtIcons.Qr,
                                    contentDescription = null,
                                    tint = MdtTheme.color.iconTint,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            when {
                                                uiState.isAuthenticated -> onToggleQrVisibility()
                                                uiState.hasLock -> onAuthenticateQrCodeClick()
                                                else -> showQrNoLockDialog = true
                                            }
                                        }
                                        .padding(6.dp),
                                )

                                Spacer(Modifier.width(4.dp))

                                Icon(
                                    painter = if (isSecretVisible) MdtIcons.VisibilityOff else MdtIcons.Visibility,
                                    contentDescription = null,
                                    tint = MdtTheme.color.iconTint,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            when {
                                                service.id == 0L || uiState.isAuthenticated -> onToggleSecretVisibility()
                                                uiState.hasLock -> onAuthenticateSecretClick()
                                                else -> showSecretNoLockDialog = true
                                            }
                                        }
                                        .padding(6.dp),
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                listItem(EditServiceListItem.InputInfo) {
                    TextField(
                        value = service.info.orEmpty(),
                        labelText = stringResource(R.string.tokens__additional_info),
                        singleLine = true,
                        onValueChange = { text -> if (text.length <= 50) onUpdateInfo(text, true) },
                        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }

                // Personalization section
                listItem(EditServiceListItem.HeaderPersonalization) {
                    OptionHeader(text = stringResource(R.string.customization_personalization))
                }

                listItem(EditServiceListItem.IconSelector) {
                    IconSelector(
                        service = service,
                        isBrandSelected = isBrandSelected,
                        isLabelSelected = isLabelSelected,
                        onSelectionChanged = { onUpdateIconType(it, service.labelText, service.labelColor) },
                    )
                }

                listItem(EditServiceListItem.ChangeBrand) {
                    OptionEntry(
                        title = stringResource(R.string.customization_change_brand),
                        icon = MdtIcons.Panorama,
                        enabled = isBrandSelected,
                        onClick = { onChangeBrandClick() },
                    )
                }

                listItem(EditServiceListItem.EditLabel) {
                    OptionEntry(
                        title = stringResource(R.string.customization_edit_label),
                        icon = MdtIcons.Edit,
                        enabled = isLabelSelected,
                        onClick = { onChangeLabelClick() },
                    )
                }

                listItem(EditServiceListItem.BadgeColor) {
                    OptionEntry(
                        title = stringResource(R.string.tokens__badge_color),
                        icon = MdtIcons.CircleFilled,
                        iconTint = uiState.service.badgeColor.asColor(),
                        onClick = { showBadgeDialog = true },
                    )
                }

                if (uiState.groups.isNotEmpty()) {
                    listItem(EditServiceListItem.Group) {
                        OptionEntry(
                            title = stringResource(R.string.tokens__group),
                            subtitle = uiState.groups.firstOrNull { it.id == service.groupId }?.name ?: MdtLocale.strings.servicesMyTokens,
                            icon = MdtIcons.Group,
                            onClick = { showGroupDialog = true },
                        )
                    }
                }

                // Other section
                listItem(EditServiceListItem.HeaderOther) {
                    OptionHeader(text = stringResource(R.string.tokens__add_manual_other))
                }

                listItem(EditServiceListItem.BrowserExtension) {
                    OptionEntry(
                        title = stringResource(R.string.browser__browser_extension),
                        icon = MdtIcons.Extension,
                        enabled = service.assignedDomains.isNotEmpty(),
                        onClick = { onDomainAssignmentClick() },
                    )
                }

                listItem(EditServiceListItem.Info) {
                    OptionEntry(
                        title = stringResource(R.string.commons__info),
                        icon = MdtIcons.Info,
                        onClick = { showInfoModal = true },
                    )
                }
            }

            if (showInfoModal) {
                AdvancedSettingsModal(
                    onDismissRequest = { showInfoModal = false },
                    service = service,
                )
            }

            if (showBadgeDialog) {
                ColorBadgeDialog(
                    selected = service.badgeColor ?: Service.Tint.Default,
                    onDismiss = { showBadgeDialog = false },
                    onSelected = {
                        showBadgeDialog = false
                        onUpdateBadge(it)
                    },
                )
            }

            if (showGroupDialog) {
                val groups = uiState.groups.filter { it.name != null }

                ListRadioDialog(
                    title = stringResource(id = R.string.tokens__group),
                    options = listOf(MdtLocale.strings.servicesMyTokens) + groups.map { it.name.orEmpty() },
                    selectedIndex = groups.indexOfFirst { it.id == service.groupId }.plus(1),
                    onDismissRequest = { showGroupDialog = false },
                    onOptionSelected = { index, _ ->
                        onUpdateGroup(if (index == 0) null else groups[index - 1])
                    },
                )
            }

            if (showSecretNoLockDialog) {
                InfoDialog(
                    onDismissRequest = { showSecretNoLockDialog = false },
                    title = stringResource(id = R.string.tokens__show_service_key),
                    body = stringResource(id = R.string.tokens__show_service_key_setup_lock),
                    positive = stringResource(id = R.string.commons__set),
                    onNegative = {},
                    onPositive = { onSecurityClick() },
                )
            }

            if (showQrNoLockDialog) {
                InfoDialog(
                    onDismissRequest = { showQrNoLockDialog = false },
                    title = stringResource(id = R.string.tokens__show_qr_code),
                    body = stringResource(id = R.string.tokens__show_service_qr_setup_lock),
                    positive = stringResource(id = R.string.commons__set),
                    onNegative = {},
                    onPositive = { onSecurityClick() },
                )
            }

            if (uiState.isQrVisible) {
                BaseDialog(
                    onDismissRequest = { onToggleQrVisibility() },
                    title = stringResource(id = R.string.tokens__show_qr_code),
                    positive = stringResource(id = R.string.commons__OK),
                    negative = stringResource(id = R.string.tokens__copy_uri),
                    onNegativeClick = { activity.copyToClipboard(service.toUri(), isSensitive = true) },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            bitmap = QrGenerator
                                .generateBitmap(service.toUri())
                                .asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedShape12),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconSelector(
    service: Service,
    isBrandSelected: Boolean,
    isLabelSelected: Boolean,
    onSelectionChanged: (Service.ImageType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedShape16)
            .background(MdtTheme.color.surfaceContainer)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
    ) {
        IconTypeOption(
            title = stringResource(R.string.tokens__brand_icon),
            selected = isBrandSelected,
            onClick = {
                if (isLabelSelected) {
                    onSelectionChanged(Service.ImageType.IconCollection)
                }
            },
        ) {
            ServiceCardImage(
                type = ServiceImageType.Icon,
                iconLight = ServiceIcons.getIcon(collectionId = service.iconCollectionId, isDark = false),
                iconDark = ServiceIcons.getIcon(collectionId = service.iconCollectionId, isDark = true),
                labelText = null,
                labelColor = service.labelColor.asColor(),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
            )
        }

        IconTypeOption(
            title = stringResource(R.string.tokens__label),
            selected = isLabelSelected,
            onClick = {
                if (isBrandSelected) {
                    onSelectionChanged(Service.ImageType.Label)
                }
            },
        ) {
            ServiceCardImage(
                type = ServiceImageType.Label,
                iconLight = "",
                iconDark = "",
                labelText = service.labelText ?: service.name.take(2).uppercase(),
                labelColor = service.labelColor.asColor(),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun IconTypeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, if (selected) MdtTheme.color.primary else MdtTheme.color.transparent, RoundedCornerShape(14.dp))
                .clickable { onClick() }
                .padding(4.dp),
            content = content,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MdtTheme.typo.material.titleMedium,
        )
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = EditServiceUiState(service = Service.Preview.copy(id = 1L)),
        )
    }
}