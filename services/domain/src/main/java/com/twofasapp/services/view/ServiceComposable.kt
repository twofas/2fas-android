package com.twofasapp.services.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twofasapp.design.compose.serviceIconBitmap
import com.twofasapp.design.theme.divider
import com.twofasapp.design.theme.isNight
import com.twofasapp.design.theme.parse
import com.twofasapp.design.theme.textPrimary
import com.twofasapp.design.theme.textSecondary
import com.twofasapp.prefs.model.BackupSyncStatus
import com.twofasapp.prefs.model.ServiceType
import com.twofasapp.prefs.model.Tint
import com.twofasapp.services.domain.model.Service

@Composable
fun ServiceIcon(
    service: Service,
    modifier: Modifier = Modifier.size(36.dp),
    fontSize: TextUnit = 14.sp
) {
    when (service.selectedImageType) {
        Service.ImageType.Label -> {

            Box(
                modifier = modifier
                    .background(shape = CircleShape, color = service.labelBackgroundColor.toColor())
            ) {
                Text(
                    text = service.labelText.orEmpty().uppercase(),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, fontSize = fontSize),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Service.ImageType.IconCollection,
        null -> {
            Image(
                bitmap = serviceIconBitmap(iconCollectionId = service.iconCollectionId),
                contentDescription = null,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ServiceCompact(
    service: Service,
    showDivider: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column {
        Row(modifier = modifier.height(56.dp)) {
            ServiceIcon(
                service = service, modifier = Modifier
                    .align(CenterVertically)
                    .padding(end = 16.dp)
                    .size(36.dp)
            )

            Column(modifier = Modifier.align(CenterVertically)) {
                Text(
                    text = service.name,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.textPrimary,
                        fontSize = 18.sp,
                    ),
                    maxLines = 1,
                )

                if (service.otp.account.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = service.otp.account,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.textSecondary, fontSize = 14.sp),
                        maxLines = 1,
                    )
                }
            }
        }

        if (showDivider) {
            Divider(color = MaterialTheme.colors.divider)
        }
    }
}

@Composable
fun Tint?.toColor(default: Tint = Tint.Default): Color {
    val tint = this ?: default
    return Color.parse(if (isNight()) tint.hexDark else tint.hex)
}

@Preview(showSystemUi = true)
@Composable
fun ServiceCompact() {
    ServiceIcon(
        service = Service(
            id = 0,
            name = "",
            secret = "",
            authType = Service.AuthType.TOTP,
            otp = Service.Otp(
                link = null,
                label = "",
                account = "",
                issuer = null,
                digits = 0,
                period = 0,
                hotpCounter = 0,
                algorithm = Service.Algorithm.SHA1
            ),
            badge = null,
            selectedImageType = Service.ImageType.IconCollection,
            labelText = null,
            labelBackgroundColor = Tint.Default,
            iconCollectionId = "",
            groupId = null,
            assignedDomains = emptyList(),
            isDeleted = false,
            backupSyncStatus = BackupSyncStatus.SYNCED,
            updatedAt = 0,
            serviceTypeId = null,
            source = Service.Source.Link,
        )
    )
}