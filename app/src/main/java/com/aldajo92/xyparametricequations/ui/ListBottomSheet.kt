package com.aldajo92.xyparametricequations.ui

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aldajo92.xyparametricequations.MainViewModel
import com.aldajo92.xyparametricequations.domain.PairEquation
import com.aldajo92.xyparametricequations.ui.model.EquationUIState
import com.aldajo92.xyparametricequations.ui.theme.XYParametricEquationsTheme

fun Activity.showListBottomSheet(
    viewModel: MainViewModel // TODO: Consider inject a ViewModel for this
) {
    this.showAsBottomSheet { dismissDialog ->

        val equations = listOf(
            PairEquation("Archimedean Spiral", "t*cos(t)", "t*sin(t)"),
            PairEquation("Circle", "cos(t)", "sin(t)"),
            PairEquation("Circle Radius 10", "10*cos(t)", "10*sin(t)"),
            PairEquation("Ellipse", "10*cos(t)", "20*sin(t)"),
            PairEquation("Sine horizontal", "t", "sin(t)"),
            PairEquation("Sine vertical", "sin(t)", "t"),
            PairEquation("Sine horizontal, Amplitude 10", "t", "10*sin(t)"),
            PairEquation("Sine vertical, Amplitude 10", "10*sin(t)", "t"),
            PairEquation("Sine horizontal, Amplitude 10, Frequency 0.1", "t", "10*sin(2*0.1*PI*t)"),
        )

        XYParametricEquationsTheme {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    disabledContentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(bottom = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(equations) {
                        ListItemBottomSheet(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    viewModel.setEquationStringX(it.xt)
                                    viewModel.setEquationStringY(it.yt)
                                    dismissDialog() 
                                },
                            pairEquation = it,
                        ) {
                            viewModel.setEquationStringX(it.xt)
                            viewModel.setEquationStringY(it.yt)
                            dismissDialog()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListItemBottomSheet(
    modifier: Modifier = Modifier,
    pairEquation: PairEquation = PairEquation("No Name", "t", "t"),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Text(pairEquation.name)
        InputEquationsRow(
            modifier = Modifier.clickable { onClick() },
            equationXUIState = EquationUIState(pairEquation.xt),
            equationYUIState = EquationUIState(pairEquation.yt),
            enableInputText = false,
            onEquationExpressionXChange = {},
            onEquationExpressionYChange = {},
        )
    }
}
