package com.twofasapp.feature.qrscan

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.twofasapp.designsystem.R

@Composable
fun QrScanFinder(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.img_qrscan_finder),
        contentDescription = null,
        modifier = modifier
    )
}

@Preview
@Composable
private fun Preview() {
    QrScanFinder()
}