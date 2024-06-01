package com.alad1nks.productsandroid.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Int,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val discountPercentage: Double? = null,
    val rating: Double? = null,
    val stock: Int? = null,
    val brand: String? = null,
    val category: String? = null,
    val thumbnail: String? = null,
    val images: List<String>? = null
)
