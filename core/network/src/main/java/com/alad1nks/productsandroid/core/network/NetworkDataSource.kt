package com.alad1nks.productsandroid.core.network

import com.alad1nks.productsandroid.core.network.model.ProductsResponse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface NetworkDataSource {
    suspend fun getProducts(): ProductsResponse
}
