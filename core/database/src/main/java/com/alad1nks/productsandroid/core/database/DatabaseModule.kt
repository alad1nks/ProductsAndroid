package com.alad1nks.productsandroid.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesProductsDatabase(
        @ApplicationContext context: Context,
    ): ProductsDatabase = Room.databaseBuilder(
        context,
        ProductsDatabase::class.java,
        "products-database",
    ).build()
}
