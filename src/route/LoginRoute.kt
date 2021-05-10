package com.example.route

import com.example.data.checkPasswordForEmail
import com.example.data.request.AccountRequest
import com.example.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute() {
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch(e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
            if(isPasswordCorrect) {
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Your are now logged in!"))
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "The E-Mail or password is incorrect"))
            }
        }
    }
}
