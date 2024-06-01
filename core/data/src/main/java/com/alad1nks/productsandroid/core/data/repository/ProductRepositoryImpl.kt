package com.alad1nks.productsandroid.core.data.repository

import com.alad1nks.productsandroid.core.model.ProductInfo
import com.alad1nks.productsandroid.core.network.NetworkDataSource
import com.alad1nks.productsandroid.core.network.model.ProductResponse
import io.reactivex.rxjava3.core.Single
import java.io.IOException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dataSource: NetworkDataSource
) : ProductRepository {
    override fun getProduct(id: Int): Single<ProductInfo> {
        return dataSource.getProduct(id)
            .map { response -> response.toModel() }
            .onErrorResumeNext { throwable: Throwable ->
                if (throwable is IOException) {
                    Single.error(Exception("Network error occurred"))
                } else {
                    Single.error(throwable)
                }
            }
    }

    private fun ProductResponse.toModel(): ProductInfo {
        return ProductInfo(
            title = title ?: "",
            description = description ?: "",
            price = price ?: 0.0,
            discountPercentage = discountPercentage ?: 0.0,
            rating = rating ?: 0.0,
            stock = stock ?: 0,
            brand = brand ?: "",
            category = category ?: "",
            thumbnail = thumbnail ?: "",
            images = images ?: emptyList()
        )
    }
}
