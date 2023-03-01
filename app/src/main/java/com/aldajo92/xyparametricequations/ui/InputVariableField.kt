package com.aldajo92.xyparametricequations.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun InputStringField(
    modifier: Modifier = Modifier,
    textTitle: String = "X",
    textValue: String = "",
    enableInputText: Boolean = true,
    showError: Boolean = false,
    errorMessage: String = "Error",
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    onValueChange: (String) -> Unit = {}
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    OutlinedTextField(
        modifier = modifier
            .defaultMinSize(minHeight = 12.dp)
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusEvent { focusState ->
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        textStyle = TextStyle(fontSize = 12.sp),
        value = textValue,
        label = {
            Text(
                text = if (showError) errorMessage else textTitle,
                fontSize = 12.sp
            )
        },
        onValueChange = onValueChange,
        isError = showError,
        enabled = enableInputText,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() })
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun InputNumberField(
    modifier: Modifier = Modifier,
    textTitle: String = "X",
    textValue: String = "",
    showError: Boolean = false,
    errorMessage: String = "Error",
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    onDone: (String) -> Unit = {},
    onValueChange: (String) -> Unit = {}
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    OutlinedTextField(
        modifier = modifier
            .defaultMinSize(minHeight = 12.dp)
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusEvent { focusState ->
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        textStyle = TextStyle(fontSize = 12.sp),
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
