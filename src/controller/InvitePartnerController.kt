package com.example.controller

import com.example.MyAttributeKey
import com.example.msg.InvitePartnerRes
import com.example.service.InvitePartnerService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.invitePartnerController() {
    val invitePartnerService = InvitePartnerService()
    route("/invite_partner") {
        post {
            val now = System.currentTimeMillis()
            val userId = call.attributes[MyAttributeKey]
            val inviteToken = invitePartnerService.getAvailableInviteToken(userId, now)

            call.respond(
                HttpStatusCode.OK,
                InvitePartnerRes(inviteToken.token, inviteToken.password, invitePartnerService.getExpireHours())
            )
        }
    }

    // TODO inviteTokenからユーザー情報取得
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