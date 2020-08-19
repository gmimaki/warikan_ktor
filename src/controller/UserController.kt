package com.example.controller

import com.example.msg.CreateUserReq
import com.example.msg.GetCoupleResMsg
import com.example.msg.GetUserResMsg
import com.example.service.CoupleService
import com.example.service.UserAuthService
import com.example.service.UserService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.async

fun Route.userController() {
    val userService = UserService()
    val coupleService = CoupleService()

    route("/user") {
        get {
            print("GETTTTTTTTTTTTTTTTTTTTTTT")
            call.respond(
                HttpStatusCode.OK,
                userService.getAllUsers().map { user -> GetUserResMsg(user.name, user.email, user.password) }
            )
        }
        post {
            print("POSTTTTTTTTTTTTTTTTTTTTTTTTTTTTT")
            /*
            val result = call.receive<CreateCoupleReq>()
            coupleService.createCouple(result.name1, result.name2, result.ratio1, result.ratio2)
            call.respond(
                HttpStatusCode.OK,
                coupleService.getAllCouples().map { couple -> GetCoupleResMsg(couple.name1, couple.name2, couple.ratio1, couple.ratio2) }
            )
             */
            call.respond(
                HttpStatusCode.OK,
                userService.getAllUsers().map { user -> GetUserResMsg(user.name, user.email, user.password) }
            )
        }
    }

    route("/user/register") {
        post {
            val param = call.receive<CreateUserReq>()
            val user = userService.createUser(param.name, param.email, param.password)
            print("THIS IS USER\n")
            print(user.name + "\n")
            print(user.email + "\n")
            print(user.password + "\n")
            val token = async {
                UserAuthService().createToken(user)
            }.await()
            print("TOKKKKKKKKKKKKKKKKKKKKKEEEEEEEEEENNNNNNNNNNNNNNNNN\n")
            print(token)
            // TODO ちゃんと返ってるか確認 jacksonが効いてない?
            call.respond(
                HttpStatusCode.OK,
                /*CreateUserResMsg(token)*/
 //               CreateUserResMsg("AOOOOOOOOOOOONNNNNNNNNNNNNn")
                        coupleService.getAllCouples().map { couple -> GetCoupleResMsg(couple.name1, couple.name2, couple.ratio1, couple.ratio2) }
            )
        }
    }

    route("/user/login") {
        post {

        }
    }
}