package com.example.service

import com.example.dao.Users
import com.example.entity.User
import org.jetbrains.exposed.sql.and
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

    fun createUser(name: String, email: String, password: String): User {
        if (existsByEmail(email)) {
            throw Error("Email exists");
        }
        return transaction {
            // TODO パスワード暗号化
            User.new {
                this.name = name
                this.email = email
                this.password = password
            }
        }
    }

    fun createUser(): User {
        return transaction {
            User.new {}
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

    fun findByEmailAndPassword(email: String, password: String): User? {
        return  transaction {
            User.find { Users.email.eq(email) and Users.password.eq(password)}.singleOrNull()
        }
    }
}