package com.example.route

import com.example.data.checkIfUserExist
import com.example.data.collections.User
import com.example.data.registerUser
import com.example.data.request.AccountRequest
import com.example.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute(){
    route("/register"){
        post {
            val request = try {
                call.receive<AccountRequest>() //recive in the form of json
            }catch (e:ContentTransformationException){
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExist = checkIfUserExist(request.email)
            if(!userExist){
                if(registerUser(User(request.email,request.password))){
                    call.respond(HttpStatusCode.OK,SimpleResponse(true,"Registered succesful"))
                }else{
                    call.respond(HttpStatusCode.OK,SimpleResponse(false,"Some Connection problem in db"))
                }
            }else{
                call.respond(HttpStatusCode.OK,SimpleResponse(false,"Email exist"))
            }
        }

    }
}