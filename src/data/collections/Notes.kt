package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Notes(
    val title:String,
    val content:String,
    val data:Long,
    val owners:List<String>,
    val color:String,
    @BsonId //this tell the mogo that this is the id
    val id:String = ObjectId().toString()
)