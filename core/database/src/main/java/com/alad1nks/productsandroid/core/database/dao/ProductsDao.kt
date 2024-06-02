package com.alad1nks.productsandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alad1nks.productsandroid.core.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Query("SELECT * FROM product")
    fun getProducts(): Flow<List<ProductEntity>>

    @Insert
    fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM product")
    fun clearProducts()
}
