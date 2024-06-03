package com.alad1nks.productsandroid.feature.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.productsandroid.core.data.repository.ProductRepository
import com.alad1nks.productsandroid.core.model.ProductInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState: MutableLiveData<ProductUiState> = MutableLiveData(ProductUiState.Loading)
    val uiState: LiveData<ProductUiState> = _uiState

    fun refresh(id: Int) {
        _uiState.value = ProductUiState.Loading
        viewModelScope.launch {
            val product = withContext(Dispatchers.IO) {
                repository.getProduct(id)
            }
            _uiState.value = ProductUiState.Data(product)
        }
    }
}

sealed interface ProductUiState {
    data object Loading : ProductUiState

    data class Data(
        val product: ProductInfo
    ) : ProductUiState

    data object Error : ProductUiState
}
