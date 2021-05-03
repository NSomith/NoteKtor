package com.example.data

import com.example.data.collections.Notes
import com.example.data.collections.User
import io.ktor.auth.*
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.not
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine //this make kmongo to use coroutines in all case
private val database = client.getDatabase("NotesDb")
private val users  = database.getCollection<User>()
private val notes = database.getCollection<Notes>()

suspend fun registerUser(user: User):Boolean{
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExist(email:String):Boolean{
    return users.findOne(User::email eq email) !=null
}

suspend fun checkPassword(email:String,passwordToCheck:String):Boolean{
    val actualPassword = users.findOne(User::email eq email)?.password?:return false
    return actualPassword == passwordToCheck
}

suspend fun getNotesForUser(email:String):List<Notes>{
    return notes.find(Notes::owners contains email).toList()
}

suspend fun saveNote(note:Notes):Boolean{
    val noteExist = notes.findOneById(note.id) !=null
    if(noteExist){
        return notes.updateOneById(note.id, note).wasAcknowledged()
    }else{
        return notes.insertOne(note).wasAcknowledged()
    }
}

suspend fun deleteNoteForUser(email:String,noteId:String):Boolean{
    val note = notes.findOne(Notes::id eq noteId,Notes::owners contains email)
    note?.let {
        if(it.owners.size > 1){
            val newOwner = it.owners - email //remove that perticular user
            val updateOwner = notes.updateOne(Notes::id eq noteId, setValue(Notes::owners,newOwner))
            return updateOwner.wasAcknowledged()
        }
        return notes.deleteOneById(it.id).wasAcknowledged()
    } ?: return false
}

suspend fun addOwneresToNote(noteId: String,owner: String):Boolean{
    val owners = notes.findOneById(noteId)?.owners ?: return false
    return notes.updateOneById(noteId, setValue(Notes::owners,owners+owner)).wasAcknowledged()
}

suspend fun isOwnerNote(noteId: String,owner: String):Boolean{
    val note = notes.findOneById(noteId) ?:return false
    return owner in note.owners
}