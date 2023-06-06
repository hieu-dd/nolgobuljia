package com.noljanolja.android.features.shop.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.withMedium
import org.koin.androidx.compose.getViewModel

@Composable
fun SearchProductScreen(
    viewModel: SearchProductViewModel = getViewModel(),
) {
    val searchKeys by viewModel.searchKeys.collectAsStateWithLifecycle()
    SearchProductContent(
        searchKeys = searchKeys,
        handleEvent = viewModel::handleEvent
    )
}

@Composable
private fun SearchProductContent(
    searchKeys: List<String>,
    handleEvent: (SearchProductEvent) -> Unit,
) {
    var isSearchFocus by remember {
        mutableStateOf(false)
    }

    Column {
        SearchProductHeader(
            onSearch = {
                handleEvent(SearchProductEvent.Search(it))
            },
            onFocusChange = {
                isSearchFocus = it
            },
            onBack = {
                handleEvent(SearchProductEvent.Back)
            }
        )
        if (isSearchFocus) {
            searchKeys.forEach {
                Text(text = it)
            }
        } else {
            Text(text = "4567")
        }
    }
}

@Composable
private fun SearchProductHeader(
    onSearch: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var searchText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome to Nolja shop!",
                style = MaterialTheme.typography.titleSmall.withMedium(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        SizeBox(height = 5.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable {
                    onBack.invoke()
                }
            )
            SizeBox(width = 15.dp)
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                searchText = searchText,
                hint = "Search products",
                onSearch = {
                    searchText = it
                },
                background = MaterialTheme.colorScheme.background,
                onFocusChange = {
                    onFocusChange.invoke(it.isFocused)
                },
                onSearchButton = {
                    onSearch(searchText)
                    focusManager.clearFocus()
                },
                focusRequester = focusRequester
            )
        }
    }
}