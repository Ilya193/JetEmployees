package ru.ikom.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ikom.home.R

@Composable
fun NothingFoundSearch() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))
        Image(
            modifier = Modifier.size(56.dp, 56.dp),
            painter = painterResource(R.drawable.ic_nothing_found),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.nothing_found),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 17.sp)
        )
        Text(
            text = stringResource(R.string.correct_request),
            style = TextStyle(color = Color.Gray, fontSize = 15.sp)
        )
    }
}