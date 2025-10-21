package com.michaelrmossman.docoutdoors.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.michaelrmossman.docoutdoors.R

@Composable
fun SingleChoiceDialog(
    defaultSelected: Int,
    dialogTitle: String,
    onDismissRequest: () -> Unit,
    onSubmitButtonClick: (Int) -> Unit,
    optionsList: Array<String>,
    optionalMessage: AnnotatedString? = null
) {
    val cardCornerShape = dimensionResource(R.dimen.card_corner_shape)
    var selectedOption by remember { mutableIntStateOf(defaultSelected) }
    val spacerHeight = dimensionResource(R.dimen.padding_small)

    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {

        Surface(
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(cardCornerShape)
        ) {

            Column(
                modifier = Modifier.padding(
                    vertical = spacerHeight
                )
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        end = dimensionResource(R.dimen.padding_mega),
                        start = dimensionResource(R.dimen.padding_mega),
                        top = dimensionResource(R.dimen.padding_small)
                    )
                )
                Spacer(modifier = Modifier.height(spacerHeight))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = optionsList,
                        key = { option: String -> optionsList.indexOf(option) }
                    ) { item ->
                        SingleChoiceRadioButton(
                            text = item,
                            selectedValue = optionsList[selectedOption],
                            onSelected = { selectedValue ->
                                selectedOption = optionsList.indexOf(selectedValue)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spacerHeight))

                optionalMessage?.let { message ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center,
                        text = message
                    )
                    Spacer(modifier = Modifier.height(spacerHeight))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Button(
                        onClick = {
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Button(
                        onClick = {
                            onSubmitButtonClick.invoke(selectedOption)
                            onDismissRequest.invoke()
                        },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = stringResource(android.R.string.ok))
                    }
                }
            }
        }
    }
}