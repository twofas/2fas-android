package com.twofasapp.designsystem.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.twofasapp.designsystem.TwTheme

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheet(
    onDismissRequest: () -> Unit = {},
    bottomSheetNavigator: BottomSheetNavigator,
    content: @Composable () -> Unit
) {
    if (bottomSheetNavigator.navigatorSheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onDismissRequest()
            }
        }
    }

    com.google.accompanist.navigation.material.ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        modifier = Modifier.fillMaxWidth().statusBarsPadding().navigationBarsPadding(),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetElevation = 2.dp,
        sheetBackgroundColor = TwTheme.color.surface,
        sheetContentColor = TwTheme.color.onSurfacePrimary,
        scrimColor = ModalBottomSheetDefaults.scrimColor,
        content = content,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheet(
    onDismissRequest: () -> Unit = {},
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    if (sheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                onDismissRequest()
            }
        }
    }
    ModalBottomSheetLayout(
        modifier = Modifier.animateContentSize(),
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .animateContentSize()
            ) {
                SlideHandle(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(CenterHorizontally)
                )
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

@Composable
fun Modal(
    showHandle: Boolean = true,
    content: @Composable () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .imePadding()
            .animateContentSize()
    ) {
        if (showHandle) {
            SlideHandle(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(CenterHorizontally)
            )
        }

        content()
    }
}

@Composable
fun SlideHandle(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(44.dp)
            .height(4.dp)
            .clip(TwTheme.shape.roundedDefault)
            .background(TwTheme.color.surfaceVariant)
    )
}