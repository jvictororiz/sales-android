package br.com.joaov.persistence.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.joaov.persistence.data.model.OrderEntity
import br.com.joaov.persistence.data.model.OrderWithProducts
import br.com.joaov.persistence.data.model.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(orderProducts: OrderEntity): Long

    @Query("delete from orderentity where uidOrder = :id")
    fun delete(id: Int)

    @Transaction
    @Query("SELECT * FROM orderentity")
    fun getAll(): Flow<List<OrderWithProducts>>

    @Transaction
    @Query("SELECT * FROM orderentity WHERE uidOrder = :id")
    fun getById(id: Int): Flow<OrderWithProducts>

    @Transaction
    @Query("SELECT * FROM saleentity")
    fun getAllSales(): Flow<List<SaleEntity>>
}