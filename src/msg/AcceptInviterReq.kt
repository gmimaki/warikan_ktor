package com.example.msg

data class AcceptInviterReq(
    val inviterUserId: Int,
    val invitePartnerId: Int,
    val inviteToken: String,
    val invitePassword: String
)