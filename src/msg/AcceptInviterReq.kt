package com.example.msg

data class AcceptInviterReq(
    val inviterUserId: Int,
    val inviteToken: String,
    val invitePassword: String
)