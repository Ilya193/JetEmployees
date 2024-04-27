package ru.ikom.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ikom.common.Gray
import ru.ikom.common.Purple
import ru.ikom.home.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticTopAppBar() {
    TopAppBar(title = {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .height(55.dp)
                    .weight(1f)
                    .padding(start = 8.dp, end = 24.dp),
                value = "",
                onValueChange = {},
                placeholder = { Text(text = stringResource(R.string.enter_the_name_tag_email)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_sorted),
                        contentDescription = null
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Gray,
                    unfocusedContainerColor = Gray,
                    focusedContainerColor = Gray,
                    focusedIndicatorColor = Gray,
                ),
                shape = RoundedCornerShape(24.dp)
            )
        }
    })
}