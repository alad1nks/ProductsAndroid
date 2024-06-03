package com.alad1nks.productsandroid.feature.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.alad1nks.productsandroid.core.designsystem.components.DropdownIcon
import com.alad1nks.productsandroid.core.designsystem.components.ErrorScreen
import com.alad1nks.productsandroid.core.designsystem.components.SearchBar
import com.alad1nks.productsandroid.core.model.Brand
import com.alad1nks.productsandroid.core.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductsRoute(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val shouldEndRefresh by viewModel.shouldEndRefresh.collectAsState()
    val brandList by viewModel.brandList.collectAsState()

    ProductsScreen(
        onSwipe = { viewModel.refresh(true) },
        shouldEndRefresh = shouldEndRefresh,
        onRefreshEnded = { viewModel.onRefreshEnded() },
        onThemeIconClick = { viewModel.changeTheme() },
        searchValue = searchQuery,
        brandList = brandList,
        onBrandSelect = { viewModel.selectBrand(it) },
        onSearchValueChange = { viewModel.search(it) },
        uiState = uiState,
        onItemClick = onItemClick,
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
    brandList: List<Brand>,
    onBrandSelect: (Int) -> Unit,
    onSearchValueChange: (String) -> Unit,
    uiState: ProductsUiState,
    onItemClick: (Int) -> Unit,
    darkTheme: Boolean,
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier,
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState()
) {
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onSwipe()
        }
    }

    if (shouldEndRefresh) {
        LaunchedEffect(true) {
            onRefreshEnded()
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ProductsTopBar(
                darkTheme = darkTheme,
                onThemeIconClick = onThemeIconClick,
                searchValue = searchValue,
                brandList = brandList,
                onBrandSelect = onBrandSelect,
                onSearchValueChange = onSearchValueChange
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
                .padding(padding)
        ) {
            ProductsContent(
                uiState = uiState,
                onItemClick = onItemClick,
                onTryAgainClick = onTryAgainClick,
                modifier = Modifier
                    .fillMaxSize()
            )
            PullToRefreshContainer(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                state = pullToRefreshState
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
    brandList: List<Brand>,
    onBrandSelect: (Int) -> Unit,
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

            DropdownIcon(
                imageVector = Icons.Filled.MoreVert
            ) {
                brandList.forEachIndexed { index, brand ->
                    val interactionSource = remember { MutableInteractionSource() }
                    with(brand) {
                        DropdownMenuItem(
                            text = { Text(text = name) },
                            onClick = { onBrandSelect(index) },
                            trailingIcon = {
                                Checkbox(
                                    checked = applied,
                                    onCheckedChange = {  },
                                    interactionSource = interactionSource
                                )
                            },
                            interactionSource = interactionSource
                        )
                    }
                }
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
    onTryAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is ProductsUiState.Data -> {
            ProductsData(
                products = uiState.products,
                onClickItem = onItemClick,
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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(products) {  product ->
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
