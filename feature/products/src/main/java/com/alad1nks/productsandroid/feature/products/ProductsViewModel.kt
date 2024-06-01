package com.alad1nks.productsandroid.feature.products

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.productsandroid.core.data.repository.ProductsRepository
import com.alad1nks.productsandroid.core.data.repository.UserDataRepository
import com.alad1nks.productsandroid.core.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductsRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _uiState: MutableLiveData<ProductsUiState> = MutableLiveData()
    val uiState: LiveData<ProductsUiState> = _uiState

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = _searchQuery
    private val searchQuerySubject = PublishSubject.create<String>()

    private val _shouldEndRefresh = MutableLiveData(false)
    val shouldEndRefresh: LiveData<Boolean> get() = _shouldEndRefresh

    val darkTheme: Flow<Boolean> = userDataRepository.userData.map { it.darkTheme }

    init {
        disposables.add(
            searchQuerySubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMapSingle { query ->
                    repository.getProducts(query)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { items -> _uiState.value = ProductsUiState.Data(items) },
                    { e ->
                        Log.d("error", e.toString())
                        _uiState.value = ProductsUiState.Error
                    }
                )
        )
        refresh()
    }

    fun refresh(swipe: Boolean = false) {
        _uiState.value = ProductsUiState.Loading
        disposables.add(
            repository.getProducts(searchQuery.value ?: "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { items ->
                        val products = when (val state = uiState.value) {
                            is ProductsUiState.Data -> state.products + items
                            else -> items
                        }
                        _uiState.value = ProductsUiState.Data(products)
                        if (swipe) { _shouldEndRefresh.value = true }
                    },
                    { e ->
                        Log.d("error", e.toString())
                        _uiState.value = ProductsUiState.Error
                        if (swipe) { _shouldEndRefresh.value = true }
                    }
                )
        )
    }

    fun search(query: String) {
        _searchQuery.value = query
        searchQuerySubject.onNext(query)
    }

    fun changeTheme() {
        viewModelScope.launch {
            userDataRepository.changeTheme()
        }
    }

    fun onRefreshEnded() {
        _shouldEndRefresh.value = false
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}

sealed interface ProductsUiState {
    data object Loading : ProductsUiState

    data class Data(
        val products: List<Product>
    ) : ProductsUiState

    data object Error : ProductsUiState
}
