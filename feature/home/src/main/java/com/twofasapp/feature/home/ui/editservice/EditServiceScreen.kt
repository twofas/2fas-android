package com.twofasapp.feature.home.ui.editservice

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.items.asColor
import com.twofasapp.core.design.feature.settings.OptionEntry
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.foundation.dialog.BaseDialog
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.InfoDialog
import com.twofasapp.core.design.foundation.lazy.listItem
import com.twofasapp.core.design.foundation.outline.HorizontalLine
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.foundation.topbar.TopAppBar
import com.twofasapp.core.design.ktx.copyToClipboard
import com.twofasapp.core.design.ktx.dpToSp
import com.twofasapp.core.design.theme.RoundedShape12
import com.twofasapp.data.services.domain.Group
import com.twofasapp.feature.home.ui.editservice.badge.ColorBadgeDialog
import com.twofasapp.locale.MdtLocale
import com.twofasapp.locale.R
import kotlinx.coroutines.launch

@Composable
internal fun EditServiceScreen(
    onBackClick: () -> Unit,
    onAdvanceClick: () -> Unit,
    onChangeBrandClick: () -> Unit,
    onChangeLabelClick: () -> Unit,
    onDomainAssignmentClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onAuthenticateSecretClick: () -> Unit,
    onAuthenticateQrCodeClick: () -> Unit,
    viewModel: EditServiceViewModel,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val scope = rememberCoroutineScope()
    val showUnsavedChangesDialog = remember { mutableStateOf(false) }

    if (uiState.finish) {
        LaunchedEffect(Unit) {
            scope.launch { onBackClick() }
        }
    }

    BackHandler {
        if (uiState.hasChanges) {
            showUnsavedChangesDialog.value = true
        } else {
            onBackClick()
        }
    }

    Content(
        uiState = uiState,
        onAdvanceClick = onAdvanceClick,
        onChangeBrandClick = onChangeBrandClick,
        onChangeLabelClick = onChangeLabelClick,
        onDomainAssignmentClick = onDomainAssignmentClick,
        onDeleteClick = onDeleteClick,
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

    if (showUnsavedChangesDialog.value) {
        ConfirmDialog(
            title = stringResource(id = R.string.tokens__service_unsaved_changes_title),
            body = stringResource(id = R.string.tokens__service_unsaved_changes),
            onDismissRequest = { showUnsavedChangesDialog.value = false },
            onPositive = { onBackClick() },
        )
    }
}

@Composable
private fun Content(
    uiState: EditServiceUiState,
    onAdvanceClick: () -> Unit = {},
    onChangeBrandClick: () -> Unit = {},
    onChangeLabelClick: () -> Unit = {},
    onDomainAssignmentClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
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
    val activity = (LocalContext.current as? Activity)
    val isSecretVisible = uiState.isSecretVisible
    val showBadgeDialog = remember { mutableStateOf(false) }
    val showSecretNoLockDialog = remember { mutableStateOf(false) }
    val showQrNoLockDialog = remember { mutableStateOf(false) }

    val isBrandSelected = service.imageType == Service.ImageType.IconCollection
    val isLabelSelected = isBrandSelected.not()

    var expanded by remember { mutableStateOf(false) }

    if (uiState.service.id != 0L) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(id = R.string.tokens__customize_service_title),
                    actions = {
                        TextButton(
                            onClick = { onSaveClick() },
                            enabled = uiState.hasChanges && uiState.isInputNameValid && uiState.isInputInfoValid,
                        ) {
                            Text(text = stringResource(id = R.string.commons__save))
                        }
                    },
                )
            },
        ) { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                listItem(EditServiceListItem.HeaderInfo) {
                    OptionHeader(text = stringResource(R.string.tokens__service_information))
                }

                listItem(EditServiceListItem.InputName) {
                    TextField(
                        value = service.name,
                        labelText = stringResource(R.string.tokens__service_name),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                        onValueChange = { text ->
                            if (text.length <= 30) {
                                if (text.isBlank()) {
                                    onUpdateName(text, false)
                                } else {
                                    onUpdateName(text, true)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 72.dp, end = 16.dp)
                            .padding(top = 8.dp, bottom = 4.dp),
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
                            Row(
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
                                                uiState.hasLock.not() -> showQrNoLockDialog.value = true
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
                                                uiState.hasLock.not() -> showSecretNoLockDialog.value = true
                                            }
                                        }
                                        .padding(6.dp),
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 72.dp, end = 16.dp)
                            .padding(top = 8.dp, bottom = 4.dp),
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
                            .padding(start = 72.dp, end = 16.dp)
                            .padding(top = 8.dp, bottom = 4.dp),
                    )
                }

                listItem(EditServiceListItem.Advanced) {
                    OptionEntry(title = stringResource(R.string.customization_advanced), onClick = { onAdvanceClick() })
                }

                listItem(EditServiceListItem.HeaderPersonalization) {
                    HorizontalLine()
                    OptionHeader(text = stringResource(R.string.customization_personalization))
                }

                listItem(EditServiceListItem.IconSelector) {
                    IconSelector(service, isBrandSelected = isBrandSelected, isLabelSelected = isLabelSelected) {
                        onUpdateIconType(it, service.labelText, service.labelColor)
                    }
                }

                listItem(EditServiceListItem.ChangeBrand) {
                    OptionEntry(
                        title = stringResource(R.string.customization_change_brand),
                        enabled = isBrandSelected,
                        onClick = { onChangeBrandClick() },
                    )
                }

                listItem(EditServiceListItem.EditLabel) {
                    OptionEntry(
                        title = stringResource(R.string.customization_edit_label),
                        enabled = isLabelSelected,
                        onClick = { onChangeLabelClick() },
                    )
                }

                listItem(EditServiceListItem.BadgeColor) {
                    OptionEntry(
                        title = stringResource(R.string.tokens__badge_color),
                        icon = MdtIcons.Circle,
                        iconTint = uiState.service.badgeColor.asColor(),
                        onClick = { showBadgeDialog.value = true },
                    )
                }

                if (uiState.groups.isNotEmpty()) {
                    listItem(EditServiceListItem.Group) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = expanded.not() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp, start = 72.dp, bottom = 24.dp, top = 16.dp),

                        ) {
                            OutlinedTextField(
                                value = uiState.groups.firstOrNull { it.id == service.groupId }?.name ?: MdtLocale.strings.servicesMyTokens,
                                onValueChange = { },
                                label = { Text(stringResource(id = R.string.tokens__group)) },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = OutlinedTextFieldDefaults.colors(errorLabelColor = MdtTheme.color.error),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MdtTheme.color.surface),
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = MdtLocale.strings.servicesMyTokens, color = MdtTheme.color.onSurface)
                                    },
                                    onClick = {
                                        onUpdateGroup(null)
                                        expanded = false
                                    },
                                )

                                uiState.groups.filter { it.name != null }.forEach { group ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = group.name.orEmpty(), color = MdtTheme.color.onSurface)
                                        },
                                        onClick = {
                                            onUpdateGroup(group)
                                            expanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                listItem(EditServiceListItem.HeaderOther) {
                    HorizontalLine()
                    OptionHeader(text = stringResource(R.string.tokens__add_manual_other))
                }
                listItem(EditServiceListItem.BrowserExtension) {
                    OptionEntry(
                        title = stringResource(R.string.browser__browser_extension),
                        enabled = service.assignedDomains.isNotEmpty(),
                        onClick = { onDomainAssignmentClick() },
                    )
                }

                listItem(EditServiceListItem.Delete) {
                    HorizontalLine()
                    OptionEntry(
                        title = stringResource(R.string.commons__delete),
                        onClick = { onDeleteClick() },
                        titleColor = MdtTheme.color.primary,
                    )
                }
            }

            if (showBadgeDialog.value) {
                ColorBadgeDialog(
                    selected = service.badgeColor ?: Service.Tint.Default,
                    onDismiss = { showBadgeDialog.value = false },
                    onSelected = {
                        showBadgeDialog.value = false
                        onUpdateBadge(it)
                    },
                )
            }

            if (showSecretNoLockDialog.value) {
                InfoDialog(
                    onDismissRequest = { showSecretNoLockDialog.value = false },
                    title = stringResource(id = R.string.tokens__show_service_key),
                    body = stringResource(id = R.string.tokens__show_service_key_setup_lock),
                    positive = stringResource(id = R.string.commons__set),
                    onNegative = {},
                    onPositive = { onSecurityClick() },
                )
            }

            if (showQrNoLockDialog.value) {
                InfoDialog(
                    onDismissRequest = { showQrNoLockDialog.value = false },
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
                    onNegativeClick = { activity?.copyToClipboard(service.toUri(), isSensitive = true) },
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
                                .clip(RoundedCornerShape(12.dp)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconSelector(
    service: Service,
    isBrandSelected: Boolean,
    isLabelSelected: Boolean,
    onSelectionChanged: (Service.ImageType) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 72.dp)
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
    ) {
        /**
         * Brand
         */
        Box(
            modifier = Modifier
                .size(88.dp)
                .run {
                    if (isBrandSelected) {
                        border(2.dp, MdtTheme.color.primary, RoundedCornerShape(8.dp))
                    } else {
                        border(1.dp, MdtTheme.color.divider, RoundedCornerShape(8.dp))
                    }
                }
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    if (isLabelSelected) {
                        onSelectionChanged(Service.ImageType.IconCollection)
                    }
                },
        ) {
            Image(
                bitmap = serviceIconBitmap(iconCollectionId = service.iconCollectionId),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
            )

            if (isBrandSelected) {
                Icon(
                    painter = MdtIcons.CheckCircle,
                    contentDescription = null,
                    tint = MdtTheme.color.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                        .align(Alignment.BottomEnd),
                )
            }
        }

        Spacer(modifier = Modifier.width(40.dp))

        /**
         * Label
         */
        Box(
            modifier = Modifier
                .size(88.dp)
                .run {
                    if (isLabelSelected) {
                        border(2.dp, MdtTheme.color.primary, RoundedCornerShape(8.dp))
                    } else {
                        border(1.dp, MdtTheme.color.divider, RoundedCornerShape(8.dp))
                    }
                }
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    if (isBrandSelected) {
                        onSelectionChanged(Service.ImageType.Label)
                    }
                },
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .background(shape = CircleShape, color = service.labelColor.asColor()),
            )

            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(18.dp)
                    .clip(RoundedShape12)
                    .background(MdtTheme.color.background)
                    .align(Alignment.Center),
            )

            Text(
                text = service.labelText ?: service.name.take(2).uppercase(),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = MdtTheme.typo.sm.normal.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = dpToSp(dp = 14.dp),
                    lineHeight = dpToSp(dp = 20.dp),
                ),
                modifier = Modifier.align(Alignment.Center),
            )

            if (isLabelSelected) {
                Icon(
                    painter = MdtIcons.CheckCircle,
                    contentDescription = null,
                    tint = MdtTheme.color.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                        .align(Alignment.BottomEnd),
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            uiState = EditServiceUiState(service = Service.Preview.copy(id = 1L)),
        )
    }
}