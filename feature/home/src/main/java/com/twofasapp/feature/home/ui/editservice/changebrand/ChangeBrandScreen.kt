package com.twofasapp.feature.home.ui.editservice.changebrand

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.MdtIcons
import com.twofasapp.core.design.MdtTheme
import com.twofasapp.core.design.feature.settings.OptionHeader
import com.twofasapp.core.design.feature.settings.OptionHeaderContentPaddingFirst
import com.twofasapp.core.design.foundation.button.Button
import com.twofasapp.core.design.foundation.button.ButtonHeight
import com.twofasapp.core.design.foundation.button.ButtonStyle
import com.twofasapp.core.design.foundation.dialog.BaseDialog
import com.twofasapp.core.design.foundation.icon.Icon
import com.twofasapp.core.design.foundation.image.AsyncImage
import com.twofasapp.core.design.foundation.other.Space
import com.twofasapp.core.design.foundation.preview.PreviewTheme
import com.twofasapp.core.design.foundation.topbar.TopAppBarWithSearch
import com.twofasapp.core.design.ktx.LocalBackDispatcher
import com.twofasapp.core.design.theme.RoundedShape16
import com.twofasapp.core.design.theme.RoundedShape24
import com.twofasapp.feature.home.ui.editservice.BrandIcon
import com.twofasapp.feature.home.ui.editservice.EditServiceViewModel
import com.twofasapp.locale.R
import com.twofasapp.parsers.ServiceIcons
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
internal fun ChangeBrandScreen(
    close: () -> Unit,
    onRequestIconClick: () -> Unit,
    viewModel: EditServiceViewModel = koinViewModel(),
    brandViewModel: ChangeBrandViewModel = koinViewModel(),
) {
    val service = viewModel.uiState.collectAsState().value.service
    val state = brandViewModel.uiState.collectAsState().value
    val backDispatcher = LocalBackDispatcher

    Content(
        service = service,
        state = state,
        onClose = close,
        onBack = { backDispatcher.onBackPressed() },
        onSearchValueChanged = { brandViewModel.applySearchFilter(it) },
        onUpdateBrand = { viewModel.updateBrand(it) },
        onRequestIconClick = onRequestIconClick,
    )
}

