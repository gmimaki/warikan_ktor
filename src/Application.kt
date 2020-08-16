package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.example.controller.coupleController
import com.example.dao.Couples
import com.example.dao.Users
import com.example.entity.Couple
import com.example.entity.User
import com.example.msg.CreateUserReq
import com.example.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.ContentNegotiation
import io.ktor.features.origin
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun initDB() {
    val config = HikariConfig("/hikari.properties")
    //config.schema = <dbSchema>
    val ds = HikariDataSource(config)
    Database.connect(ds)
}

private fun Application.getEnv(name: String): String {
    return environment.config.property(name).getString()
}

data class MySession(val userId: String = "AAAAA")

val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "google",
    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
    accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
    requestMethod = HttpMethod.Post,

    clientId = System.getenv("GOOGLE_CLIENT_ID"),
    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
    defaultScopes = listOf("profile")
)

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}

fun Application.module() {
    //Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "org.h2.Driver")
    initDB()
    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            // JSONを返せるように
            jackson {
                configure(SerializationFeature.INDENT_OUTPUT, true)
            }
        }

        install(Authentication) {
            /*
            oauth(COGNITO) {
                client = HttpClient()
                providerLookup = {
                    val domain = getEnv("COGNITO_DOMAIN") // TODO なんか違うかも指定方法
                    OAuthServerSettings.OAuth2ServerSettings(
                        name = "cognito",
                        authorizeUrl = "$domain/oauth2/authorize",
                        accessTokenUrl = "$domain/oauth2/token",
                        requestMethod = HttpMethod.Post,
                        clientId = getEnv("COGNITO_CLIENT_ID"),
                        clientSecret = getEnv("COGNITO_CLIENT_SECRET")
                    )
                }
                urlProvider = { "http://localhost:8080/login" }
            }
             */
            oauth("google-oauth") {
                client = HttpClient(Apache)
                providerLookup = { googleOauthProvider }
                urlProvider = { redirectUrl("/login") }
            }
        }

        transaction {
            SchemaUtils.create(Couples)
            SchemaUtils.create(Users)
        }
        routing {
            authenticate("google-oauth") {
                route("/login") {
                    handle {
                        val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                            ?: error("No principal")

                        val json = HttpClient(Apache).get<String>("https://www.googleapis.com/userinfo/v2/me") {
                            header("Authorization", "Bearer ${principal.accessToken}")
                        }

                        val data = ObjectMapper().readValue<Map<String, Any?>>(json)
                        val id = data["id"] as String?

                        if (id != null) {
                            call.sessions.set(MySession("id").userId, MySession("id"))
                        }
                        call.respondRedirect("/")
                    }
                }
            }
            coupleController()
            route("/user/register") {
                val userService = UserService()
                post {
                    print("ユーザー登録")
                    val param = call.receive<CreateUserReq>()
                    userService.createUser(param.name, param.email, param.password)
                    call.respond(
                        HttpStatusCode.OK
                    )
                    /*
                    val result = call.receive<CreateCoupleReq>()
                    coupl1eService.createCouple(result.name1, result.name2, result.ratio1, result.ratio2)
                    call.respond(
                        HttpStatusCode.OK,
                        coupleService.getAllCouples().map { couple -> GetCoupleResMsg(couple.name1, couple.name2, couple.ratio1, couple.ratio2) }
                    )
                     */
                }
            }
        }
    }
    server.start()
}

// https://qiita.com/syumiwohossu/items/afae28300f3b70577e1b
val privateKey = "IHAVETOHIDETHIS"
fun authenticateToken(token: String) {
    try {
        val algorithm: com.auth0.jwt.algorithms.Algorithm = Algorithm.HMAC256(privateKey)
        val verifier: JWTVerifier = JWT.require(algorithm).withIssuer("warikan_ktor").build()
        verifier.verify(token)
    } catch (e: JWTCreationException) {
        println("Invalid token")
    }
}
fun createToken(user: User): String {
    lateinit var token: String

    try {
        val algorithm: com.auth0.jwt.algorithms.Algorithm = Algorithm.HMAC256(privateKey)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 30)
        var expireTime = calendar.time
        token = JWT.create().withIssuer("warikan_ktor").withClaim("name", user.name).withExpiresAt(expireTime).sign(algorithm)
    } catch (e: JWTCreationException) {
        println("Invalid signing")
        // TODO 例外処理
    }

    return token
}

fun getUserNameFromToken(token: String): String {
    val decodedToken = JWT.decode(token)
    return decodedToken.getClaim("name").asString()
}