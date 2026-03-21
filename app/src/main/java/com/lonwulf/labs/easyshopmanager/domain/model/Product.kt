package com.lonwulf.labs.easyshopmanager.domain.model

data class Product(
    private val id: String,
    private val name: String,
    private val description: String,
    private val price: Double,
    private val image: String,
    private val qty: String,
)
