package com.twofasapp.designsystem.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.twofasapp.designsystem.TwTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheet(
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        modifier = Modifier,
        sheetState = sheetState,
        sheetContent = {
            Column(Modifier.navigationBarsPadding()) {
                sheetContent()
            }
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetElevation = 2.dp,
        sheetBackgroundColor = TwTheme.color.surface,
        sheetContentColor = TwTheme.color.onSurfacePrimary,
        scrimColor = ModalBottomSheetDefaults.scrimColor,
        content = content,
    )
}