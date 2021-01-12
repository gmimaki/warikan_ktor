package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.example.controller.*
import com.example.dao.Couples
import com.example.dao.Invite_tokens
import com.example.dao.Users
import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.origin
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URLDecoder

fun initDB() {
    val config = HikariConfig("/hikari.properties")
    val ds = HikariDataSource(config)
    Database.connect(ds)
}

private fun Application.getEnv(name: String): String {
    return System.getenv(name) ?: ""
}

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}

data class MySession(val token: String)

val MyAttributeKey = io.ktor.util.AttributeKey<Int>("MyAttributeKey")

fun Application.module() {
    initDB()
    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            // JSONを返せるように
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
            }
        }
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.AccessControlAllowOrigin)
            header(HttpHeaders.AccessControlAllowHeaders)
            anyHost()
            allowCredentials = true
        }
        install(Sessions) {
            cookie<MySession>("TOKEN")
        }

        transaction {
            SchemaUtils.create(Couples)
            SchemaUtils.create(Users)
            SchemaUtils.create(Invite_tokens)
        }
        routing {
            userRegisterController()
            loginController()
            acceptPartnerController()

            // ログインが必要
            route("/general") {
                var token = ""
                var userId = 0
                intercept(ApplicationCallPipeline.Call) {
                    val headerCookie = call.request.header("Cookie")
                    if (headerCookie.isNullOrBlank()) {
                        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                    } else {
                        val cookies = headerCookie.split("; ")
                        for (i in cookies) {
                            val keyAndVal = i.split("=")
                            if (keyAndVal[0] == "TOKEN") {
                                token = URLDecoder.decode(keyAndVal[1], "UTF-8")
                                token = token.replace("token=%23s", "") // なんか謎に入るんだよなー
                                val err = authenticateToken(token)
                                if (err != null) {
                                    call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                                    return@intercept
                                }
                            }
                        }
                    }

                    if (token?.length > 0) {
                        userId = getUserIdFromToken(token)
                        call.attributes.put(MyAttributeKey, userId)
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                        return@intercept
                    }
                }

                invitePartnerController()
                coupleController()
                userController()
            }
        }
    }
    server.start()
}

// https://qiita.com/syumiwohossu/items/afae28300f3b70577e1b
val privateKey = "IHAVETOHIDETHIS"
fun authenticateToken(token: String): Error? {
    try {
        val algorithm: com.auth0.jwt.algorithms.Algorithm = Algorithm.HMAC256(privateKey)
        val verifier: JWTVerifier = JWT.require(algorithm).withIssuer("warikan_ktor").build()
        verifier.verify(token)
        return null
    } catch (e: TokenExpiredException) {
        return Error(e)
    } catch (e: Exception) {
        print(e)
        return Error(e)
    }

    return null
}

fun getUserIdFromToken(token: String?): Int {
    val decodedToken = JWT.decode(token)
    val decoded = decodedToken.getClaim("userId").asString()
    return Integer.parseInt(decoded)
}