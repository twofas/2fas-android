package com.twofasapp.data.services.domain

import com.twofasapp.prefs.model.ServiceType
import com.twofasapp.prefs.model.Tint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BackupService(
    @SerialName("name")
    val name: String,
    @SerialName("secret")
    val secret: String,
    @SerialName("updatedAt")
    val updatedAt: Long,
    @SerialName("type")
    val type: ServiceType? = null,
    @SerialName("serviceTypeID")
    val serviceTypeId: String?,
    @SerialName("otp")
    val otp: Otp,
    @SerialName("order")
    val order: Order,
    @SerialName("badge")
    val badge: Badge?,
    @SerialName("icon")
    val icon: Icon?,
    @SerialName("groupId")
    val groupId: String?,
) {
    @Serializable
    data class Otp(
        @SerialName("link")
        val link: String?,
        @SerialName("label")
        val label: String?,
        @SerialName("account")
        val account: String?,
        @SerialName("issuer")
        val issuer: String?,
        @SerialName("digits")
        val digits: Int?,
        @SerialName("period")
        val period: Int?,
        @SerialName("algorithm")
        val algorithm: String?,
        @SerialName("counter")
        val counter: Int?,
        @SerialName("tokenType")
        val tokenType: String?,
        @SerialName("source")
        val source: String?,
    )

    @Serializable
    data class Order(
        @SerialName("position")
        val position: Int
    )

    @Serializable
    data class Badge(
        @SerialName("color")
        val color: String,
    )

    @Serializable
    data class Icon(
        @SerialName("selected")
        val selected: IconType,
        @SerialName("brand")
        val brand: Brand?,
        @SerialName("label")
        val label: Label?,
        @SerialName("iconCollection")
        val iconCollection: IconCollection?,
    )

    enum class IconType { Brand, Label, IconCollection }

    @Serializable
    data class Brand(
        @SerialName("id")
        val id: ServiceType?,
    )

    @Serializable
    data class IconCollection(
        @SerialName("id")
        val id: String,
    )

    @Serializable
    data class Label(
        @SerialName("text")
        val text: String,
        @SerialName("backgroundColor")
        val backgroundColor: String,
    )
}