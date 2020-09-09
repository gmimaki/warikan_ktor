package com.example.dao

import org.jetbrains.exposed.dao.IntIdTable

object Invite_tokens : IntIdTable() {
    val userId = integer("userId")// TODO 外部キー設定
    val token = varchar("token", 50)
    val password = varchar("password", 12)
    val createdAt = long("created_at")
    val expiredAt = long("expired_at")
    val approvedAt = long("approved_at").nullable()
    val approvedUserId = integer("approved_user_id").nullable()
}