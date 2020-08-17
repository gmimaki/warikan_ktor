package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.example.entity.User
import java.util.*

class UserAuthService {
    val privateKey = "IHAVETOHIDETHIS" // TODO 変えて隠す

    fun createToken(user: User): String {
        lateinit var token: String

        try {
            val algorithm: com.auth0.jwt.algorithms.Algorithm = Algorithm.HMAC256(privateKey)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.SECOND, 30)
            var expireTime = calendar.time
            token = JWT.create().withIssuer("warikan_ktor").withClaim("name", user.name).withExpiresAt(expireTime).sign(algorithm)
        } catch (e: JWTCreationException) {
            throw Error("Invalid signing")
        }

        return token
    }
}