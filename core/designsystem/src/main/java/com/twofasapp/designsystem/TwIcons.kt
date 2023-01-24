package com.twofasapp.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.painterResource

@Immutable
@Stable
object TwIcons {
    val Placeholder @Composable get() = painterResource(R.drawable.ic_placeholder)
    val More @Composable get() = painterResource(R.drawable.ic_more)
    val Home @Composable get() = painterResource(R.drawable.ic_home)
    val ExternalLink @Composable get() = painterResource(R.drawable.ic_external_link)
    val Notification @Composable get() = painterResource(R.drawable.ic_notification)
    val Settings @Composable get() = painterResource(R.drawable.ic_settings)
    val Edit @Composable get() = painterResource(R.drawable.ic_edit)
    val Qr @Composable get() = painterResource(R.drawable.ic_qr)
    val Write @Composable get() = painterResource(R.drawable.ic_write)
    val Licenses @Composable get() = painterResource(R.drawable.ic_licenses)
    val Lock @Composable get() = painterResource(R.drawable.ic_lock)
    val LockOpen @Composable get() = painterResource(R.drawable.ic_lock_open)
    val Share @Composable get() = painterResource(R.drawable.ic_share)
    val Terms @Composable get() = painterResource(R.drawable.ic_terms)
    val DragHandle @Composable get() = painterResource(R.drawable.ic_drag_handle)
    val Add @Composable get() = painterResource(R.drawable.ic_add)
    val CloudUpload @Composable get() = painterResource(R.drawable.ic_cloud_upload)
    val Delete @Composable get() = painterResource(R.drawable.ic_delete)
    val Extension @Composable get() = painterResource(R.drawable.ic_extension)
    val Eye @Composable get() = painterResource(R.drawable.ic_eye)
    val Favorite @Composable get() = painterResource(R.drawable.ic_favorite)
    val FileUpload @Composable get() = painterResource(R.drawable.ic_file_upload)
    val Info @Composable get() = painterResource(R.drawable.ic_info)
    val Security @Composable get() = painterResource(R.drawable.ic_security)
    val Support @Composable get() = painterResource(R.drawable.ic_support)
    val Copy @Composable get() = painterResource(R.drawable.ic_copy)
}