package com.alad1nks.productsandroid.core.data.repository

import com.alad1nks.productsandroid.core.model.ProductInfo
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProduct(id: Int): ProductInfo
}
