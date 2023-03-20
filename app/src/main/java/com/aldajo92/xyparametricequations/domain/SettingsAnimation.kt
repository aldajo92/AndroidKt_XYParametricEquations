package com.aldajo92.xyparametricequations.domain

data class SettingsAnimation(
    val tMin: Float? = -10f,
    val tMax: Float? = 10f,
    val timeDurationMillis: Int? = 5000,
    val showPath: Boolean? = true,
    val pathPoints: Int? = 20
)
