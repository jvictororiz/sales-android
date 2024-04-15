package br.com.joaov.persistence.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.joaov.persistence.data.dao.OrderDao
import br.com.joaov.persistence.data.dao.ProductDao
import br.com.joaov.persistence.data.dao.SaleDao
import br.com.joaov.persistence.data.model.OrderEntity
import br.com.joaov.persistence.data.model.ProductEntity
import br.com.joaov.persistence.data.model.SaleEntity

@Database(exportSchema = false, entities = [OrderEntity::class, ProductEntity::class, SaleEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun orderDao(): OrderDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao


    companion object {
        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "app-database"
            ).build()
        }
    }
}