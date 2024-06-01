package com.alad1nks.productsandroid.core.network

import com.alad1nks.productsandroid.core.network.model.ProductResponse
import com.alad1nks.productsandroid.core.network.model.ProductsResponse
import io.reactivex.rxjava3.core.Single

interface NetworkDataSource {
    fun getProducts(search: String): Single<ProductsResponse>
    fun getProduct(id: Int): Single<ProductResponse>
}
