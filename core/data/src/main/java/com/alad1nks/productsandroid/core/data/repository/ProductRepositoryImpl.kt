package com.alad1nks.productsandroid.core.data.repository

import com.alad1nks.productsandroid.core.database.dao.ProductsDao
import com.alad1nks.productsandroid.core.database.entity.ProductEntity
import com.alad1nks.productsandroid.core.model.ProductInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductsDao
) : ProductRepository {
    override suspend fun getProduct(id: Int): ProductInfo {
        return dao.getProductFlow(id).toModel()
    }

    private fun ProductEntity.toModel(): ProductInfo {
        return ProductInfo(
            title = title,
            description = description,
            price = price,
            discountPercentage = discountPercentage,
            rating = rating,
            stock = stock,
            brand = brand,
            category = category,
            thumbnail = thumbnail,
            images = images
        )
    }
}
