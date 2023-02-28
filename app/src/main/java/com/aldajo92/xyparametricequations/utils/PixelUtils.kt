package com.aldajo92.xyparametricequations.utils

import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

fun Float.convertPixelsToDp(): Dp {
    val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val dp = this / (metrics.densityDpi / 160f)
    return dp.roundToInt().dp
}
