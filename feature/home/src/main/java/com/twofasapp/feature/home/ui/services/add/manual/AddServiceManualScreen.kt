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
import com.twofasapp.core.design.foundation.dialog.ListRadioDialog
import com.twofasapp.core.design.foundation.other.Divider
import com.twofasapp.core.design.foundation.textfield.OutlinedTextField
import com.twofasapp.core.design.foundation.textfield.OutlinedTextFieldPassword
import com.twofasapp.core.design.foundation.topbar.BackButton
import com.twofasapp.core.design.ktx.assetAsBitmap
import com.twofasapp.core.design.ktx.keyboardAsState
import com.twofasapp.data.services.domain.RecentlyAddedService
import com.twofasapp.locale.TwLocale
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
                text = TwLocale.strings.addTitle,
                style = MdtTheme.typo.title,
                color = MdtTheme.color.onSurfacePrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 16.dp),
                textAlign = TextAlign.Center,
            )
        }

        OutlinedTextField(
            value = uiState.serviceName.orEmpty(),
            onValueChange = { viewModel.updateName(it) },
            labelText = TwLocale.strings.addManualServiceName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp)
                .focusRequester(focusRequester),
            supportingText = uiState.serviceNameError?.let { context.getString(it) },
            isError = uiState.serviceName != null && uiState.serviceNameValid.not(),
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
            maxLength = 30,
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

        OutlinedTextFieldPassword(
            value = uiState.serviceSecret.orEmpty(),
            onValueChange = { viewModel.updateSecret(it) },
            labelText = TwLocale.strings.addManualServiceKey,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            supportingText = uiState.serviceSecretError?.let { context.getString(it) },
            isError = uiState.serviceSecret != null && uiState.serviceSecretValid.not(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            singleLine = true,
            maxLines = 1,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 16.dp)
                .padding(top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = TwLocale.strings.addManualOther,
                color = MdtTheme.color.onSurfacePrimary,
                style = MdtTheme.typo.body1,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = TwLocale.strings.addManualOtherOptional,
                color = MdtTheme.color.onSurfaceSecondary,
                style = MdtTheme.typo.body1,
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                painter = if (uiState.advancedExpanded) {
                    MdtIcons.ChevronUp
                } else {
                    MdtIcons.ChevronDown
                },
                onClick = { viewModel.toggleAdvanceExpanded() },
            )
        }

        if (uiState.advancedExpanded) {
            OutlinedTextField(
                value = uiState.additionalInfo,
                onValueChange = { viewModel.updateInfo(it) },
                labelText = TwLocale.strings.addManualAdditionalInfo,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                maxLength = 50,
                singleLine = true,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = TwLocale.strings.addManualAdvanced,
                color = MdtTheme.color.onSurfacePrimary,
                style = MdtTheme.typo.body1,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = TwLocale.strings.addManualAdvancedDescription,
                color = MdtTheme.color.onSurfaceSecondary,
                style = MdtTheme.typo.body3,
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
                    color = MdtTheme.color.onSurfacePrimary,
                    style = MdtTheme.typo.body1,
                )

                Text(
                    text = uiState.authType.name,
                    color = MdtTheme.color.onSurfaceSecondary,
                    style = MdtTheme.typo.body3,
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
                    text = TwLocale.strings.addManualAlgorithm,
                    color = if (uiState.authType == Service.AuthType.TOTP) MdtTheme.color.onSurfacePrimary else MdtTheme.color.onSurfaceSecondary,
                    style = MdtTheme.typo.body1,
                )

                Text(
                    text = uiState.algorithm.name,
                    color = MdtTheme.color.onSurfaceSecondary,
                    style = MdtTheme.typo.body3,
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
                            text = TwLocale.strings.addManualRefreshTime,
                            color = if (uiState.authType == Service.AuthType.TOTP) MdtTheme.color.onSurfacePrimary else MdtTheme.color.onSurfaceSecondary,
                            style = MdtTheme.typo.body1,
                        )

                        Text(
                            text = uiState.refreshTime.toString(),
                            color = MdtTheme.color.onSurfaceSecondary,
                            style = MdtTheme.typo.body3,
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
                            text = TwLocale.strings.addManualInitialCounter,
                            color = MdtTheme.color.onSurfacePrimary,
                            style = MdtTheme.typo.body1,
                        )

                        Text(
                            text = uiState.hotpCounter.toString(),
                            color = MdtTheme.color.onSurfaceSecondary,
                            style = MdtTheme.typo.body3,
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
                    text = TwLocale.strings.addManualDigits,
                    color = if (uiState.authType != Service.AuthType.STEAM) MdtTheme.color.onSurfacePrimary else MdtTheme.color.onSurfaceSecondary,
                    style = MdtTheme.typo.body1,
                )

                Text(
                    text = uiState.digits.toString(),
                    color = MdtTheme.color.onSurfaceSecondary,
                    style = MdtTheme.typo.body3,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Divider()
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            text = TwLocale.strings.addManualDoneCta,
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
                positive = TwLocale.strings.commonSave,
                negative = TwLocale.strings.commonCancel,
                hint = TwLocale.strings.addManualInitialCounter,
                prefill = uiState.hotpCounter.toString(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Number,
                ),
                positiveEnabled = { it.toIntOrNull() != null },
                onPositiveClick = { viewModel.updateHotpCounter(it.toIntOrNull() ?: 1) },
            )
        }

        if (uiState.showServiceExistsDialog) {
            ConfirmDialog(
                onDismissRequest = { viewModel.dismissServiceExistsDialog() },
                title = TwLocale.strings.addScanServiceExistsTitle,
                body = TwLocale.strings.addScanServiceExistsBody,
                positive = TwLocale.strings.addScanServiceExistsPositiveCta,
                negative = TwLocale.strings.addScanServiceExistsNegativeCta,
                onPositive = {
                    viewModel.dismissServiceExistsDialog()
                    viewModel.addService()
                },
                onNegative = { viewModel.dismissServiceExistsDialog() },
            )
        }
    }
}