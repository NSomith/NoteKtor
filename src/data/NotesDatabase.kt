package com.example.data

import com.example.data.collections.User
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine //this make kmongo to use coroutines in all case
private val database = client.getDatabase("NotesDb")
private val users  = database.getCollection<User>()

suspend fun registerUser(user: User):Boolean{

    return users.insertOne(user).wasAcknowledged()
}

//suspend fun

