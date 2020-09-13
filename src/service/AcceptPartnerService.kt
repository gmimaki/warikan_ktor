package com.example.service

import com.example.entity.User

class AcceptPartnerService {
    fun getExpireHours(): Int {
        return 24
    }

    fun getInviterByInviteToken(token: String, password: String, checkedAt: Long): User? {
        // expireしてないか?
        // passwordはcorrectか?
        /*
        SELECT
            U.name
        FROM
            invite_tokens AS IT
        INNER JOIN users AS U ON IT.user_id = U.id
        WHERE IT.token = token
         */

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