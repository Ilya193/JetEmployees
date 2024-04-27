package ru.ikom.home.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.ikom.common.Gray
import ru.ikom.common.Purple
import ru.ikom.home.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val pullRefreshState = rememberPullToRefreshState()
    val scrollState = rememberLazyListState()

    var inputEmployee by remember { mutableStateOf("") }

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            inputEmployee = ""
            viewModel.action(Event.Refresh)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.action(Event.Fetch)

        snapshotFlow {
            state
        }.collect {
            scrollState.scrollToItem(0)
            if (!state.showRefresh) {
                pullRefreshState.endRefresh()
            }
        }
    }

    if (state.isError) Error { viewModel.action(Event.Fetch) }
    else {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.nestedScroll(pullRefreshState.nestedScrollConnection),
                topBar = {
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
                                value = inputEmployee,
                                onValueChange = {
                                    inputEmployee = it
                                    viewModel.action(Event.Input(inputEmployee))
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
                                        modifier = Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            viewModel.action(Event.Dialog(true))
                                        },
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
                            if (inputEmployee.isNotEmpty())
                                Text(
                                    modifier = Modifier.weight(0.25f)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            inputEmployee = ""
                                            viewModel.action(Event.Cancel)
                                        },
                                    text = stringResource(R.string.cancel),
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Purple
                                    )
                                )
                        }

                    })
                }) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues).fillMaxWidth()) {
                    PullToRefreshContainer(
                        modifier = Modifier.align(Alignment.Center),
                        state = pullRefreshState,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        itemsIndexed(
                            state.departments,
                            key = { index, item -> item.id }) { index, item ->
                            CategoryItem(item = item) {
                                viewModel.action(Event.SelectCategory(index))
                            }
                        }
                    }

                    if (state.nothingFound == NothingFound.SEARCH) NothingFoundSearch()

                    if (state.isLoading) LoadingEmployees()
                    else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            state = scrollState
                        ) {
                            items(state.employees, key = { item -> item.id }) {
                                EmployeeItem(employee = it)
                            }
                        }

                        if (state.showDialog)
                            ChoiseModalBottomSheet(value = state.filter, onDismiss = {
                                if (it == null) inputEmployee = ""
                                viewModel.action(Event.Filter(it))
                            })
                    }
                }
            }
        }
    }
}

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