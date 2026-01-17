package com.example.chillbeads.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val id: String? = null,
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val description: String = "",
    val photoUrl: String? = null // menggunakan url photo karena google photo saya gabisa di akses
)