@Composable
private fun Content(
    service: Service,
    state: ChangeBrandUiState,
    onClose: () -> Unit = {},
    onBack: () -> Unit = {},
    onSearchValueChanged: (String) -> Unit = {},
    onUpdateBrand: (BrandIcon) -> Unit = {},
    onRequestIconClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showBrandingDialog by remember { mutableStateOf(false) }
    var finish by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(finish) {
        if (finish) {
            onClose()
        }
    }

    Scaffold(
        topBar = {
            TopAppBarWithSearch(
                title = stringResource(id = R.string.customization_change_brand),
                searchHint = stringResource(id = R.string.commons__search),
                onSearchValueChanged = { onSearchValueChanged(it) },
            ) {
                onBack()
            }
        },
    ) { padding ->
        if (state.loading) {
            return@Scaffold
        }

        if (state.sections.isEmpty()) {
            Text(
                text = stringResource(id = R.string.brand_empty_msg),
                style = MdtTheme.typo.base.normal,
                color = MdtTheme.color.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
            )
            return@Scaffold
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MdtTheme.color.background)
                .imePadding()
                .padding(padding),
        ) {
            // Icon order
            item(key = "icon_order") {
                RequestIconItem(
                    modifier = Modifier.fillMaxWidth(),
                    onRequestClick = { showBrandingDialog = true },
                )
            }

            state.sections.forEach { (header, brands) ->

                // Header
                item(key = header) {
                    OptionHeader(
                        text = header,
                        contentPadding = OptionHeaderContentPaddingFirst,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MdtTheme.color.surfaceContainer),
                    )
                }

                // Icons
                items(brands.windowed(4, 4, partialWindows = true)) { brandsRow ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        brandsRow.forEach { brand ->
                            Column(
                                modifier = Modifier
                                    .size(width = 72.dp, height = 104.dp)
                                    .align(CenterVertically),
                            ) {
                                AsyncImage(
                                    model = "file:///android_asset/${ServiceIcons.getIcon(collectionId = brand.iconCollectionId, isDark = MdtTheme.isDark)}",
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .align(CenterHorizontally)
                                        .border(
                                            width = 2.dp,
                                            color = if (brand.iconCollectionId == service.iconCollectionId) MdtTheme.color.primary else MdtTheme.color.transparent,
                                            shape = CircleShape,
                                        )
                                        .clip(CircleShape)
                                        .clickable {
                                            onUpdateBrand(brand)
                                            finish = true
                                        }
                                        .padding(16.dp),
                                )

                                Text(
                                    text = brand.name,
                                    style = MdtTheme.typo.xs2.normal,
                                    color = MdtTheme.color.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .align(CenterHorizontally)
                                        .padding(top = 4.dp),
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

        if (showBrandingDialog) {
            RequestIconDialog(
                onDismissRequest = { showBrandingDialog = false },
                onUserOptionClick = onRequestIconClick,
                onCompanyOptionClick = { uriHandler.openUri("https://2fas.com/your-branding/") },
            )
        }

        LaunchedEffect(state.scrollTo) {
            if (state.scrollTo.not()) return@LaunchedEffect

            scope.launch {
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
private fun RequestIconItem(
    modifier: Modifier = Modifier,
    onRequestClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp, bottom = 12.dp)
            .clip(RoundedShape24)
            .background(MdtTheme.color.surfaceContainer)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MdtTheme.color.primary.copy(alpha = 0.12f)),
            contentAlignment = Center,
        ) {
            Icon(
                painter = MdtIcons.Panorama,
                modifier = Modifier.size(24.dp),
                tint = MdtTheme.color.primary,
            )
        }

        Space(16.dp)

        Text(
            text = stringResource(id = R.string.tokens__order_icon_description),
            color = MdtTheme.color.onSurface,
            style = MdtTheme.typo.base.normal,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Space(16.dp)

        Button(
            text = stringResource(id = R.string.tokens__order_icon_link),
            onClick = onRequestClick,
            style = ButtonStyle.Filled,
            size = ButtonHeight.Small,
        )

        Space(4.dp)
    }
}

@Composable
private fun RequestIconDialog(
    onDismissRequest: () -> Unit,
    onUserOptionClick: () -> Unit = {},
    onCompanyOptionClick: () -> Unit = {},
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(id = R.string.tokens__order_menu_title),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RequestIconOption(
                title = stringResource(id = R.string.tokens__order_menu_option_user),
                onClick = {
                    onUserOptionClick()
                    onDismissRequest()
                },
            )

            RequestIconOption(
                title = stringResource(id = R.string.tokens__order_menu_option_company),
                onClick = {
                    onCompanyOptionClick()
                    onDismissRequest()
                },
            )
        }
    }
}

@Composable
private fun RequestIconOption(
    title: String,
    onClick: () -> Unit = {},
) {
    Text(
        text = title,
        style = MdtTheme.typo.base.medium,
        color = MdtTheme.color.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedShape16)
            .background(MdtTheme.color.surfaceContainer)
            .clickable { onClick() }
            .padding(16.dp),
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PreviewTheme {
        Content(
            service = Service.Preview,
            state = ChangeBrandUiState(
                loading = false,
                sections = mapOf(
                    "A" to listOf(
                        BrandIcon(name = "Apple", iconCollectionId = ""),
                        BrandIcon(name = "Amazon", iconCollectionId = ""),
                    ),
                ),
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewRequestIconDialog() {
    PreviewTheme {
        RequestIconDialog(
            onDismissRequest = {},
        )
    }
}