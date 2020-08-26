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
}
