package com.example.entity

import com.example.dao.Invite_tokens
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class InviteToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<InviteToken>(Invite_tokens)

    var userId by Invite_tokens.userId
    var token by Invite_tokens.token
    var password by Invite_tokens.password

    var createdAt by Invite_tokens.createdAt
    var expiredAt by Invite_tokens.expiredAt

    var approvedAt by Invite_tokens.approvedAt
    var approvedUserId by Invite_tokens.approvedUserId
}
