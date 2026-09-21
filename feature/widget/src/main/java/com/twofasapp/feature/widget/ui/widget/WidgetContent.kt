package com.twofasapp.feature.widget.ui.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.twofasapp.common.domain.SelectedTheme
import com.twofasapp.common.domain.Service
import com.twofasapp.core.design.feature.items.formatCode
import com.twofasapp.core.design.ktx.assetAsBitmap
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.data.services.domain.Widget
import com.twofasapp.data.services.domain.WidgetService
import com.twofasapp.data.session.CustomizationRepository
import com.twofasapp.feature.widget.R
import com.twofasapp.feature.widget.ui.settings.WidgetSettingsActivity

@Composable
internal fun WidgetContent(
    appWidgetId: Int,
    widgetsRepository: WidgetsRepository,
    customizationRepository: CustomizationRepository,
) {
    val context = LocalContext.current
    val selectedTheme by customizationRepository.observeSelectedTheme().collectAsState(initial = customizationRepository.getSelectedTheme())
    val isNight = when (selectedTheme) {
        SelectedTheme.Auto -> context.resources.getBoolean(R.bool.isNight)
        SelectedTheme.Light -> false
        SelectedTheme.Dark -> true
    }
    val widget by widgetsRepository.observeWidget(appWidgetId).collectAsState(initial = Widget(appWidgetId = appWidgetId))

    CompositionLocalProvider(LocalWidgetColors provides if (isNight) NightColors else LightColors) {
        WidgetContent(
            appWidgetId = appWidgetId,
            widget = widget,
            isNight = isNight,
        )
    }
}

@Composable
private fun WidgetContent(
    appWidgetId: Int,
    widget: Widget,
    isNight: Boolean,
) {
    val context = LocalContext.current
    val colors = LocalWidgetColors.current

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(colors.background),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(start = 8.dp, end = 6.dp, top = 8.dp, bottom = 2.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Spacer(GlanceModifier.width(4.dp))

            Image(
                provider = ImageProvider(R.drawable.logo_auth_widget),
                contentDescription = null,
                modifier = GlanceModifier.size(20.dp),
            )

            Spacer(GlanceModifier.width(10.dp))

            Text(
                text = "2FAS Auth",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = LocalWidgetColors.current.onSurface.toColorProvider(),
                    fontWeight = FontWeight.Medium,
                ),
            )

            Spacer(GlanceModifier.defaultWeight())

            Icon(
                modifier = GlanceModifier
                    .size(24.dp)
                    .clickable(
                        actionStartActivity<WidgetSettingsActivity>(
                            actionParametersOf(
                                ActionParameters.Key<Int>(AppWidgetManager.EXTRA_APPWIDGET_ID) to appWidgetId,
                            ),
                        ),
                    )
                    .padding(2.dp)
                    .cornerRadius(24.dp),
                tint = colors.iconTint,
                resId = com.twofasapp.core.design.R.drawable.ic_settings,
            )
        }

        Divider()

        LazyColumn {
            if (widget.services.isEmpty()) {
                item {
                    Box(
                        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = context.getString(com.twofasapp.locale.R.string.widgets_empty_msg),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.iconTint.toColorProvider(),
                            ),
                        )
                    }
                }
            }

            items(items = widget.services, itemId = { it.service.id }) { widgetService ->
                ServiceItem(
                    appWidgetId = appWidgetId,
                    service = widgetService.service,
                    revealed = widgetService.revealed,
                    isNight = isNight,
                )
            }
        }
    }
}

@Composable
private fun ServiceItem(
    appWidgetId: Int,
    service: Service,
    revealed: Boolean,
    isNight: Boolean,
) {
    val context = LocalContext.current
    val textColor = LocalWidgetColors.current.onSurface.toColorProvider()
    val code = service.code ?: Service.Code(current = "", next = "", timer = 0, progress = 0.0f)

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(
                actionRunCallback<ToggleServiceAction>(
                    ToggleServiceAction.params(
                        appWidgetId = appWidgetId,
                        serviceId = service.id,
                    ),
                ),
            )
            .padding(
                start = if (revealed) 0.dp else 10.dp,
                end = if (revealed) 8.dp else 4.dp,
            ),
        verticalAlignment = Alignment.Vertical.CenterVertically,
    ) {
        if (revealed) {
            Icon(
                resId = com.twofasapp.core.design.R.drawable.ic_chevron_left,
                modifier = GlanceModifier.size(24.dp),
            )
        }

        ServiceImage(
            service = service,
            isNight = isNight,
        )

        Spacer(GlanceModifier.width(8.dp))

        if (revealed) {
            Text(
                text = code.current.formatCode(),
                style = TextStyle(fontSize = 23.sp, color = textColor),
            )

            Spacer(GlanceModifier.defaultWeight())

            when (service.authType) {
                Service.AuthType.STEAM,
                Service.AuthType.TOTP,
                -> {
                    Column(
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    ) {
                        Text(
                            text = context.getString(com.twofasapp.locale.R.string.widgets__expires_in),
                            style = TextStyle(fontSize = 11.sp, color = textColor),
                        )
                        Text(
                            text = "${code.timer}" + context.getString(com.twofasapp.locale.R.string.time_unit_seconds_short),
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor),
                        )
                    }
                }

                Service.AuthType.HOTP -> {
                    Icon(
                        resId = com.twofasapp.core.design.R.drawable.ic_refresh,
                        modifier = GlanceModifier
                            .size(26.dp)
                            .clickable(
                                actionRunCallback<HotpGenerateAction>(
                                    HotpGenerateAction.params(service.id),
                                ),
                            )
                            .padding(2.dp)
                            .cornerRadius(24.dp),
                        tint = LocalWidgetColors.current.primary,
                    )
                }
            }

            Spacer(GlanceModifier.width(8.dp))

            Icon(
                resId = com.twofasapp.core.design.R.drawable.ic_copy,
                modifier = GlanceModifier
                    .size(26.dp)
                    .clickable(actionRunCallback<CopyToClipboardAction>(CopyToClipboardAction.params(code.current)))
                    .padding(2.dp)
                    .cornerRadius(24.dp),
            )
        } else {
            Text(
                text = service.name,
                style = TextStyle(fontSize = 16.sp, color = textColor),
                maxLines = 1,
            )

            Spacer(GlanceModifier.defaultWeight())

            Icon(
                resId = com.twofasapp.core.design.R.drawable.ic_chevron_right,
                modifier = GlanceModifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun ServiceImage(
    service: Service,
    isNight: Boolean,
) {
    val context = LocalContext.current

    when (service.imageType) {
        Service.ImageType.IconCollection -> {
            Image(
                provider = ImageProvider(context.assetAsBitmap(if (isNight) service.iconDark else service.iconLight)),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp).padding(2.dp),
            )
        }

        Service.ImageType.Label -> {
            val labelColor = service.labelColor.asWidgetColor()
            val labelTextColor = if (labelColor.luminance() > 0.5f) Color.Black else Color.White

            Box(
                modifier = GlanceModifier
                    .size(24.dp)
                    .background(labelColor)
                    .cornerRadius(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = service.labelText.orEmpty(),
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = labelTextColor.toColorProvider()),
                )
            }
        }
    }
}

