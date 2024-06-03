package com.alad1nks.productsandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alad1nks.productsandroid.core.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Query("SELECT * FROM product")
    fun getProductListFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM product")
    fun getProductList(): List<ProductEntity>

    @Query("SELECT DISTINCT brand FROM product ORDER BY brand")
    fun getProductBrandList(): Flow<List<String>>

    @Query("SELECT * FROM product WHERE id = :id LIMIT 1")
    suspend fun getProductFlow(id: Int): ProductEntity

    @Insert
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM product")
    suspend fun clearProducts()
}
