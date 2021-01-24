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

    data class checkInviteTokenResult(val available: Boolean, val reason: String)

    // 招待を承諾する際、正しいリクエストであることを検証
    fun checkInviteToken(inviterUserId: Int, token: String, password: String, checkedAt: Long): checkInviteTokenResult {
        val target = transaction {
            // TODO パスワード暗号化
            InviteToken.find {
                Invite_tokens.userId.eq(inviterUserId) and Invite_tokens.token.eq(token) and Invite_tokens.password.eq(password)
            }.singleOrNull()
        } ?: return checkInviteTokenResult(false, "不正なリクエストです")

        if (target.expiredAt < checkedAt) {
            return checkInviteTokenResult(false, "招待後24時間以上経過しており、無効となっています。招待者に再度招待をお願いしてください。")
        }

        return checkInviteTokenResult(true, "")
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