package com.alad1nks.productsandroid.core.database.entity

import androidx.room.Entity

@Entity(tableName = "product")
data class ProductEntity(
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String,
    val category: String,
    val thumbnail: String,
    val images: List<String>
)
