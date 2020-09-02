package com.example.controller

import com.example.msg.InvitePartnerRes
import com.example.util.StringUtil
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.invitePartnerController() {
    val stringutil = StringUtil()
    route("/invite_partner") {
        post {
            val token = stringutil.createRandomString(30)
            val password = stringutil.createRandomString(12)
            val expireHours = 24
            val expiredAt = System.currentTimeMillis() + (expireHours * 60 * 60) // 24H
            // TODO DBチェック 発行されてればそれを使う なければ発行してDBに記録

            call.respond(
                HttpStatusCode.OK,
                InvitePartnerRes(token, password, expireHours)
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