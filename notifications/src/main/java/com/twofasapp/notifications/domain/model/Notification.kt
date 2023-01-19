package com.twofasapp.notifications.domain.model

internal data class Notification(
    val id: String,
    val category: Category,
    val link: String,
    val message: String,
    val publishTime: Long,
    val push: Boolean,
    val platform: String,
    val isRead: Boolean,
) {
    enum class Category(val icon: String) {
        Updates("updates"),
        News("news"),
        Features("features"),
        Youtube("youtube"),
    }
}
