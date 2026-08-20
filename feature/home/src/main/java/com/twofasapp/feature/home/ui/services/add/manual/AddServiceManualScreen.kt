package com.twofasapp.feature.home.ui.services.add.manual

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.IconButton
import com.twofasapp.core.design.foundation.dialog.ConfirmDialog
import com.twofasapp.core.design.foundation.dialog.InputDialog
import com.twofasapp.core.design.foundation.dialog.InputValidation
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.other.Divider
import com.twofasapp.core.design.foundation.textfield.SecretField
import com.twofasapp.core.design.foundation.textfield.SecretFieldTrailingIcon
import com.twofasapp.core.design.foundation.textfield.TextField
import com.twofasapp.core.design.foundation.topbar.BackButton
import com.twofasapp.core.design.ktx.assetAsBitmap
import com.twofasapp.core.design.ktx.keyboardAsState
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.locale.MdtLocale
import kotlinx.coroutines.android.awaitFrame
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AddServiceManualScreen(
    viewModel: AddServiceManualViewModel = koinViewModel(),
    onAddedSuccessfully: (RecentlyAddedService) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
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
    val scrollState = rememberScrollState()
    val isKeyboardExpanded = keyboardAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect {
            when (it) {
                is AddServiceManualUiEvent.AddedSuccessfully -> onAddedSuccessfully(it.recentlyAddedService)
            }
        }
    }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .background(MdtTheme.color.surface)
            .verticalScroll(scrollState),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton()

            Text(
                text = MdtLocale.strings.addTitle,
                style = MdtTheme.typo.xl.normal,
                color = MdtTheme.color.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 16.dp),
                textAlign = TextAlign.Center,
            )
        }

        TextField(
            value = uiState.serviceName.orEmpty(),
            onValueChange = { if (it.length <= 30) viewModel.updateName(it) },
            labelText = MdtLocale.strings.addManualServiceName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .focusRequester(focusRequester),
            supportingText = uiState.serviceNameError?.let { context.getString(it) },
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

        TextField(
            value = uiState.serviceSecret.orEmpty(),
            onValueChange = { viewModel.updateSecret(it) },
            labelText = MdtLocale.strings.addManualServiceKey,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            supportingText = uiState.serviceSecretError?.let { context.getString(it) },
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
                SecretFieldTrailingIcon(
                    visible = secretVisible,
                    onToggle = { secretVisible = secretVisible.not() },
                )
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 16.dp)
                .padding(top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = MdtLocale.strings.addManualOther,
                color = MdtTheme.color.onSurface,
                style = MdtTheme.typo.base.normal,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = MdtLocale.strings.addManualOtherOptional,
                color = MdtTheme.color.onSurfaceVariant,
                style = MdtTheme.typo.base.normal,
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                icon = if (uiState.advancedExpanded) {
                    MdtIcons.ChevronUp
                } else {
                    MdtIcons.ChevronDown
                },
                onClick = { viewModel.toggleAdvanceExpanded() },
            )
        }

        if (uiState.advancedExpanded) {
            TextField(
                value = uiState.additionalInfo,
                onValueChange = { if (it.length <= 50) viewModel.updateInfo(it) },
                labelText = MdtLocale.strings.addManualAdditionalInfo,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                singleLine = true,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = MdtLocale.strings.addManualAdvanced,
                color = MdtTheme.color.onSurface,
                style = MdtTheme.typo.base.normal,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = MdtLocale.strings.addManualAdvancedDescription,
                color = MdtTheme.color.onSurfaceVariant,
                style = MdtTheme.typo.sm.normal,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAuthTypeDialog = true }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Type",
                    color = MdtTheme.color.onSurface,
                    style = MdtTheme.typo.base.normal,
                )

                Text(
                    text = uiState.authType.name,
                    color = MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )
            }

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(uiState.authType == Service.AuthType.TOTP) {
                        showAlgorithmDialog = true
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = MdtLocale.strings.addManualAlgorithm,
                    color = if (uiState.authType == Service.AuthType.TOTP) MdtTheme.color.onSurface else MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.base.normal,
                )

                Text(
                    text = uiState.algorithm.name,
                    color = MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )
            }

            Divider()

            when (uiState.authType) {
                Service.AuthType.STEAM,
                Service.AuthType.TOTP,
                -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                uiState.authType == Service.AuthType.TOTP,
                            ) { showRefreshTimeDialog = true }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = MdtLocale.strings.addManualRefreshTime,
                            color = if (uiState.authType == Service.AuthType.TOTP) MdtTheme.color.onSurface else MdtTheme.color.onSurfaceVariant,
                            style = MdtTheme.typo.base.normal,
                        )

                        Text(
                            text = uiState.refreshTime.toString(),
                            color = MdtTheme.color.onSurfaceVariant,
                            style = MdtTheme.typo.sm.normal,
                        )
                    }
                }

                Service.AuthType.HOTP -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showHotpDialog = true }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = MdtLocale.strings.addManualInitialCounter,
                            color = MdtTheme.color.onSurface,
                            style = MdtTheme.typo.base.normal,
                        )

                        Text(
                            text = uiState.hotpCounter.toString(),
                            color = MdtTheme.color.onSurfaceVariant,
                            style = MdtTheme.typo.sm.normal,
                        )
                    }
                }
            }

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(uiState.authType != Service.AuthType.STEAM) { showDigitsDialog = true }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = MdtLocale.strings.addManualDigits,
                    color = if (uiState.authType != Service.AuthType.STEAM) MdtTheme.color.onSurface else MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.base.normal,
                )

                Text(
                    text = uiState.digits.toString(),
                    color = MdtTheme.color.onSurfaceVariant,
                    style = MdtTheme.typo.sm.normal,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            text = MdtLocale.strings.addManualDoneCta,
            onClick = {
                if (uiState.isFormValid) {
                    viewModel.tryInsertService()
                }
            },
            enabled = uiState.isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
        )

        if (isKeyboardExpanded.value) {
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showAuthTypeDialog) {
            ListRadioDialog(
                onDismissRequest = { showAuthTypeDialog = false },
                options = Service.AuthType.entries.map { it.name },
                selectedIndex = Service.AuthType.entries.indexOf(uiState.authType),
                onOptionSelected = { index, _ -> viewModel.updateAuthType(Service.AuthType.entries[index]) },
            )
        }

        if (showAlgorithmDialog) {
            ListRadioDialog(
                onDismissRequest = { showAlgorithmDialog = false },
                options = Service.Algorithm.entries.map { it.name },
                selectedIndex = Service.Algorithm.entries.indexOf(uiState.algorithm),
                onOptionSelected = { index, _ -> viewModel.updateAlgorithm(Service.Algorithm.entries[index]) },
            )
        }

        if (showRefreshTimeDialog) {
            ListRadioDialog(
                onDismissRequest = { showRefreshTimeDialog = false },
                options = listOf("10", "30", "60", "90"),
                selectedOption = uiState.refreshTime.toString(),
                onOptionSelected = { _, value -> viewModel.updateRefreshTime(value.toInt()) },
            )
        }

        if (showDigitsDialog) {
            ListRadioDialog(
                onDismissRequest = { showDigitsDialog = false },
                options = listOf("5", "6", "7", "8"),
                selectedOption = uiState.digits.toString(),
                onOptionSelected = { _, value -> viewModel.updateDigits(value.toInt()) },
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
                onPositive = { viewModel.updateHotpCounter(it.trim().toIntOrNull() ?: 1) },
            )
        }

        if (uiState.showServiceExistsDialog) {
            ConfirmDialog(
                onDismissRequest = { viewModel.dismissServiceExistsDialog() },
                title = MdtLocale.strings.addScanServiceExistsTitle,
                body = MdtLocale.strings.addScanServiceExistsBody,
                positive = MdtLocale.strings.addScanServiceExistsPositiveCta,
                negative = MdtLocale.strings.addScanServiceExistsNegativeCta,
                onPositive = {
                    viewModel.dismissServiceExistsDialog()
                    viewModel.addService()
                },
                onNegative = { viewModel.dismissServiceExistsDialog() },
            )
        }
    }
}