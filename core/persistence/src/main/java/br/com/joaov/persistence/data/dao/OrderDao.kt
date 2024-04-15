package br.com.joaov.persistence.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.joaov.persistence.data.model.OrderEntity
import br.com.joaov.persistence.data.model.OrderWithProducts
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
    @Query("SELECT seq FROM sqlite_sequence WHERE name= 'OrderEntity'")
    fun getNextId(): Int

    @Transaction
    @Query("SELECT * FROM orderentity WHERE uidOrder = :id")
    fun getById(id: Int): Flow<OrderWithProducts>
}