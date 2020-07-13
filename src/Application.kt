package com.example

import com.example.controller.coupleController
import com.example.dao.Couples
import com.example.entity.Couple
import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.oauth
import io.ktor.client.HttpClient
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
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

const val COGNITO = "cognito"
private fun Application.getEnv(name: String): String {
    return environment.config.property(name).getString()
}

fun Application.module() {
    //Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "org.h2.Driver")
    initDB()
    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            // JSONを返せるように
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
            }
        }

        install(Authentication) {
            oauth(COGNITO) {
                client = HttpClient()
                providerLookup = {
                    val domain = getEnv("COGNITO_DOMAIN") // TODO なんか違うかも指定方法
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = "cognito",
                        authorizeUrl = "$domain/oauth2/authorize",
                        accessTokenUrl = "$domain/oauth2/token",
                        requestMethod = HttpMethod.Post,
                        clientId = getEnv("COGNITO_CLIENT_ID"),
                        clientSecret = getEnv("COGNITO_CLIENT_SECRET")
                    )
                }
                urlProvider = { "http://localhost:8080/login" }
            }
        }

        transaction {
            SchemaUtils.create(Couples)
            Couple.new {
                name1 = "name1"
                name2 = "name2"
                ratio1 = 50
                ratio2 = 50
            }
        }
        routing {
            coupleController()
        }
    }
    server.start()
}

