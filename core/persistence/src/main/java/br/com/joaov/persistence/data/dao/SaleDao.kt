package br.com.joaov.persistence.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import br.com.joaov.persistence.data.model.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {

    @Insert
    fun insertSale(orderProducts: List<SaleEntity>)

    @Query("DELETE FROM saleentity WHERE foreignKeyOrder = :id")
    fun deleteById(id: Int)

    @Transaction
    @Query("SELECT * FROM saleentity")
    fun getAllSales(): Flow<List<SaleEntity>>
}