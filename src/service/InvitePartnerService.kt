package com.example.service

import com.example.dao.InviteTokens
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

        // TODO userIdが0になっちゃう jwtから取れてない
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
                InviteTokens.userId.eq(userId) and
                InviteTokens.createdAt.lessEq(checkedAt) and
                InviteTokens.createdAt.greaterEq(checkedAt - (getExpireHours() * 60 * 60)) and
                InviteTokens.approvedUserId.greater(0)
            }.singleOrNull()
        }
    }
}