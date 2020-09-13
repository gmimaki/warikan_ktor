package com.example.controller

import com.example.msg.GetInviterReq
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.acceptPartnerController() {
    //val PartnerService = InvitePartnerService()
    route("/inviter") {
        // TODO inviteTokenからユーザー情報取得
        post {
            val now = System.currentTimeMillis()
            val param = call.receive<GetInviterReq>()
            //val userId = call.attributes[MyAttributeKey]
            //val inviteToken = invitePartnerService.getAvailableInviteToken(userId, now)

            /*
            call.respond(
                HttpStatusCode.OK,
                InvitePartnerRes(inviteToken.token, inviteToken.password, invitePartnerService.getExpireHours())
            )
             */
        }
    }
}