package com.example.route

import com.example.data.checkPassword
import com.example.data.request.AccountRequest
import com.example.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute(){
    route("/login"){
        post {
            val request = try {
                call.receive<AccountRequest>() //recive in the form of json
            }catch (e:ContentTransformationException){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val checkpassword = checkPassword(request.email,request.password)
            if(checkpassword){
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"Logged in"))
            }else{
                call.respond(HttpStatusCode.OK,SimpleResponse(false,"Email or password incorrect"))
            }
        }
    }
}