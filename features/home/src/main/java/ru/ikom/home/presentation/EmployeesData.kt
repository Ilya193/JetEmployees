package ru.ikom.home.presentation

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesData(
    state: EmployeesUiState.Data,
    departments: List<DepartmentUi>,
    refreshState: Boolean,
    inputState: String,
    selectDepartment: (Int) -> Unit,
    refresh: () -> Unit,
    launchDialog: () -> Unit,
    input: (String) -> Unit,
    cancel: () -> Unit,
    onClick: (EmployeeUi) -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()
    val scrollState = rememberLazyListState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            refresh()
        }
    }

    LaunchedEffect(state) {
        scrollState.animateScrollToItem(0)
    }

    LaunchedEffect(refreshState) {
        if (!refreshState) pullRefreshState.endRefresh()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.nestedScroll(pullRefreshState.nestedScrollConnection),
            topBar = {
                DynamicInputTopAppBar(state = inputState, onValueChange = input, launchDialog = launchDialog, cancel = cancel)
            }) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxWidth()) {
                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.Center),
                    state = pullRefreshState,
                )
            }
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    itemsIndexed(
                        departments,
                        key = { index, item -> item.id }) { index, item ->
                        DepartmentItem(item = item, onClick = { selectDepartment(index) })
                    }
                }

                if (state.nothingFound == NothingFound.SEARCH) NothingFoundSearch()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = scrollState
                ) {
                    items(state.employees, key = { item -> item.id }) {
                        EmployeeItem(employee = it) { onClick(it) }
                    }
                }
            }
        }
    }
}