package com.example.dao

import org.jetbrains.exposed.dao.IntIdTable

object InviteTokens : IntIdTable() {
    val userId = integer("userId")
    val token = varchar("token", 50)
    val password = varchar("password", 8)
}