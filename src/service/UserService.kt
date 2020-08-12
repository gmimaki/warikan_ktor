package com.example.service

import com.example.entity.User
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun getAllUsers(): List<User> {
        var result: List<User> = listOf()
        transaction {
            result = User.all().toList()
        }
        return result
    }

    fun createUser(name: String, email: String, password: String) {
        transaction {
            User.new {
                this.name = name
                this.email = email
                this.password = password
            }
        }
    }
}