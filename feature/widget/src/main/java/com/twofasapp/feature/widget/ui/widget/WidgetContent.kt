package com.twofasapp.feature.widget.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.twofasapp.common.domain.Service
import com.twofasapp.data.services.WidgetsRepository
import com.twofasapp.data.services.domain.Widget
import com.twofasapp.designsystem.ktx.assetAsBitmap
import com.twofasapp.designsystem.service.asColor
import com.twofasapp.designsystem.service.atoms.formatCode
import com.twofasapp.feature.widget.R
import com.twofasapp.feature.widget.ui.settings.WidgetSettingsActivity

@Composable
internal fun WidgetContent(
    appWidgetId: Int,
    widgetsRepository: WidgetsRepository,
) {
    val context = LocalContext.current
    val isNight by remember { mutableStateOf(context.resources.getBoolean(R.bool.isNight)) }
    val widget by widgetsRepository.observeWidget(appWidgetId).collectAsState(initial = Widget(appWidgetId = appWidgetId))

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(R.color.background),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(start = 8.dp, end = 6.dp, top = 8.dp, bottom = 2.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Image(
                provider = ImageProvider(R.drawable.logo_2fas_widget),
                contentDescription = null,
                modifier = GlanceModifier.height(20.dp)
            )

            Spacer(GlanceModifier.defaultWeight())

            Icon(
                modifier = GlanceModifier
                    .size(26.dp)
                    .clickable(
                        actionStartActivity<WidgetSettingsActivity>(
                            actionParametersOf(
                                ActionParameters.Key<Int>(AppWidgetManager.EXTRA_APPWIDGET_ID) to appWidgetId
                            )
                        )
                    )
                    .padding(2.dp)
                    .cornerRadius(24.dp),
                resId = com.twofasapp.designsystem.R.drawable.ic_settings,
            )
        }

        Divider()

        LazyColumn {
            if (widget.services.isEmpty()) {
                item {
                    Box(
                        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = context.getString(com.twofasapp.locale.R.string.widgets_empty_msg),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = context.getColorProvider(R.color.iconTint)
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
    val textColor = context.getColorProvider(R.color.onSurface)
    val code = service.code ?: Service.Code(current = "", next = "", timer = 0, progress = 0.0f)

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(
                actionRunCallback<ToggleServiceAction>(
                    ToggleServiceAction.params(
                        appWidgetId = appWidgetId,
                        serviceId = service.id
                    )
                )
            )
            .padding(
                start = if (revealed) 0.dp else 8.dp,
                end = if (revealed) 8.dp else 4.dp
            ),
        verticalAlignment = Alignment.Vertical.CenterVertically,
    ) {
        if (revealed) {
            Icon(
                resId = R.drawable.ic_widget_chevron_left,
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
                style = TextStyle(fontSize = 23.sp, color = textColor)
            )

            Spacer(GlanceModifier.defaultWeight())

            when (service.authType) {
                Service.AuthType.STEAM,
                Service.AuthType.TOTP -> {
                    Column(
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    ) {
                        Text(
                            text = context.getString(com.twofasapp.locale.R.string.widgets__expires_in),
                            style = TextStyle(fontSize = 11.sp, color = textColor)
                        )
                        Text(
                            text = "${code.timer}" + context.getString(com.twofasapp.locale.R.string.time_unit_seconds_short),
                            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                        )
                    }
                }

                Service.AuthType.HOTP -> {
                    Icon(
                        resId = com.twofasapp.designsystem.R.drawable.ic_increment_hotp,
                        modifier = GlanceModifier
                            .size(26.dp)
                            .clickable(
                                actionRunCallback<HotpGenerateAction>(
                                    HotpGenerateAction.params(service.id)
                                )
                            )
                            .padding(2.dp)
                            .cornerRadius(24.dp),
                        tint = R.color.primary,
                    )
                }
            }

            Spacer(GlanceModifier.width(8.dp))

            Icon(
                resId = com.twofasapp.designsystem.R.drawable.ic_copy,
                modifier = GlanceModifier
                    .size(26.dp)
                    .clickable(actionRunCallback<CopyToClipboardAction>(CopyToClipboardAction.params(code.current)))
                    .padding(2.dp)
                    .cornerRadius(24.dp),
            )

        } else {
            Text(
                text = service.name,
                style = TextStyle(fontSize = 18.sp, color = textColor),
                maxLines = 1,
            )

            Spacer(GlanceModifier.defaultWeight())

            Icon(
                resId = R.drawable.ic_widget_chevron_right,
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
            Box(
                modifier = GlanceModifier
                    .size(24.dp)
                    .background(service.labelColor.asColor())
                    .cornerRadius(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = service.labelText.orEmpty(),
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorProvider(Color.White)),
                )
            }
        }
    }
}

@Composable
private fun Divider(modifier: GlanceModifier = GlanceModifier.fillMaxWidth().height(1.dp)) {
    Box(
        modifier = modifier.background(R.color.divider),
        content = {}
    )
}

@Composable
private fun Icon(modifier: GlanceModifier, resId: Int, tint: Int = R.color.iconTint) {
    val context = LocalContext.current

    Image(
        provider = ImageProvider(resId),
        contentDescription = null,
        modifier = modifier,
        colorFilter = ColorFilter.tint(context.getColorProvider(tint)),
    )
}

private fun Context.getComposeColor(resId: Int) = Color(getColor(resId))
private fun Context.getColorProvider(resId: Int) = ColorProvider(getComposeColor(resId))
