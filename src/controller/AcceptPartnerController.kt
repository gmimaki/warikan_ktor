package com.example.controller

import com.example.msg.GetInviterReq
import com.example.msg.GetInviterResMsg
import com.example.service.AcceptPartnerService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.acceptPartnerController() {
    val acceptPartnerService = AcceptPartnerService()
    route("/inviter") {
        // inviteTokenからユーザー情報取得
        post {
            val now = System.currentTimeMillis() / 1000
            val param = call.receive<GetInviterReq>()
            val inviter = acceptPartnerService.getInviterByInviteToken(param.token, param.password, now)
            if (inviter == null) {
                call.respond(
                    HttpStatusCode.BadRequest
                )
            }

            call.respond(
                HttpStatusCode.OK,
                GetInviterResMsg(inviter!!.name)
            )
        }
    }
    route("/accept") {
        post {

        }
    }
}