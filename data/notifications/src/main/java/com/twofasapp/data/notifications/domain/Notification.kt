package com.twofasapp.data.notifications.domain

data class Notification(
    val id: String,
    val category: Category,
    val link: String,
    val internalRoute: String?,
    val message: String,
    val createdAt: Long,
    val isRead: Boolean,
) {
    enum class Category(val icon: String) {
        Updates("updates"),
        News("news"),
        Features("features"),
        Youtube("youtube"),
        Tips("tips"),
    }
}
