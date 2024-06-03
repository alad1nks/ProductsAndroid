package com.alad1nks.productsandroid.core.data.repository

import com.alad1nks.productsandroid.core.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getProductListFlow(): Flow<List<Product>>

    fun getProductBrandListFlow(): Flow<List<String>>

    suspend fun refreshProducts()
}
