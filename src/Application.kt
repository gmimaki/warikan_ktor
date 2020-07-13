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
import io.ktor.features.ContentNegotiation
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

