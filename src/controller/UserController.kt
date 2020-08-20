package com.example.controller

import com.example.msg.GetUserResMsg
import com.example.service.UserService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.userController() {
    val userService = UserService()

    route("/user") {
        get {
            call.respond(
                HttpStatusCode.OK,
                userService.getAllUsers().map { user -> GetUserResMsg(user.name, user.email, user.password) }
            )
        }
        post {
            print("POSTTTTTTTTTTTTTTTTTTTTTTTTTTTTT")
            /*
            val result = call.receive<CreateCoupleReq>()
            coupleService.createCouple(result.name1, result.name2, result.ratio1, result.ratio2)
            call.respond(
                HttpStatusCode.OK,
                coupleService.getAllCouples().map { couple -> GetCoupleResMsg(couple.name1, couple.name2, couple.ratio1, couple.ratio2) }
            )
             */
            call.respond(
                HttpStatusCode.OK,
                userService.getAllUsers().map { user -> GetUserResMsg(user.name, user.email, user.password) }
            )
        }
    }
}