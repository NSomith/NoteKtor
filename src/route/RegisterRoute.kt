package com.example.route

import com.example.data.checkIfUserExists
import com.example.data.collections.User
import com.example.data.registerUser
import com.example.data.request.AccountRequest
import com.example.data.response.SimpleResponse
import com.example.security.getHashWithSalt
import io.ktor.application.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute() {
    route("/register") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch(e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExists = checkIfUserExists(request.email)
            if(!userExists) {
                if(registerUser(User(request.email, getHashWithSalt(request.password)))) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Successfully created account!"))
                } else {
                    call.respond(HttpStatusCode.OK, SimpleResponse(false, "An unknown error occured"))
                }
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "A user with that E-Mail already exists"))
            }
        }
    }
}