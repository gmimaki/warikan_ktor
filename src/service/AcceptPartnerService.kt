package com.example.service

import com.example.dao.Invite_tokens
import com.example.dao.Users
import com.example.entity.User
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AcceptPartnerService {
    fun getExpireHours(): Int {
        return 24
    }

    fun getInviterByInviteToken(token: String, password: String, checkedAt: Long): User? {
        // expireしてないか?
        // passwordはcorrectか?
        val tokens = transaction {
            (Invite_tokens innerJoin Users).slice(Users.name).
                    select { (Invite_tokens.token.eq(token)) and Invite_tokens.userId.eq(Users.id) }.
                    orderBy(Invite_tokens.expiredAt, SortOrder.DESC)
        }
        println("THIS IS InviteTokens")
        println(tokens)

        return null
    }

    /*
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
     */
}