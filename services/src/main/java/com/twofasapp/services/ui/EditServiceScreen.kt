package com.twofasapp.services.ui

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
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twofasapp.data.services.domain.Service
import com.twofasapp.design.compose.HeaderEntry
import com.twofasapp.design.compose.InputEntry
import com.twofasapp.design.compose.SimpleEntry
import com.twofasapp.design.compose.dialogs.ConfirmDialog
import com.twofasapp.design.compose.dialogs.InputType
import com.twofasapp.design.compose.dialogs.SimpleDialog
import com.twofasapp.design.compose.serviceIconBitmap
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.ktx.dpToSp
import com.twofasapp.designsystem.lazy.listItem
import com.twofasapp.locale.TwLocale
import com.twofasapp.prefs.model.Tint
import com.twofasapp.resources.R
import com.twofasapp.services.domain.GenerateTotp
import com.twofasapp.services.ui.badge.ColorBadgeDialog
import com.twofasapp.services.view.toColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

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
    viewModel: EditServiceViewModel,
) {
    val uiState = viewModel.uiState.collectAsState().value
    val service = uiState.service
    val activity = (LocalContext.current as? Activity)
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val isSecretVisible = uiState.isSecretVisible
    val showBadgeDialog = remember { mutableStateOf(false) }
    val showNoLockDialog = remember { mutableStateOf(false) }
    val showUnsavedChangesDialog = remember { mutableStateOf(false) }

    val isBrandSelected = service.imageType == Service.ImageType.IconCollection
    val isLabelSelected = isBrandSelected.not()

    var expanded by remember { mutableStateOf(false) }

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

    if(uiState.service.id != 0L) {
        Scaffold(topBar = {
            TwTopAppBar(
                titleText = activity!!.getString(R.string.tokens__customize_service_title),
                actions = {
                    TextButton(
                        onClick = { viewModel.saveService() },
                        enabled = uiState.hasChanges && uiState.isInputNameValid && uiState.isInputInfoValid,
                    ) {
                        Text(text = stringResource(id = R.string.commons__save))
                    }

                }
            )
        }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
                listItem(EditServiceListItem.HeaderInfo) {
                    HeaderEntry(stringResource(R.string.tokens__service_information))
                }


                listItem(EditServiceListItem.InputName) {
                    InputEntry(
                        text = service.name,
                        hint = stringResource(R.string.tokens__service_name),
                        maxChars = 30,
                        allowEmpty = false,
                        onValueChange = { isValid, text ->
                            if (text.isBlank()) {
                                viewModel.updateName(text, false)
                            } else {
                                viewModel.updateName(text, isValid)
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                        focusRequester = focusRequester,
                    )
                }

                listItem(EditServiceListItem.InputSecret) {

                    InputEntry(
                        text = service.secret,
                        hint = stringResource(R.string.tokens__service_key),
                        readOnly = true,
                        inputType = InputType.Password,
                        visualTransformation = if (isSecretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {
                                when {
                                    service.id == 0L || uiState.isAuthenticated -> viewModel.toggleSecretVisibility()
                                    uiState.hasLock -> onAuthenticateSecretClick()
                                    uiState.hasLock.not() -> showNoLockDialog.value = true
                                }
                            }) {
                                Icon(
                                    painterResource(id = if (isSecretVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility),
                                    null
                                )
                            }
                        },
                    )
                }

                listItem(EditServiceListItem.InputInfo) {
                    InputEntry(
                        text = service.info.orEmpty(),
                        hint = stringResource(R.string.tokens__additional_info),
                        maxChars = 50,
                        onValueChange = { isValid, text -> viewModel.updateInfo(text, isValid) },
                        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                    )
                }

                listItem(EditServiceListItem.Advanced) {
                    SimpleEntry(title = stringResource(R.string.customization_advanced), click = { onAdvanceClick() })
                }

                listItem(EditServiceListItem.HeaderPersonalization) {
                    Divider(color = TwTheme.color.divider)
                    HeaderEntry(stringResource(R.string.customization_personalization))
                }


                listItem(EditServiceListItem.IconSelector) {
                    IconSelector(service, isBrandSelected = isBrandSelected, isLabelSelected = isLabelSelected) {
                        viewModel.updateIconType(it, service.labelText, service.labelColor)
                    }
                }

                listItem(EditServiceListItem.ChangeBrand) {
                    SimpleEntry(
                        title = stringResource(R.string.customization_change_brand),
                        isEnabled = isBrandSelected,
                        click = { onChangeBrandClick() },
                    )
                }

                listItem(EditServiceListItem.EditLabel) {
                    SimpleEntry(
                        title = stringResource(R.string.customization_edit_label),
                        isEnabled = isLabelSelected,
                        click = { onChangeLabelClick() },
                    )
                }

                listItem(EditServiceListItem.BadgeColor) {
                    SimpleEntry(title = stringResource(R.string.tokens__badge_color),
                        icon = painterResource(id = R.drawable.vector_circle),
                        iconTint = uiState.service.badgeColor.asColor(),
                        click = { showBadgeDialog.value = true })
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
                                value = uiState.groups.firstOrNull { it.id == service.groupId }?.name ?: TwLocale.strings.servicesMyTokens,
                                onValueChange = { },
                                label = { Text(stringResource(id = R.string.tokens__group)) },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = TextFieldDefaults.outlinedTextFieldColors(errorLabelColor = TwTheme.color.error),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(TwTheme.color.surface),
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = TwLocale.strings.servicesMyTokens, color = TwTheme.color.onSurfacePrimary)
                                    },
                                    onClick = {
                                        viewModel.updateGroup(null)
                                        expanded = false
                                    }
                                )

                                uiState.groups.filter { it.name != null }.forEach { group ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = group.name.orEmpty(), color = TwTheme.color.onSurfacePrimary)
                                        },
                                        onClick = {
                                            viewModel.updateGroup(group)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                listItem(EditServiceListItem.HeaderOther) {
                    Divider(color = TwTheme.color.divider)
                    HeaderEntry(stringResource(R.string.tokens__add_manual_other))
                }
                listItem(EditServiceListItem.BrowserExtension) {
                    SimpleEntry(title = stringResource(R.string.browser__browser_extension),
                        isEnabled = service.assignedDomains.isNotEmpty(),
                        click = { onDomainAssignmentClick() })
                }

                listItem(EditServiceListItem.Delete) {
                    Divider(color = TwTheme.color.divider)
                    SimpleEntry(
                        title = stringResource(R.string.commons__delete),
                        click = { onDeleteClick() },
                        titleColor = TwTheme.color.primary
                    )
                }
            }

            if (showBadgeDialog.value) {
                ColorBadgeDialog(
                    selected = service.badgeColor ?: Service.Tint.Default,
                    onDismiss = { showBadgeDialog.value = false },
                    onSelected = {
                        showBadgeDialog.value = false
                        viewModel.updateBadge(it)
                    },
                )
            }

            if (showNoLockDialog.value) {
                SimpleDialog(
                    title = stringResource(id = R.string.tokens__show_service_key),
                    text = stringResource(id = R.string.tokens__show_service_key_setup_lock),
                    positiveText = stringResource(id = R.string.commons__set),
                    onDismiss = { showNoLockDialog.value = false },
                    onNegative = {},
                    onPositive = { onSecurityClick() },
                )
            }

            if (showUnsavedChangesDialog.value) {
                ConfirmDialog(title = stringResource(id = R.string.tokens__service_unsaved_changes_title),
                    text = stringResource(id = R.string.tokens__service_unsaved_changes),
                    onDismiss = { showUnsavedChangesDialog.value = false },
                    onPositive = { onBackClick() })
            }
        }
    }
}

