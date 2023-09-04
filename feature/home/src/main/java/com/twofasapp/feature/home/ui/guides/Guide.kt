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
    LinkedIn,
    EpicGames,
    RockstarGames,
    Discord,
    Universal,
    ;
}

val Guide.json: String
    get() = when (this) {
        Guide.Facebook -> "facebook.json"
        Guide.Twitter -> "twitter.json"
        Guide.Amazon -> "amazon.json"
        Guide.Universal -> "universal.json"
        Guide.LinkedIn -> "linkedin.json"
        Guide.EpicGames -> "epic_games.json"
        Guide.RockstarGames -> "rockstar_games.json"
        Guide.Discord -> "discord.json"
    }

private val Guide.serviceId: String
    get() = when (this) {
        Guide.Facebook -> "744e788d-3975-43ac-8166-0029c9a0871c"
        Guide.Twitter -> "a2987ab4-ac5c-48ce-863c-d3d3d1220fdb"
        Guide.Amazon -> "d50d085c-87a1-4c03-80aa-d2384971c6f3"
        Guide.Universal -> "89efcc2d-52f4-4ac3-988d-5d7f3b3cd0a7"
        Guide.LinkedIn -> "924f8361-2435-41fe-8070-b2f6b105b042"
        Guide.EpicGames -> "21701630-a5d2-457c-a983-bfbf4efa801c"
        Guide.RockstarGames -> "deead8dd-c9e3-463a-8c73-1e75c5ec13cf"
        Guide.Discord -> "5c9efdde-cb62-4304-9f04-d120084a53dd"
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