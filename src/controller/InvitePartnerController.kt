package com.example.controller

import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.AmazonSQSException
import com.amazonaws.services.sqs.model.SendMessageRequest
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

            val sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).build()

            try {
                sqs.sendMessage(SendMessageRequest("SQS URL", "ABCDEFG")) // TODO envから取る
            } catch (e: AmazonSQSException) {
                if (e.errorCode != "QueueAlreadyExists") {
                    throw e;
                }
            }

            call.respond(
                HttpStatusCode.OK,
                InvitePartnerRes(inviteToken.token, inviteToken.password, invitePartnerService.getExpireHours())
            )
        }
    }
}

