package com.example.firebasecrud.model

//data class User(
//
//    val id: String? = null,
//    val name: String = "",  // Initialize with an empty string
//    val email: String
//    )

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null
) {
    // No-argument constructor
    constructor() : this("", "", "")
}


