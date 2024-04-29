package ru.ikom.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ikom.home.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiseModalBottomSheet(value: FilterMode?, onDismiss: (FilterMode?) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val alphabeticallyState = remember { mutableStateOf(false) }
    val dateOfBirthState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (value == FilterMode.ALPHABET) alphabeticallyState.value = true
        else if (value == FilterMode.DATE_OF_BIRTH) dateOfBirthState.value = true
    }

    ModalBottomSheet(onDismissRequest = {
        var value: FilterMode? = null
        if (alphabeticallyState.value) value = FilterMode.ALPHABET
        if (dateOfBirthState.value) value = FilterMode.DATE_OF_BIRTH
        onDismiss(value)
    }, sheetState = sheetState) {
        Column(
            modifier = Modifier.fillMaxWidth().height(250.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.sorting),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 19.sp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    modifier = Modifier.clip(CircleShape),
                    checked = alphabeticallyState.value,
                    onCheckedChange = {
                        alphabeticallyState.value = it
                        if (it) dateOfBirthState.value = false
                    })
                Text(
                    text = stringResource(R.string.sorting_alphabetically),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    modifier = Modifier.clip(CircleShape),
                    checked = dateOfBirthState.value, onCheckedChange = {
                        dateOfBirthState.value = it
                        if (it) alphabeticallyState.value = false
                    })
                Text(
                    text = stringResource(R.string.sorting_date_of_birth),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }
        }
    }
}