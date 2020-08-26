package com.example.controller

import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.invitePartnerController() {
    // TODO inviteTokenとpasswordの発行
    route("/invite_partner") {
        post {

        }
    }
    /*
    val userService = UserService()

    route("/user/login") {
        post {
            val param = call.receive<LoginReq>()
            val user = userService.findByEmailAndPassword(param.email, param.password) ?: throw Error("メールアドレスかパスワードが正しくありません")
            val token = async {
                UserAuthService().createToken(user)
            }.await()

            call.respond(
                HttpStatusCode.OK,
                LoginRes(token)
            )
        }
    }
     */
}