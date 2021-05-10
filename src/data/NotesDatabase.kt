package com.example.data

import com.example.data.collections.Notes
import com.example.data.collections.User
import com.example.security.checkHashForPassword
import io.ktor.auth.*
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.not
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine //this make kmongo to use coroutines in all case
private val database = client.getDatabase("NotesDb")
private val users = database.getCollection<User>()
private val notes = database.getCollection<Notes>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean {
    return users.findOne(User::email eq email) != null
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return checkHashForPassword(passwordToCheck,actualPassword)
}

suspend fun getNotesForUser(email: String): List<Notes> {
    return notes.find(Notes::owners contains email).toList()
}

suspend fun saveNote(note: Notes): Boolean {
    val noteExists = notes.findOneById(note.id) != null
    return if(noteExists) {
        notes.updateOneById(note.id, note).wasAcknowledged()
    } else {
        notes.insertOne(note).wasAcknowledged()
    }
}

suspend fun isOwnerOfNote(noteID: String, owner: String): Boolean {
    val note = notes.findOneById(noteID) ?: return false
    return owner in note.owners
}

suspend fun addOwnerToNote(noteID: String, owner: String): Boolean {
    val owners = notes.findOneById(noteID)?.owners ?: return false
    return notes.updateOneById(noteID, setValue(Notes::owners, owners + owner)).wasAcknowledged()
}

suspend fun deleteNoteForUser(email: String, noteID: String): Boolean {
    val note = notes.findOne(Notes::id eq noteID, Notes::owners contains email)
    note?.let { note ->
        if(note.owners.size > 1) {
            // the note has multiple owners, so we just delete the email from the owners list
            val newOwners = note.owners - email
            val updateResult = notes.updateOne(Notes::id eq note.id, setValue(Notes::owners, newOwners))
            return updateResult.wasAcknowledged()
        }
        return notes.deleteOneById(note.id).wasAcknowledged()
    } ?: return false
}
