package com.alad1nks.productsandroid.feature.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.alad1nks.productsandroid.core.designsystem.components.AnimatedShimmerListItem
import com.alad1nks.productsandroid.core.designsystem.components.ErrorScreen
import com.alad1nks.productsandroid.core.designsystem.components.SearchBar
import com.alad1nks.productsandroid.core.model.Product

@Composable
internal fun ProductsRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(initial = ProductsUiState.Loading)
    val darkTheme by viewModel.darkTheme.collectAsState(initial = false)
    val searchQuery by viewModel.searchQuery.observeAsState(initial = "")
    val shouldEndRefresh by viewModel.shouldEndRefresh.observeAsState(initial = false)

    ProductsScreen(
        onSwipe = { viewModel.refresh(true) },
        shouldEndRefresh = shouldEndRefresh,
        onRefreshEnded = { viewModel.onRefreshEnded() },
        onThemeIconClick = { viewModel.changeTheme() },
        searchValue = searchQuery,
        onSearchValueChange = { viewModel.search(it) },
        uiState = uiState,
        onItemClick = onItemClick,
        onScroll = { viewModel.loadMore(it) },
        darkTheme = darkTheme,
        onTryAgainClick = { viewModel.refresh() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsScreen(
    onSwipe: () -> Unit,
    shouldEndRefresh: Boolean,
    onRefreshEnded: () -> Unit,
    onThemeIconClick: () -> Unit,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    uiState: ProductsUiState,
    onItemClick: (Int) -> Unit,
    onScroll: (Int) -> Unit,
    darkTheme: Boolean,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            onSwipe()
        }
    }
    if (shouldEndRefresh) {
        LaunchedEffect(true) {
            onRefreshEnded()
            refreshState.endRefresh()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ProductsTopBar(
                darkTheme = darkTheme,
                onThemeIconClick = onThemeIconClick,
                searchValue = searchValue,
                onSearchValueChange = onSearchValueChange
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .nestedScroll(refreshState.nestedScrollConnection)
                .padding(padding)
        ) {
            ProductsContent(
                uiState = uiState,
                onItemClick = onItemClick,
                onScroll = onScroll,
                onTryAgainClick = onTryAgainClick,
                modifier = Modifier
                    .fillMaxSize()
            )
            PullToRefreshContainer(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                state = refreshState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsTopBar(
    darkTheme: Boolean,
    onThemeIconClick: () -> Unit,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchBar by remember { mutableStateOf(searchValue.isNotEmpty()) }

    TopAppBar(
        title = {
            if (!showSearchBar) {
                Text(stringResource(R.string.products))
            }

            AnimatedVisibility(
                visible = showSearchBar,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                SearchBar(
                    value = searchValue,
                    onValueChange = onSearchValueChange,
                    onSearchClose = {
                        onSearchValueChange("")
                        showSearchBar = false
                    }
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onThemeIconClick) {
                Icon(
                    imageVector = if (darkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                    contentDescription = stringResource(R.string.icon_app_theme)
                )
            }
        },
        actions = {
            AnimatedVisibility(
                visible = !showSearchBar,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                IconButton(onClick = { showSearchBar = true }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_icon)
                    )
                }
            }
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.more_icon)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
internal fun ProductsContent(
    uiState: ProductsUiState,
    onItemClick: (Int) -> Unit,
    onScroll: (Int) -> Unit,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is ProductsUiState.Data -> {
            ProductsData(
                products = uiState.products,
                onClickItem = onItemClick,
                onScroll = onScroll,
                modifier = modifier
            )
        }

        ProductsUiState.Loading -> {
            ProductsLoading(
                modifier = modifier
            )
        }

        ProductsUiState.Error -> {
            ProductsError(
                onTryAgainClick = onTryAgainClick,
                modifier = modifier
            )
        }
    }
}

@Composable
internal fun ProductsData(
    products: List<Product>,
    onClickItem: (Int) -> Unit,
    onScroll: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(products) { index, product ->
            ListItem(
                headlineContent = {
                    Text(
                        text = product.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier
                    .clickable { onClickItem(product.id) },
                supportingContent = {
                    Text(
                        text = product.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    Text(
                        text = "\$${product.price}",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                leadingContent = {
                    AsyncImage(
                        model = product.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(30)),
                        contentScale = ContentScale.Crop
                    )
                }
            )
            if (index >= products.size - 1) {
                onScroll(products.size)
            }
        }
    }
}

@Composable
internal fun ProductsLoading(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(20) {
            AnimatedShimmerListItem()
        }
    }
}

@Composable
internal fun ProductsError(
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorScreen(
        onTryAgainClick = onTryAgainClick,
        modifier = modifier
    )
}
