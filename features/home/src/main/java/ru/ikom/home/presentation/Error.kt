package ru.ikom.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import ru.ikom.common.Purple
import ru.ikom.home.R

@Composable
fun Error(onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(56.dp, 56.dp),
            painter = painterResource(R.drawable.ic_load_error),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.something_broke),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 17.sp)
        )
        Text(
            text = stringResource(R.string.fix_it),
            style = TextStyle(color = Color.Gray, fontSize = 15.sp)
        )
        Text(
            modifier = Modifier.clickable(
                onClick = onClick),
            text = stringResource(R.string.retry),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Purple)
        )
    }
}