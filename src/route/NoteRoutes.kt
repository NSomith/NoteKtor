package com.example.route

import com.example.data.*
import com.example.data.collections.Notes
import com.example.data.request.DeleteNote
import com.example.data.request.OwnerRequest
import com.example.data.response.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.request.ContentTransformationException
import io.ktor.response.*
import io.ktor.routing.*

fun Route.noteRoutes(){
    route("/getnotes"){
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name //not null
                val notes = getNotesForUser(email)
                call.respond(HttpStatusCode.OK,notes)
            }
        }
    }

    route("/addnote"){
        authenticate {
            post {
                val note = try {
                    call.receive<Notes>()

                }catch (e:ContentTransformationException){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if(saveNote(note)){
                    call.respond(HttpStatusCode.OK)
                }else{
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }
    route("/deletenote"){
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNote>()
                }catch (e:ContentTransformationException){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if(deleteNoteForUser(email,request.id)){
                    call.respond(HttpStatusCode.OK)
                }else{
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }

    route("/addowner"){
        authenticate {
            post {
                val request = try{
                    call.receive<OwnerRequest>()
                }catch (e:ContentTransformationException){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if(!checkIfUserExist(request.owner)){
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(false,"No email exist")
                    )
                    return@post
                }
                if(isOwnerNote(request.noteId,request.owner)){
                    call.respond(HttpStatusCode.OK,SimpleResponse(false,"user already exist"))
                    return@post
                }
                if(addOwneresToNote(request.noteId,request.owner)){
                    call.respond(HttpStatusCode.OK,SimpleResponse(true,"${request.owner} owner created"))
                }else{
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }
}