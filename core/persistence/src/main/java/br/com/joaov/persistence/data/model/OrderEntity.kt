package br.com.joaov.persistence.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val uidOrder: Int? = 0,
    val clientName: String,
    val timestamp: Long,
    val totalValue: Double
)