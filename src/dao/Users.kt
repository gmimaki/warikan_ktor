package com.example.dao

import org.jetbrains.exposed.dao.IntIdTable

object Users : IntIdTable() {
    val name = varchar("name", 20)
    val email = varchar("email", 50)
    val password = varchar("password", 1024)
}