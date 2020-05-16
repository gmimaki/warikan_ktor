package com.example.dao

import org.jetbrains.exposed.dao.IntIdTable

object Couples : IntIdTable() {
    val name1 = varchar("name1", 20)
    val name2 = varchar("name2", 20)
}