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
            val now = System.currentTimeMillis() / 1000
            val userId = call.attributes[MyAttributeKey]
            val inviteToken = invitePartnerService.getAvailableInviteToken(userId, now)

            call.respond(
                HttpStatusCode.OK,
                InvitePartnerRes(inviteToken.token, inviteToken.password, invitePartnerService.getExpireHours())
            )
        }
    }
}

