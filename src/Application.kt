package com.example

import io.ktor.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

data class Couple(
    val id: Long
)

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respond(HttpStatusCode.OK, "Hello Ktor")
            }
            post("/couple") {
                println("couple create")
                call.respond(Couple(1))
            }
        }
    }
    server.start()
}
