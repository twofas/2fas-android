package com.twofasapp.feature.home.ui.guides

import android.content.Context
import androidx.compose.runtime.Composable
import com.twofasapp.designsystem.internal.isDarkTheme
import com.twofasapp.parsers.ServiceIcons
import java.io.IOException

enum class Guide {
    Facebook,
    Twitter,
    Amazon,
    Universal,
    ;
}

val Guide.json: String
    get() = when (this) {
        Guide.Facebook -> "facebook.json"
        Guide.Twitter -> "twitter.json"
        Guide.Amazon -> "amazon.json"
        Guide.Universal -> "universal.json"
    }

private val Guide.serviceId: String
    get() = when (this) {
        Guide.Facebook -> "744e788d-3975-43ac-8166-0029c9a0871c"
        Guide.Twitter -> "a2987ab4-ac5c-48ce-863c-d3d3d1220fdb"
        Guide.Amazon -> "d50d085c-87a1-4c03-80aa-d2384971c6f3"
        Guide.Universal -> "89efcc2d-52f4-4ac3-988d-5d7f3b3cd0a7"
    }

@Composable
fun Guide.iconFile(): String {
    return ServiceIcons.getIcon(
        collectionId = ServiceIcons.getIconCollection(serviceId),
        isDark = isDarkTheme(),
    )
}

internal fun Context.getGuideJson(fileName: String): String {
    return try {
        assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        ""
    }
}