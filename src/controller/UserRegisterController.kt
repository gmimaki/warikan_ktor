package com.example.controller

import com.example.msg.CreateUserReq
import com.example.msg.CreateUserResMsg
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

fun Route.userRegisterController() {
    val userService = UserService()

    route("/user/register") {
        post {
            val param = call.receive<CreateUserReq>()
            val user = userService.createUser(param.name, param.email, param.password)
            val token = async {
                UserAuthService().createToken(user)
            }.await()
            // TODO なぜか以下の方式じゃないと返らない Accept: application/jsonをつけると何も返らない
            /*
                curl -X POST -H "Content-Type:application/json" http://localhost:8080/user/register -d '{"name":"","email":"", "password": ""}'
             */
            call.respond(
                HttpStatusCode.OK,
                CreateUserResMsg(token)
            )
        }
    }
}