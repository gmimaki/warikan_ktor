package com.example.dao

import org.jetbrains.exposed.dao.IntIdTable

object InviteTokens : IntIdTable() {
    val userId = integer("userId")// TODO 外部キー設定
    val token = varchar("token", 50)
    val password = varchar("password", 12)
    val createdAt = long("createdAt")
    val expiredAt = long("expiredAt")
    val approvedAt = long("approvedAt").nullable()
    val approvedUserId = integer("approvedUserId").nullable()
}