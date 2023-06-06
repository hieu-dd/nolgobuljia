package com.noljanolja.android.features.shop.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.features.shop.composable.CouponItem
import com.noljanolja.android.features.shop.composable.ProductItem
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SearchBar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.UserPoint
import com.noljanolja.android.ui.theme.helpIconColor
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.ui.theme.withMedium
import com.noljanolja.android.util.formatDigitsNumber
import org.koin.androidx.compose.getViewModel

@Composable
fun ShopScreen(
    viewModel: ShopViewModel = getViewModel(),
) {
    ShopContent(handleEvent = viewModel::handleEvent)
}

@Composable
private fun ShopContent(
    handleEvent: (ShopEvent) -> Unit,
) {
    ScaffoldWithUiState(
        uiState = UiState<Any>()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            SearchProductHeader(
                goToSearch = { handleEvent(ShopEvent.Search) }
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    SizeBox(height = 10.dp)
                    UserPoint(
                        point = 100000.formatDigitsNumber(),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    SizeBox(height = 16.dp)
                }
                item {
                    ExchangeCoupons()
                }
                item {
                    SizeBox(height = 30.dp)
                }
                shop()
            }
        }
    }
}

@Composable
private fun SearchProductHeader(
    goToSearch: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome to Nolja shop!",
                style = MaterialTheme.typography.titleSmall.withMedium()
            )
            Icon(
                Icons.Default.Help,
                contentDescription = null,
                tint = MaterialTheme.helpIconColor(),
                modifier = Modifier.size(16.dp)
            )
        }
        SizeBox(height = 5.dp)
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { goToSearch.invoke() },
            searchText = "",
            hint = "Search products",
            onSearch = {},
            enabled = false,
        )
    }
}

@Composable
private fun ExchangeCoupons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Exchanged Coupons",
            style = MaterialTheme.typography.bodyLarge.withBold(),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Expanded()
        Text(
            text = "View all",
            style = MaterialTheme.typography.bodyLarge.withBold(),
            color = Color.White
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
    SizeBox(height = 10.dp)
    LazyRow(modifier = Modifier.padding(start = 16.dp)) {
        items(count = 10) { index ->
            CouponItem(modifier = Modifier.width(130.dp))
            SizeBox(width = 12.dp)
        }
    }
}

fun LazyListScope.shop() {
    val items = listOf(1, 2, 3, 4, 5, 6, 7)
    item {
        Text(
            text = "Shop",
            style = MaterialTheme.typography.bodyLarge.withBold(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        SizeBox(height = 10.dp)
    }
    items(count = (items.size + 1) / 2) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ProductItem(index = row * 2, modifier = Modifier.weight(1F))
            SizeBox(width = 12.dp)
            if (items.getOrNull(row * 2 + 1) != null) {
                ProductItem(index = row * 2 + 1, modifier = Modifier.weight(1F))
            } else {
                Box(modifier = Modifier.weight(1F))
            }
        }
        SizeBox(height = 20.dp)
    }
}