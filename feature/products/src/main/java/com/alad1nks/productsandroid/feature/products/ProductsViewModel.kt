package com.alad1nks.productsandroid.feature.products

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.productsandroid.core.data.repository.ProductsRepository
import com.alad1nks.productsandroid.core.data.repository.UserDataRepository
import com.alad1nks.productsandroid.core.domain.FilterProductsUseCase
import com.alad1nks.productsandroid.core.model.Brand
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

    private val _brandList = MutableStateFlow<List<Brand>>(emptyList())
    val brandList: StateFlow<List<Brand>> get() = _brandList.asStateFlow()

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
        fetchBrands()
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

    fun selectBrand(index: Int) {
        val updatedList = _brandList.value.toMutableList().apply {
            val applied = !this[index].applied
            this[index] = this[index].copy(applied = applied)
        }.toList()

        _brandList.value = updatedList
    }

    private fun fetchAndFilterProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                repository.getProductListFlow(),
                _searchQuery,
                _brandList
            ) { productList, search, brandList ->
                val filteredProductList = filterProductsUseCase(productList, search, brandList)
                Log.i("filteredProductList", "$filteredProductList")
                ProductsUiState.Data(filteredProductList)
            }.onStart {
                Log.v("fetchAndFilterProducts", "Entering onStart")
                _uiState.value = ProductsUiState.Loading
            }.catch {e ->
                Log.e("fetchAndFilterProducts", "Error fetching and filtering products: $e")
                _uiState.value = ProductsUiState.Error
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun fetchBrands() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProductBrandListFlow().map { brandList ->
                brandList.map { name -> Brand(name, false) }
            }.collect { brands ->
                _brandList.value = brands
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
