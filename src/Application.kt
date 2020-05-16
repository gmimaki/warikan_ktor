package com.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database

data class Couple(
    val id: Long,
    val name1: String,
    val name2: String
)

fun Application.module() {
    Database.connect("jdbc:h2:mem:ktor_db;DB_CLOSE_DELAY=1", "org.h2.Driver")
    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            // JSONを返せるように
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
            }
        }
        routing {
            get("/") {
                call.respond(HttpStatusCode.OK, "Hello Ktor")
            }
            get("/couple") {
                println("couple create")
                call.respond(Couple(1, "名前1", "名前2"))
            }
            post("/couple") {

            }
        }
    }
    server.start()
}
