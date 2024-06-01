package com.alad1nks.productsandroid.core.data.repository

import com.alad1nks.productsandroid.core.model.Product
import io.reactivex.rxjava3.core.Single

interface ProductsRepository {
    fun getProducts(search: String): Single<List<Product>>
}
