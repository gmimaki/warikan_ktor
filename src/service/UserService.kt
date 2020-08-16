package com.example.service

import com.example.dao.Users
import com.example.entity.User
import org.jetbrains.exposed.sql.select
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
        if (!existsByEmail(email)) {
            // TODO エラーハンドリング
        }
        transaction {
            // TODO パスワード暗号化
            User.new {
                this.name = name
                this.email = email
                this.password = password
            }
        }
    }

    fun existsByEmail(email: String): Boolean {
        var count = 0;
        transaction {
            val select = Users.select { Users.email eq email }
            count = select.count();
        }
        return count > 0
    }
}