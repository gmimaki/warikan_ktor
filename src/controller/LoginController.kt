package com.example.controller

import com.example.msg.LoginReq
import com.example.msg.LoginRes
import com.example.service.UserAuthService
import com.example.service.UserService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.async

fun Route.loginController() {
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
}