@Composable
fun IconSelector(
    service: Service, isBrandSelected: Boolean, isLabelSelected: Boolean, onSelectionChanged: (Service.ImageType) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 72.dp)
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {

        /**
         * Brand
         */
        Box(modifier = Modifier
            .size(88.dp)
            .run {
                if (isBrandSelected) {
                    border(2.dp, TwTheme.color.primary, RoundedCornerShape(8.dp))
                } else {
                    border(1.dp, TwTheme.color.divider, RoundedCornerShape(8.dp))
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (isLabelSelected) {
                    onSelectionChanged(Service.ImageType.IconCollection)
                }
            }) {
            Image(
                bitmap = serviceIconBitmap(iconCollectionId = service.iconCollectionId),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )

            if (isBrandSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle_old),
                    contentDescription = null,
                    tint = TwTheme.color.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(40.dp))

        /**
         * Label
         */
        Box(modifier = Modifier
            .size(88.dp)
            .run {
                if (isLabelSelected) {
                    border(2.dp, TwTheme.color.primary, RoundedCornerShape(8.dp))
                } else {
                    border(1.dp, TwTheme.color.divider, RoundedCornerShape(8.dp))
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (isBrandSelected) {
                    onSelectionChanged(Service.ImageType.Label)
                }
            }) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .background(shape = CircleShape, color = service.labelColor.asColor())
            )

            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(18.dp)
                    .clip(TwTheme.shape.roundedDefault)
                    .background(TwTheme.color.background)
                    .align(Alignment.Center),
            )

            Text(
                text = service.labelText ?: service.name.take(2).uppercase(),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                style = TwTheme.typo.body3.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = dpToSp(dp = 14.dp),
                    lineHeight = dpToSp(dp = 20.dp)
                ),
                modifier = Modifier.align(Alignment.Center)
            )

            if (isLabelSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle_old),
                    contentDescription = null,
                    tint = TwTheme.color.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
fun Service.Tint?.asColor(): Color {
    return when (this) {
        Service.Tint.Default -> TwTheme.color.surfaceVariant
        Service.Tint.LightBlue -> TwTheme.color.accentLightBlue
        Service.Tint.Indigo -> TwTheme.color.accentIndigo
        Service.Tint.Purple -> TwTheme.color.accentPurple
        Service.Tint.Turquoise -> TwTheme.color.accentTurquoise
        Service.Tint.Green -> TwTheme.color.accentGreen
        Service.Tint.Red -> TwTheme.color.accentRed
        Service.Tint.Orange -> TwTheme.color.accentOrange
        Service.Tint.Yellow -> TwTheme.color.accentYellow
        Service.Tint.Pink -> TwTheme.color.accentPink
        Service.Tint.Brown -> TwTheme.color.accentBrown
        null -> TwTheme.color.surfaceVariant
    }
}