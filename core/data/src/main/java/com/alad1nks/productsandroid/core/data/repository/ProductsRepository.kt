package com.alad1nks.productsandroid.core.data.repository

import com.alad1nks.productsandroid.core.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    fun getProducts(): Flow<List<Product>>

    suspend fun refreshProducts()
}
