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

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name

                val notes = getNotesForUser(email)
                call.respond(HttpStatusCode.OK, notes)
            }
        }
    }
    route("/addOwnerToNote") {
        authenticate {
            post {
                val request = try {
                    call.receive<OwnerRequest>()
                } catch(e: io.ktor.features.ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if(!checkIfUserExists(request.owner)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(false, "No user with this E-Mail exists")
                    )
                    return@post
                }
                if(isOwnerOfNote(request.noteId, request.owner)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(false, "This user is already an owner of this note")
                    )
                    return@post
                }
                if(addOwnerToNote(request.noteId, request.owner)) {
                    call.respond(
                        HttpStatusCode.OK,
                        SimpleResponse(true, "${request.owner} can now see this note")
                    )
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }
    route("/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNote>()
                } catch(e: io.ktor.features.ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if(deleteNoteForUser(email, request.id)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }
    route("/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Notes>()
                } catch (e: io.ktor.features.ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if(saveNote(note)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Conflict)
                }
            }
        }
    }
}
