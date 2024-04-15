package br.com.joaov.persistence.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val uidSale : Int = 0,
    val foreignKeyOrder: Int = 0,
    @Embedded
    val product: ProductEntity,
    val quantity: Int
)