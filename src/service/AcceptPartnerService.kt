package com.example.service

import com.example.dao.Invite_tokens
import com.example.entity.InviteToken
import com.example.entity.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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
            User.findById(token.userId)
        }
    }

    fun acceptByInvitee(inviterId: Int, token: String, acceptedAt: Long): Error? {

        transaction {
            // User作成
            val userService = UserService()
            val user = userService.createUser("", "", "")

            // Invite_tokensのapprovedAtとapprovedUserIdを更新
            Invite_tokens.update({ (Invite_tokens.userId eq inviterId) and (Invite_tokens.token eq token)}) {
                it[approvedUserId] = Integer.parseInt(user.id.toString())
                it[approvedAt] = acceptedAt
            }
        }

        // Inviterに通知 SQS使う？
        return null
    }
}