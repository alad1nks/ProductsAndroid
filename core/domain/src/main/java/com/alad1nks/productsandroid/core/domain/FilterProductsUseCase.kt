package com.alad1nks.productsandroid.core.domain

import com.alad1nks.productsandroid.core.model.Product
import javax.inject.Inject

class FilterProductsUseCase @Inject constructor() {

    operator fun invoke(
        productList: List<Product>,
        search: String
    ): List<Product> {
        return productList.filter {
            it.title.startsWith(
                prefix = search,
                ignoreCase = true
            )
        }
    }
}
