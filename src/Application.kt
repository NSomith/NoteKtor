package com.example

import com.example.data.checkPassword
import com.example.data.collections.User
import com.example.data.registerUser
import com.example.route.loginRoute
import com.example.route.noteRoutes
import com.example.route.registerRoute
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.Routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation){
        gson {
            setPrettyPrinting() //for getting the resposen in json
        }
    }
    install(Authentication){
        configuration()
    }
    install(Routing){
        registerRoute()
        loginRoute()
        noteRoutes()
    }
}

fun Authentication.Configuration.configuration(){
    basic {
        realm = "Note Server"
        validate {credentials->
            val email = credentials.name
            val password = credentials.password
            if(checkPassword(email,password)){
                UserIdPrincipal(email)
            }else null
        }
    }
}



