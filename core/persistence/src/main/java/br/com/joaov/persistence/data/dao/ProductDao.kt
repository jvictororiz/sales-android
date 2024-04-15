package br.com.joaov.persistence.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.joaov.persistence.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg sales: ProductEntity)

    @Query("DELETE FROM productentity WHERE uidProduct = :idProduct")
    fun deleteProduct(idProduct: Int)

    @Transaction
    @Query("SELECT * FROM productentity")
    fun getAll(): Flow<List<ProductEntity>>

}