package com.aldajo92.xyparametricequations.ui.model

data class EquationUIState(
    val equationString: String = "",
    val showError: Boolean = false,
    val errorMessage: String = ""
)