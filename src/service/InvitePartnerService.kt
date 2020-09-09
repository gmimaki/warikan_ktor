package com.example.service

import com.example.dao.Invite_tokens
import com.example.entity.InviteToken
import com.example.util.StringUtil
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class InvitePartnerService {
    private val stringutil = StringUtil()

    fun getExpireHours(): Int {
        return 24
    }

    fun getAvailableInviteToken(userId: Int, createdAt: Long): InviteToken {
        val existsToken = findUnapproved(userId, createdAt)
        if (existsToken != null) {
            return existsToken
        }

        return createInviteToken(userId, createdAt)
    }

    private fun createInviteToken(userId: Int, createdAt: Long): InviteToken {
        val token = stringutil.createRandomString(30)
        val password = stringutil.createRandomString(12)
        val expiredAt = createdAt + (getExpireHours() * 60 * 60) // 24H

        return transaction {
            InviteToken.new {
                this.userId = userId
                this.token = token
                this.password = password
                this.createdAt = createdAt
                this.expiredAt = expiredAt
            }
        }
    }

    private fun findUnapproved(userId: Int, checkedAt: Long): InviteToken? {
        return  transaction {
            InviteToken.find {
                Invite_tokens.userId.eq(userId) and
                Invite_tokens.createdAt.lessEq(checkedAt) and
                Invite_tokens.createdAt.greaterEq(checkedAt - (getExpireHours() * 60 * 60)) and
                Invite_tokens.approvedUserId.isNull()
            }.singleOrNull()
        }
    }
}