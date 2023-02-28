package com.aldajo92.xyparametricequations

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputStringField(
    modifier: Modifier = Modifier,
    textTitle: String = "X",
    textValue: String = "",
    showError: Boolean = false,
    errorMessage: String = "Error",
    onValueChange: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        modifier = modifier
            .defaultMinSize(minHeight = 10.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(fontSize = 10.sp),
        value = textValue,
        label = {
            Text(
                text = if (showError) errorMessage else textTitle,
                fontSize = 10.sp
            )
        },
        onValueChange = onValueChange,
        isError = showError,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() })
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputNumberField(
    modifier: Modifier = Modifier,
    textTitle: String = "X",
    textValue: String = "",
    showError: Boolean = false,
    errorMessage: String = "Error",
    onDone: (String) -> Unit = {},
    onValueChange: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        modifier = modifier
            .defaultMinSize(minHeight = 10.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(fontSize = 10.sp),
        value = textValue,
        label = {
            Text(
                text = if (showError) errorMessage else textTitle,
                fontSize = 10.sp
            )
        },
        onValueChange = onValueChange,
        isError = showError,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone(textValue)
                keyboardController?.hide()
            })
    )
}
