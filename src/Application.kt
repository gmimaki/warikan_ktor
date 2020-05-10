package com.example

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson

data class Couple(
    val id: Long,
    val name1: String,
    val name2: String
)

fun Application.module() {
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
