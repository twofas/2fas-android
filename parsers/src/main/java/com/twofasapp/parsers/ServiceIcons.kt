package com.twofasapp.parsers

import com.twofasapp.parsers.domain.IconCollection

object ServiceIcons {

    private const val defaultIconId = "38e26d32-3c76-4768-8e12-89a050676a07"
    const val defaultCollectionId: String = "a5b3fb65-4ec5-43e6-8ec1-49e24ca9e7ad"

    val collections = SupportedServices.list.filter { it.iconCollection.icons.isNotEmpty() }.map { it.iconCollection }

    fun getIcon(collectionId: String, isDark: Boolean = false): String {
        val id = collections.firstOrNull { it.id.equals(collectionId, true) }?.let { collection ->
            if (collection.icons.size == 1) {
                collection.icons.first().id
            } else {
                collection.icons.first { it.type == if (isDark) IconCollection.IconType.Dark else IconCollection.IconType.Light }.id
            }
        } ?: defaultIconId

        return formatPath(id)
    }

    fun getIconCollection(serviceTypeId: String): String {
        return SupportedServices.list
            .firstOrNull { it.id.equals(serviceTypeId, true) && it.iconCollection.icons.isNotEmpty() }
            ?.iconCollection?.id
            ?: defaultCollectionId
    }


    private fun formatPath(fileName: String): String {
        return "icons/$fileName.png"
    }
}