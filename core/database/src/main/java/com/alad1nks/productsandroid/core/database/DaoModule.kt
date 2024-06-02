package com.alad1nks.productsandroid.core.database

import com.alad1nks.productsandroid.core.database.dao.ProductsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesTopicsDao(
        database: ProductsDatabase,
    ): ProductsDao = database.productsDao()
}
