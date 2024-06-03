package com.alad1nks.productsandroid.feature.products

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.productsandroid.core.data.repository.ProductsRepository
import com.alad1nks.productsandroid.core.data.repository.UserDataRepository
import com.alad1nks.productsandroid.core.domain.FilterProductsUseCase
import com.alad1nks.productsandroid.core.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val userDataRepository: UserDataRepository,
    private val filterProductsUseCase: FilterProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery.asStateFlow()

    private val _shouldEndRefresh = MutableStateFlow(false)
    val shouldEndRefresh: StateFlow<Boolean> get() = _shouldEndRefresh.asStateFlow()

    val darkTheme: StateFlow<Boolean> = userDataRepository.userData.map { it.darkTheme }
        .stateIn(
            scope = viewModelScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(5_000)
        )

    init {
        fetchAndFilterProducts()
        refresh()
    }

    fun refresh(swipe: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.refreshProducts()
            if (swipe) {
                _shouldEndRefresh.emit(true)
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun changeTheme() {
        viewModelScope.launch {
            userDataRepository.changeTheme()
        }
    }

    fun onRefreshEnded() {
        _shouldEndRefresh.value = false
    }

    private fun fetchAndFilterProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .onStart {
                    _uiState.value = ProductsUiState.Loading
                }
                .catch {
                    _uiState.value = ProductsUiState.Error
                }
                .combine(_searchQuery) { productList, search ->
                    val filteredProductList = filterProductsUseCase(productList, search)
                    Log.i("filteredProductList", "$filteredProductList")
                    ProductsUiState.Data(filteredProductList)
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}

sealed interface ProductsUiState {
    data object Loading : ProductsUiState

    data class Data(
        val products: List<Product>
    ) : ProductsUiState

    data object Error : ProductsUiState
}