@Composable
private fun Divider(modifier: GlanceModifier = GlanceModifier.fillMaxWidth().height(1.dp)) {
    Box(
        modifier = modifier.background(LocalWidgetColors.current.divider),
        content = {},
    )
}

@Composable
private fun Icon(modifier: GlanceModifier, resId: Int, tint: Color = LocalWidgetColors.current.iconTint) {
    Image(
        provider = ImageProvider(resId),
        contentDescription = null,
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint.toColorProvider()),
    )
}

@SuppressLint("RestrictedApi") // False positive: lint conflates the public ColorProvider(Color) overload with the restricted ColorProvider(Int) one
private fun Color.toColorProvider(): ColorProvider = ColorProvider(this)

@Composable
private fun Service.Tint?.asWidgetColor(): Color {
    val colors = LocalWidgetColors.current

    return when (this) {
        Service.Tint.Default, null -> colors.surfaceVariant
        Service.Tint.Red -> colors.primary
        Service.Tint.Orange -> Color(0xFFFF7A00)
        Service.Tint.Yellow -> Color(0xFFFFBA0A)
        Service.Tint.Green -> Color(0xFF03BF38)
        Service.Tint.Turquoise -> Color(0xFF2FCFBC)
        Service.Tint.LightBlue -> Color(0xFF7F9CFF)
        Service.Tint.Indigo -> Color(0xFF5E5CE6)
        Service.Tint.Pink -> Color(0xFFCA49DE)
        Service.Tint.Purple -> Color(0xFF8C49DE)
        Service.Tint.Brown -> Color(0xFFBD8857)
    }
}

private class WidgetColors(
    val background: Color,
    val onSurface: Color,
    val iconTint: Color,
    val divider: Color,
    val primary: Color,
    val surfaceVariant: Color,
)

private val LightColors = WidgetColors(
    background = Color(0xFFF4F4F4),
    onSurface = Color(0xFF1B1B1B),
    iconTint = Color(0xFF5C5C5C),
    divider = Color(0xFFFAFAFA),
    primary = Color(0xFFED1C24),
    surfaceVariant = Color(0xFFE2E2E2),
)

private val NightColors = WidgetColors(
    background = Color(0xFF111216),
    onSurface = Color(0xFFFAFAFA),
    iconTint = Color(0xFFC2C2C2),
    divider = Color(0xFF17181D),
    primary = Color(0xFFF83A40),
    surfaceVariant = Color(0xFF414249),
)

private val LocalWidgetColors = staticCompositionLocalOf { LightColors }

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 250, heightDp = 220)
@Composable
private fun PreviewLight() {
    PreviewContent(isNight = false)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 250, heightDp = 220)
@Composable
private fun PreviewNight() {
    PreviewContent(isNight = true)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 250, heightDp = 120)
@Composable
private fun PreviewEmpty() {
    PreviewContent(isNight = false, services = emptyList())
}

@Composable
private fun PreviewContent(
    isNight: Boolean,
    services: List<WidgetService> = listOf(
        WidgetService(service = Service.Preview.copy(id = 1), revealed = false),
        WidgetService(service = Service.Preview.copy(id = 2, name = "Revealed Service"), revealed = true),
        WidgetService(service = Service.Preview.copy(id = 3, name = "Hotp Service", authType = Service.AuthType.HOTP), revealed = true),
    ),
) {
    CompositionLocalProvider(LocalWidgetColors provides if (isNight) NightColors else LightColors) {
        WidgetContent(
            appWidgetId = 0,
            widget = Widget(appWidgetId = 0, services = services),
            isNight = isNight,
        )
    }
}