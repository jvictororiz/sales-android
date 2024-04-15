package br.com.joaov.persistence.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithProducts(
    @Embedded
    val orderEntity: OrderEntity,
    @Relation(
        parentColumn = "uidOrder",
        entityColumn = "foreignKeyOrder"
    )
    val salesEntity: List<SaleEntity>
)