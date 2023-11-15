package com.abrnoc.application.presentation.screens.purchase

data class ShoppingListItem(
    val createdDate: String,
    val features: List<Feature>,
    val hasOptions: Boolean,
    val id: Int,
    val modifiedDate: String,
    val period: Int,
    val price: Double,
    val priceBeforeDiscount: Double,
    val title: String,
    val users: Int
)