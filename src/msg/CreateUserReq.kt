package com.example.msg

data class CreateUserReq(
    val name: String,
    val email: String,
    val password: String
)