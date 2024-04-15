package br.com.joaov.persistence.data.mapper

import br.com.joaov.persistence.domain.model.ProductModel
import br.com.joaov.persistence.data.model.ProductEntity


fun ProductEntity.toProductModel() = ProductModel(
    uid = uidProduct,
    name = name,
    unitValue = unitValue
)

fun ProductModel.toProductEntity() = ProductEntity(
    uidProduct = uid,
    name = name,
    unitValue = unitValue
)