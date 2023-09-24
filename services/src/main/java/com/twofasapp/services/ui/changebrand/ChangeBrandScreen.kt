package com.twofasapp.services.ui.changebrand

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.services.ui.serviceIconBitmap
import com.twofasapp.designsystem.TwIcons
import com.twofasapp.designsystem.TwTheme
import com.twofasapp.designsystem.common.TwTopAppBar
import com.twofasapp.designsystem.dialog.ListDialog
import com.twofasapp.designsystem.ktx.LocalBackDispatcher
import com.twofasapp.resources.R
import com.twofasapp.services.ui.EditServiceViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
internal fun ChangeBrandScreen(
    onRequestIconClick: () -> Unit,
    viewModel: EditServiceViewModel = getViewModel(),
    brandViewModel: ChangeBrandViewModel = get(),
) {
    val service = viewModel.uiState.collectAsState().value.service
    val state = brandViewModel.uiState.collectAsState().value
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    var showBrandingDialog = remember { mutableStateOf(false) }
    val backDispatcher = LocalBackDispatcher
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            ToolbarWithSearch(
                title = stringResource(id = R.string.customization_change_brand),
                searchHint = stringResource(id = R.string.commons__search),
                onSearchValueChanged = { brandViewModel.applySearchFilter(it) },
            ) {
                backDispatcher.onBackPressed()
            }
        }
    ) { padding ->
        if (state.sections.isEmpty()) {
            Text(
                text = stringResource(id = R.string.brand_empty_msg),
                style = MaterialTheme.typography.bodyLarge.copy(color = TwTheme.color.onSurfaceSecondary),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            )
            return@Scaffold
        }

        LazyColumn(
            state = listState, modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(top = padding.calculateTopPadding())
        ) {
            // Icon order
            item(key = "icon_order") { SectionHeader(header = stringResource(id = R.string.tokens__order_icon_title)) }

            item(key = "icon_order_details") {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Image(painter = painterResource(id = R.drawable.order_icon_image), contentDescription = null, Modifier.height(80.dp))

                    Column(modifier = Modifier.padding(start = 24.dp)) {
                        Text(
                            text = stringResource(id = R.string.tokens__order_icon_description),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.clickable {
                            showBrandingDialog.value = true
                        }) {
                            Text(
                                text = stringResource(id = R.string.tokens__order_icon_link),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = TwTheme.color.primary,
                                modifier = Modifier.align(CenterVertically)
                            )
                        }
                    }
                }
            }

            state.sections.forEach { (header, brands) ->

                // Header
                item(key = header) { SectionHeader(header = header) }

                // Icons
                items(brands.windowed(4, 4, partialWindows = true)) { brandsRow ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        brandsRow.forEach {
                            Column(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(104.dp)
                                    .align(CenterVertically)
                            ) {
                                Image(
                                    bitmap = serviceIconBitmap(iconCollectionId = it.iconCollectionId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .align(CenterHorizontally)
                                        .border(
                                            width = 2.dp,
                                            color = if (it.iconCollectionId == service.iconCollectionId) TwTheme.color.primary else TwTheme.color.background,
                                            shape = CircleShape
                                        )
                                        .clip(CircleShape)
                                        .clickable {
                                            viewModel.updateBrand(it)
                                            backDispatcher.onBackPressed()
                                        }
                                        .padding(16.dp)
                                )

                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TwTheme.color.onSurfacePrimary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(CenterHorizontally)
                                        .padding(top = 4.dp)
                                )
                            }

                        }

                        repeat(4 - brandsRow.size) {
                            Spacer(modifier = Modifier.size(72.dp))
                        }
                    }
                }
            }
        }

        if (showBrandingDialog.value) {
            ListDialog(
                onDismissRequest = { showBrandingDialog.value = false },
                title = stringResource(id = R.string.tokens__order_menu_title),
                options = listOf(
                    stringResource(id = R.string.tokens__order_menu_option_user),
                    stringResource(id = R.string.tokens__order_menu_option_company)
                ),
                onOptionSelected = { index ->
                    when (index) {
                        0 -> onRequestIconClick()
                        1 -> uriHandler.openUri("https://2fas.com/your-branding/")
                    }
                }
            )
        }

        LaunchedEffect(Unit) {
            if (state.scrollTo.not()) return@LaunchedEffect

            coroutineScope.launch {

                var selectedIndex = -1
                var i = 0

                state.sections.forEach loop@{ entry ->
                    entry.value.windowed(4, 4, partialWindows = true).forEach { window ->
                        if (window.any { it.iconCollectionId == service.iconCollectionId }) {
                            selectedIndex = i
                            return@loop
                        }
                        i++
                    }
                    i++
                }

                if (selectedIndex == -1) {
                    return@launch
                }

                Timber.i("Scroll to row $selectedIndex")
                listState.scrollToItem(index = selectedIndex)
            }
        }
    }
}

@Composable
fun SectionHeader(header: String) {
    Text(
        text = header,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier
            .fillMaxWidth()
            .background(color = TwTheme.color.divider)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ToolbarWithSearch(
    title: String,
    searchHint: String = "",
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    onSearchValueChanged: (String) -> Unit = {},
    navigationClick: () -> Unit
) {
    val showSearch = remember { mutableStateOf(false) }

    TwTopAppBar(title = {
        if (showSearch.value) {
            SearchBar(hint = searchHint, showSearch = showSearch, onValueChanged = onSearchValueChanged)
        } else {
            Text(text = title, color = TwTheme.color.onSurfacePrimary)
        }
    }, navigationIcon = {
        IconButton(onClick = {
            if (showSearch.value) {
                showSearch.value = false
                onSearchValueChanged("")
            } else {
                navigationClick.invoke()
            }
        }) {
            Icon(TwIcons.ArrowBack, null, tint = TwTheme.color.onSurfacePrimary)
        }
    }, actions = {
        if (showSearch.value.not()) {
            IconButton(onClick = { showSearch.value = true }) {
                Icon(TwIcons.Search, null, tint = TwTheme.color.primary)
            }
        }
        actions()
    }, modifier = modifier
    )
}

@Composable
fun SearchBar(
    value: String = "",
    hint: String = "",
    showSearch: MutableState<Boolean>,
    onValueChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    val textValue = remember { mutableStateOf(value) }
    val showClearButton = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (showSearch.value) {
        OutlinedTextField(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    showClearButton.value = focusState.isFocused
                }
                .focusRequester(focusRequester),
            value = textValue.value,
            onValueChange = {
                textValue.value = it
                onValueChanged(it)
            },
            placeholder = {
                Text(text = hint, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp))
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = TwTheme.color.onSurfacePrimary,
                disabledTextColor = Color.Black,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedLabelColor = TwTheme.color.onSurfaceSecondary,
                unfocusedLabelColor = TwTheme.color.onSurfaceSecondary,
                errorLabelColor = TwTheme.color.error,
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = showClearButton.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = {
                        if (textValue.value.isNotEmpty()) {
                            textValue.value = ""
                            onValueChanged("")
                        } else {
                            showClearButton.value = false
                            showSearch.value = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }

                        onClearClick()
                    }) {
                        Icon(
                            painter = TwIcons.Close,
                            tint = TwTheme.color.onSurfacePrimary,
                            contentDescription = null
                        )
                    }
                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else {
        textValue.value = ""
        onValueChanged("")
    }
}