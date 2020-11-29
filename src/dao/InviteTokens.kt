package com.example.dao

import org.jetbrains.exposed.dao.IntIdTable

object Invite_tokens : IntIdTable() {
    val userId = integer("userId").references(Users.id)
    val token = varchar("token", 50)
    val password = varchar("password", 12)
    val createdAt = long("created_at")
    val expiredAt = long("expired_at")
    var approvedAt = long("approved_at").nullable()
    val approvedByInviterAt = long("approved_by_inviter_at").nullable()
    val approvedUserId = integer("approved_user_id").nullable()
}
// TODO ほんとは変遷を別テーブルで記録したほうがいい