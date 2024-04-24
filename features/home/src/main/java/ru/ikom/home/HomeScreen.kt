package ru.ikom.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ru.ikom.common.Gray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    var inputEmployee by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchEmployees()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(topBar = {
            TopAppBar(title = {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 8.dp, end = 24.dp),
                    value = inputEmployee,
                    onValueChange = {
                        inputEmployee = it
                    },
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
            })
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    itemsIndexed(
                        state.categories,
                        key = { index, item -> item.id }) { index, item ->
                        CategoryItem(item = item) {
                            viewModel.action(Event.SelectCategory(index))
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(state.employees, key = { item -> item.id }) {
                        EmployeeItem(employee = it)
                    }
                }
            }
        }
    }
}