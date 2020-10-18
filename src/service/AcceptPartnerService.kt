package com.example.service

import com.example.dao.Invite_tokens
import com.example.entity.InviteToken
import com.example.entity.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class AcceptPartnerService {
    fun getInviterByInviteToken(token: String, password: String, checkedAt: Long): User? {
        val token = transaction {
            InviteToken.find { Invite_tokens.token.eq(token) and Invite_tokens.password.eq(password) }.singleOrNull()
        } ?: return null

        // Expireしてないかチェック
        if (token.expiredAt < checkedAt) {
            return null
        }

        return transaction {
            User.findById(token.id)
        }
    }
}