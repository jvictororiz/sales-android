package br.com.joaov.persistence.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val uidProduct: Int = 0,
    val name: String = "",
    val unitValue: Double = 0.0,
)