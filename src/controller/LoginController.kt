package com.example.controller

import com.example.MySession
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
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun Route.loginController() {
    val userService = UserService()

    route("/user/login") {
        post {
            val param = call.receive<LoginReq>()
            val user = userService.findByEmailAndPassword(param.email, param.password) ?: throw Error("メールアドレスかパスワードが正しくありません") // 400か401にしたい
            val token = withContext(Dispatchers.Default) {
                UserAuthService().createToken(user)
            }

            runBlocking {
                call.sessions.set(MySession(token = token))
            }

            call.respond(
                HttpStatusCode.OK,
                LoginRes(user.name) // TODO userIdも
            )
        }
    }
}

