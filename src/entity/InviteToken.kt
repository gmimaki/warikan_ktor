package com.example.entity

import com.example.dao.InviteTokens
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class InviteToken(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<InviteToken>(InviteTokens)

    var userId by InviteTokens.userId
    var token by InviteTokens.token
    var password by InviteTokens.password

    var createdAt by InviteTokens.createdAt
    var expiredAt by InviteTokens.expiredAt

    var approvedAt by InviteTokens.approvedAt
    var approvedUserId by InviteTokens.approvedUserId
}
