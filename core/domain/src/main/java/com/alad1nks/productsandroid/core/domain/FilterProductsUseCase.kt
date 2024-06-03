package com.alad1nks.productsandroid.core.domain

import com.alad1nks.productsandroid.core.model.Brand
import com.alad1nks.productsandroid.core.model.Product
import javax.inject.Inject

class FilterProductsUseCase @Inject constructor() {

    operator fun invoke(
        productList: List<Product>,
        search: String,
        brandList: List<Brand>
    ): List<Product> {

        val brandSet = mutableSetOf<String>()

        brandList.forEach {
            if (it.applied) {
                brandSet.add(it.name)
            }
        }

        return if (brandSet.isEmpty()) {
            productList.filter {
                it.title.startsWith(
                    prefix = search,
                    ignoreCase = true
                )
            }
        } else {
            productList.filter {
                it.title.startsWith(search, true) && brandSet.contains(it.brand)
            }
        }


    }
}
