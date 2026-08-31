package com.twofasapp.feature.home.ui.services.add.manual

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.android.viewmodel.ProvidesViewModelStoreOwner
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.InputDialog
import com.twofasapp.core.design.foundation.dialog.InputValidation
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.modal.Modal
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.text.TextIcon
import com.twofasapp.core.design.foundation.textfield.SecretField
import com.twofasapp.core.design.foundation.textfield.SecretFieldTrailingIcon
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.ktx.assetAsBitmap
import com.twofasapp.core.design.ktx.keyboardAsState
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.feature.home.ui.services.add.success.AddServiceSuccessContent
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.android.awaitFrame
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddServiceManualModal(
    onDismissRequest: () -> Unit,
    onAddedSuccessfully: (RecentlyAddedService) -> Unit = {},
) {
    Modal(
        onDismissRequest = onDismissRequest,
    ) {
        ProvidesViewModelStoreOwner {
            val viewModel: AddServiceManualViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.addedService) {
                uiState.addedService?.let(onAddedSuccessfully)
            }

            AnimatedContent(
                targetState = uiState.addedService,
                label = "AddServiceManual",
            ) { addedService ->
                if (addedService != null) {
                    AddServiceSuccessContent(serviceId = addedService.serviceId)
                } else {
                    AddServiceManualContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun AddServiceManualContent(
    viewModel: AddServiceManualViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onUpdateName = { viewModel.updateName(it) },
        onUpdateSecret = { viewModel.updateSecret(it) },
        onToggleAdvanceExpanded = { viewModel.toggleAdvanceExpanded() },
        onUpdateInfo = { viewModel.updateInfo(it) },
        onTryInsertService = { viewModel.tryInsertService() },
        onUpdateAuthType = { viewModel.updateAuthType(it) },
        onUpdateAlgorithm = { viewModel.updateAlgorithm(it) },
        onUpdateRefreshTime = { viewModel.updateRefreshTime(it) },
        onUpdateDigits = { viewModel.updateDigits(it) },
        onUpdateHotpCounter = { viewModel.updateHotpCounter(it) },
        onDismissServiceExistsDialog = { viewModel.dismissServiceExistsDialog() },
        onAddService = { viewModel.addService() },
    )
}

@Composable
private fun Content(
    uiState: AddServiceManualUiState,
    onUpdateName: (String) -> Unit = {},
    onUpdateSecret: (String) -> Unit = {},
    onToggleAdvanceExpanded: () -> Unit = {},
    onUpdateInfo: (String) -> Unit = {},
    onTryInsertService: () -> Unit = {},
    onUpdateAuthType: (Service.AuthType) -> Unit = {},
    onUpdateAlgorithm: (Service.Algorithm) -> Unit = {},
    onUpdateRefreshTime: (Int) -> Unit = {},
    onUpdateDigits: (Int) -> Unit = {},
    onUpdateHotpCounter: (Int) -> Unit = {},
    onDismissServiceExistsDialog: () -> Unit = {},
    onAddService: () -> Unit = {},
) {
    var showAuthTypeDialog by remember { mutableStateOf(false) }
    var showAlgorithmDialog by remember { mutableStateOf(false) }
    var showRefreshTimeDialog by remember { mutableStateOf(false) }
    var showDigitsDialog by remember { mutableStateOf(false) }
    var showHotpDialog by remember { mutableStateOf(false) }
    var secretVisible by remember { mutableStateOf(false) }
    val borderStyle = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
    )
    val borderColor = MdtTheme.color.iconTint
    val isKeyboardExpanded = keyboardAsState()
    val focusRequester = remember { FocusRequester() }
    val focsManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Space(16.dp)

        Text(
            text = MdtLocale.strings.addTitle,
            style = MdtTheme.typo.lg.medium,
            color = MdtTheme.color.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
        )

        Space(16.dp)

        TextField(
            value = uiState.serviceName.orEmpty(),
            onValueChange = { if (it.length <= 30) onUpdateName(it) },
            labelText = MdtLocale.strings.addManualServiceName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            supportingText = uiState.serviceNameError?.let { stringResource(it) },
            isError = uiState.serviceName != null && uiState.serviceNameValid.not(),
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
            singleLine = true,
            maxLines = 1,
            trailingIcon = {
                if (uiState.iconLight != null || uiState.iconDark != null) {
                    Image(
                        bitmap = assetAsBitmap(if (MdtTheme.isDark) uiState.iconDark.orEmpty() else uiState.iconLight.orEmpty()),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(36.dp)
                            .drawBehind {
                                drawCircle(
                                    color = borderColor,
                                    style = borderStyle,
                                )
                            },
                    ) {
                        Icon(
                            painter = MdtIcons.Panorama,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(20.dp),
                            tint = MdtTheme.color.iconTint,
                        )
                    }
                }
            },
        )

        Space(8.dp)

        TextField(
            value = uiState.serviceSecret.orEmpty(),
            onValueChange = { onUpdateSecret(it) },
            labelText = MdtLocale.strings.addManualServiceKey,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            supportingText = uiState.serviceSecretError?.let { stringResource(it) },
            isError = uiState.serviceSecret != null && uiState.serviceSecretValid.not(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done,
            ),
            singleLine = true,
            maxLines = 1,
            visualTransformation = VisualTransformation.SecretField(secretVisible),
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp),
                ) {
                    SecretFieldTrailingIcon(
                        visible = secretVisible,
                        onToggle = { secretVisible = secretVisible.not() },
                    )
                }
            },
        )

        Space(16.dp)

        TextIcon(
            text = MdtLocale.strings.addManualOther,
            style = MdtTheme.typo.sm.medium,
            leadingIcon = if (uiState.advancedExpanded) MdtIcons.ChevronUp else MdtIcons.ChevronDown,
            leadingIconTint = MdtTheme.color.onSurface,
            modifier = Modifier
                .padding(start = 8.dp)
                .clip(CircleShape)
                .clickable {
                    focsManager.clearFocus()
                    onToggleAdvanceExpanded()
                }
                .padding(8.dp),
        )

        if (uiState.advancedExpanded) {
            Space(8.dp)

            TextField(
                value = uiState.additionalInfo,
                onValueChange = { if (it.length <= 50) onUpdateInfo(it) },
                labelText = MdtLocale.strings.addManualAdditionalInfo,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                maxLines = 1,
            )

            Space(24.dp)

            Text(
                text = MdtLocale.strings.addManualAdvanced,
                color = MdtTheme.color.onSurface,
                style = MdtTheme.typo.sm.medium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Space(4.dp)

            Text(
                text = MdtLocale.strings.addManualAdvancedDescription,
                color = MdtTheme.color.onSurfaceVariant,
                style = MdtTheme.typo.xs.normal,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Space(8.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        focsManager.clearFocus()
                        showAuthTypeDialog = true
                    }
                    .padding(vertical = 12.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Type",
                    color = MdtTheme.color.onSurface,
                    style = MdtTheme.typo.sm.normal,
                )

                TextIcon(
                    text = uiState.authType.name,
                    trailingIcon = MdtIcons.UnfoldMore,
                    trailingIconTint = MdtTheme.color.onSurfaceVariant,
                    trailingIconSize = 18.dp,
                    trailingIconSpacer = 4.dp,
                    color = MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(uiState.authType == Service.AuthType.TOTP) {
                        focsManager.clearFocus()
                        showAlgorithmDialog = true
                    }
                    .padding(vertical = 12.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = MdtLocale.strings.addManualAlgorithm,
                    color = if (uiState.authType == Service.AuthType.TOTP) MdtTheme.color.onSurface else MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )

                TextIcon(
                    text = uiState.algorithm.name,
                    trailingIcon = MdtIcons.UnfoldMore,
                    trailingIconTint = MdtTheme.color.onSurfaceVariant,
                    trailingIconSize = 18.dp,
                    trailingIconSpacer = 4.dp,
                    color = MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )
            }

            when (uiState.authType) {
                Service.AuthType.STEAM,
                Service.AuthType.TOTP,
                -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                uiState.authType == Service.AuthType.TOTP,
                            ) {
                                focsManager.clearFocus()
                                showRefreshTimeDialog = true
                            }
                            .padding(vertical = 12.dp)
                            .padding(start = 16.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = MdtLocale.strings.addManualRefreshTime,
                            color = if (uiState.authType == Service.AuthType.TOTP) MdtTheme.color.onSurface else MdtTheme.color.onSurfaceVariant,
                            style = MdtTheme.typo.sm.normal,
                        )

                        TextIcon(
                            text = uiState.refreshTime.toString(),
                            trailingIcon = MdtIcons.UnfoldMore,
                            trailingIconTint = MdtTheme.color.onSurfaceVariant,
                            trailingIconSize = 18.dp,
                            trailingIconSpacer = 4.dp,
                            color = MdtTheme.color.onSurfaceVariant,
                            style = MdtTheme.typo.sm.normal,
                        )
                    }
                }

                Service.AuthType.HOTP -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focsManager.clearFocus()
                                showHotpDialog = true
                            }
                            .padding(vertical = 12.dp)
                            .padding(start = 16.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = MdtLocale.strings.addManualInitialCounter,
                            color = MdtTheme.color.onSurface,
                            style = MdtTheme.typo.sm.normal,
                        )

                        TextIcon(
                            text = uiState.hotpCounter.toString(),
                            trailingIcon = MdtIcons.UnfoldMore,
                            trailingIconTint = MdtTheme.color.onSurfaceVariant,
                            trailingIconSize = 18.dp,
                            trailingIconSpacer = 4.dp,
                            color = MdtTheme.color.onSurfaceVariant,
                            style = MdtTheme.typo.sm.normal,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(uiState.authType != Service.AuthType.STEAM) {
                        focsManager.clearFocus()
                        showDigitsDialog = true
                    }
                    .padding(vertical = 12.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = MdtLocale.strings.addManualDigits,
                    color = if (uiState.authType != Service.AuthType.STEAM) MdtTheme.color.onSurface else MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )

                TextIcon(
                    text = uiState.digits.toString(),
                    trailingIcon = MdtIcons.UnfoldMore,
                    trailingIconTint = MdtTheme.color.onSurfaceVariant,
                    trailingIconSize = 18.dp,
                    trailingIconSpacer = 4.dp,
                    color = MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )
            }
        }

        Space(16.dp)

        Button(
            text = MdtLocale.strings.addManualDoneCta,
            onClick = {
                if (uiState.isFormValid) {
                    onTryInsertService()
                }
            },
            enabled = uiState.isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 16.dp),
        )

        if (isKeyboardExpanded.value) {
            Space(8.dp)
        } else {
            Space(24.dp)
        }

        if (showAuthTypeDialog) {
            ListRadioDialog(
                onDismissRequest = { showAuthTypeDialog = false },
                options = Service.AuthType.entries.map { it.name },
                selectedIndex = Service.AuthType.entries.indexOf(uiState.authType),
                onOptionSelected = { index, _ -> onUpdateAuthType(Service.AuthType.entries[index]) },
            )
        }

        if (showAlgorithmDialog) {
            ListRadioDialog(
                onDismissRequest = { showAlgorithmDialog = false },
                options = Service.Algorithm.entries.map { it.name },
                selectedIndex = Service.Algorithm.entries.indexOf(uiState.algorithm),
                onOptionSelected = { index, _ -> onUpdateAlgorithm(Service.Algorithm.entries[index]) },
            )
        }

        if (showRefreshTimeDialog) {
            ListRadioDialog(
                onDismissRequest = { showRefreshTimeDialog = false },
                options = listOf("10", "30", "60", "90"),
                selectedOption = uiState.refreshTime.toString(),
                onOptionSelected = { _, value -> onUpdateRefreshTime(value.toInt()) },
            )
        }

        if (showDigitsDialog) {
            ListRadioDialog(
                onDismissRequest = { showDigitsDialog = false },
                options = listOf("5", "6", "7", "8"),
                selectedOption = uiState.digits.toString(),
                onOptionSelected = { _, value -> onUpdateDigits(value.toInt()) },
            )
        }

        if (showHotpDialog) {
            InputDialog(
                onDismissRequest = { showHotpDialog = false },
                label = MdtLocale.strings.addManualInitialCounter,
                prefill = uiState.hotpCounter.toString(),
                positive = MdtLocale.strings.commonSave,
                negative = MdtLocale.strings.commonCancel,
                validate = { if (it.trim().toIntOrNull() != null) InputValidation.Valid else InputValidation.Invalid(null) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Number,
                ),
                onPositive = { onUpdateHotpCounter(it.trim().toIntOrNull() ?: 1) },
            )
        }

        if (uiState.showServiceExistsDialog) {
            ConfirmDialog(
                onDismissRequest = { onDismissServiceExistsDialog() },
                title = MdtLocale.strings.addScanServiceExistsTitle,
                body = MdtLocale.strings.addScanServiceExistsBody,
                icon = MdtIcons.Warning,
                positive = MdtLocale.strings.addScanServiceExistsPositiveCta,
                negative = MdtLocale.strings.addScanServiceExistsNegativeCta,
                onPositive = {
                    onDismissServiceExistsDialog()
                    onAddService()
                },
                onNegative = { onDismissServiceExistsDialog() },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PreviewTheme {
        Content(uiState = AddServiceManualUiState(serviceName = "Google", advancedExpanded = false))
    }
}

@Preview
@Composable
private fun PreviewExpanded() {
    PreviewTheme {
        Content(uiState = AddServiceManualUiState(serviceName = "Google", advancedExpanded = true))
    }
}