package com.twofasapp.developer.domain

import com.twofasapp.developer.domain.model.LastScannedQr
import kotlinx.coroutines.flow.Flow

interface ObserveLastScannedQrCase {
    operator fun invoke(): Flow<LastScannedQr>
}