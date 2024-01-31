package com.example.tipcalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculatorapp.Components.InputField
import com.example.tipcalculatorapp.ui.theme.TipCalculatorAppTheme
import com.example.tipcalculatorapp.util.calculateTipAmount
import com.example.tipcalculatorapp.util.calculateTotalPerPerson
import com.example.tipcalculatorapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    TipCalculator()
                }
            }
        }
    }
}

@Composable
fun TipCalculator(modifier: Modifier = Modifier) {
    Column(modifier.padding(16.dp)) {
        BillForm() {

        }
    }
}

@Composable
fun TotalPerPerson(totalPerPerson: Double) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Blue.copy(0.2f))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Text(
                text = stringResource(id = R.string.total_per_person), style = TextStyle(
                    color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.SemiBold
                )
            )
            val total = "%.2f".format(totalPerPerson)
            Text(
                modifier = Modifier.padding(top = 4.dp), text = "$$total", style = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            )
        }
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
) {

    val splitBy = remember {
        mutableIntStateOf(1)
    }
    val sliderPosition = remember {
        mutableFloatStateOf(0f)
    }
    val tipPercentage = (sliderPosition.floatValue * 100).toInt()

    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val totalPerPerson = remember {
        mutableDoubleStateOf(0.0)
    }


    TotalPerPerson(totalPerPerson = totalPerPerson.value)
    Card(
        modifier = Modifier
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = Color.Black.copy(0.1f)), colors = CardDefaults.cardColors(
            containerColor = Color.White, //Card background color
            contentColor = Color.White  //Card content color,e.g.text
        ), elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(230.dp)
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )

            if (validState) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.split), style = TextStyle(
                            color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Normal
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitBy.value = if (splitBy.value > 1) splitBy.value - 1 else  1
                                /*if (splitBy.intValue > 1) {
                                    splitBy.intValue - 1
                                } else {
                                    splitBy.intValue
                                }
*/
                                totalPerPerson.doubleValue = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitBy.intValue,
                                    tipPercentage = tipPercentage
                                )
                            },
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = splitBy.intValue.toString(), style = TextStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
//                                splitBy.intValue = splitBy.intValue + 1
                                splitBy.value = splitBy.intValue + 1
                                totalPerPerson.doubleValue = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitBy.intValue,
                                    tipPercentage = tipPercentage
                                )
                            },
                        )
                    }
                }

                //Tip Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(id = R.string.tip), style = TextStyle(
                            color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Normal
                        )
                    )

                    Text(
                        modifier = Modifier.padding(end = 10.dp),
                        text = "$ ${tipAmountState.doubleValue}",
                        style = TextStyle(
                            color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                //Slider
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(end = 10.dp),
                        text = "$tipPercentage %",
                        style = TextStyle(
                            color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Slider(
                        value = sliderPosition.floatValue, onValueChange = {
                            sliderPosition.floatValue = it
                            tipAmountState.doubleValue =
                                calculateTipAmount(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage
                                )

                            totalPerPerson.doubleValue = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitBy.intValue,
                                tipPercentage = tipPercentage
                            )
                        },
                        steps = 5,
                        onValueChangeFinished = {

                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TipCalculatorAppTheme {
        TipCalculator()
    }
}