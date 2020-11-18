package com.example.msg

data class InvitePartnerRes(
    val token: String,
    val password: String,
    val expireHours: Int
)