package com.alad1nks.productsandroid.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alad1nks.productsandroid.core.database.converters.Converters
import com.alad1nks.productsandroid.core.database.dao.ProductsDao
import com.alad1nks.productsandroid.core.database.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ProductsDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDao
}
