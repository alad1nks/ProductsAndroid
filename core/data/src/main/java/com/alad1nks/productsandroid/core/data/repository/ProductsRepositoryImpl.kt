package com.alad1nks.productsandroid.core.data.repository

import android.util.Log
import com.alad1nks.productsandroid.core.database.dao.ProductsDao
import com.alad1nks.productsandroid.core.database.entity.ProductEntity
import com.alad1nks.productsandroid.core.model.Product
import com.alad1nks.productsandroid.core.network.NetworkDataSource
import com.alad1nks.productsandroid.core.network.model.ProductsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val dataSource: NetworkDataSource,
    private val dao: ProductsDao
) : ProductsRepository {
    override fun getProductListFlow(): Flow<List<Product>> {
        Log.v("getProductListFlow", "Entering function getProductListFlow()")
        return dao.getProductListFlow().map { it.asModel() }
    }

    override fun getProductBrandListFlow(): Flow<List<String>> {
        Log.v("getProductBrandListFlow", "Entering function getProductBrandListFlow()")
        return dao.getProductBrandList()
    }

    override suspend fun refreshProducts() {
        Log.v("refreshProducts", "Entering function refreshProducts()")

        try {
            val productsResponse = dataSource.getProducts()
            val productsResponseAsEntity = productsResponse.asEntity()
            Log.i("refreshProducts", "productsResponseAsEntity: $productsResponseAsEntity")

            val productEntityList = dao.getProductList()
            Log.i("refreshProducts", "productEntityList: $productEntityList")

            if (productsResponseAsEntity != productEntityList) {
                Log.v("refreshProducts", "Updating products from network")
                dao.clearProducts()
                dao.insertProducts(productsResponseAsEntity)
            } else {
                Log.v("refreshProducts", "Product lists are equal")
            }
        } catch (e: Exception) {
            Log.e("ProductsRepository", "Error refreshing products: ${e.message}", e)

            when (e) {
                is IOException -> {
                    Log.e("ProductViewModel", "Network error: ${e.message}", e)
                }
                else -> {
                    Log.e("ProductViewModel", "Unexpected error: ${e.message}", e)
                }
            }
        }
    }

    private fun List<ProductEntity>.asModel(): List<Product> {
        return map { product ->
            with(product) {
                Product(
                    id = id,
                    title = title,
                    description = description,
                    price = price,
                    brand = brand,
                    thumbnail = thumbnail
                )
            }
        }
    }

    private fun ProductsResponse.asEntity(): List<ProductEntity> {
        return products.map { product ->
            with(product) {
                ProductEntity(
                    id = id,
                    title = title ?: "",
                    description = description ?: "",
                    price = price ?: 0.0,
                    discountPercentage = discountPercentage ?: 0.0,
                    rating = rating ?: 0.0,
                    stock = stock ?: 0,
                    brand = brand ?: "???",
                    category = category ?: "",
                    thumbnail = thumbnail ?: "",
                    images = images ?: emptyList()
                )
            }
        }
    }
}
