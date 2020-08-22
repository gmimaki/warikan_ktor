package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.example.controller.coupleController
import com.example.controller.loginController
import com.example.controller.userController
import com.example.controller.userRegisterController
import com.example.dao.Couples
import com.example.dao.Users
import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.origin
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDB() {
    val config = HikariConfig("/hikari.properties")
    //config.schema = <dbSchema>
    val ds = HikariDataSource(config)
    Database.connect(ds)
}

private fun Application.getEnv(name: String): String {
    return environment.config.property(name).getString()
}

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}

fun Application.module() {
    initDB()
    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            // JSONを返せるように
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
            }
        }

        transaction {
            SchemaUtils.create(Couples)
            SchemaUtils.create(Users)
        }
        routing {
            userRegisterController()
            loginController()

            // ログインが必要
            route("/general") {
                intercept(ApplicationCallPipeline.Call) {
                    val ha = call.request.header("Authorization")
                    if (!ha.isNullOrBlank()) {
                        val splited = ha.split(" ")
                        val token = splited[1]
                        authenticateToken(token)
                    }
                }

                coupleController()
                userController()
            }
        }
    }
    server.start()
}

// https://qiita.com/syumiwohossu/items/afae28300f3b70577e1b
val privateKey = "IHAVETOHIDETHIS"
fun authenticateToken(token: String) {
    try {
        val algorithm: com.auth0.jwt.algorithms.Algorithm = Algorithm.HMAC256(privateKey)
        val verifier: JWTVerifier = JWT.require(algorithm).withIssuer("warikan_ktor").build()
        verifier.verify(token)
    } catch (e: JWTCreationException) {
        println("Invalid token")
    }
}

fun getUserNameFromToken(token: String): String {
    val decodedToken = JWT.decode(token)
    return decodedToken.getClaim("name").asString()
}