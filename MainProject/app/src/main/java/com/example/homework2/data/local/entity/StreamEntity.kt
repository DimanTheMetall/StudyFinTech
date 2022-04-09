package com.example.homework2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.homework2.dataclasses.Stream

@Entity(tableName = "streams")
data class StreamEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String
) {
    fun toStream(): Stream = Stream(
        stream_id = id,
        name = name,
        topicList = mutableListOf()
    )

    companion object {
        fun toEntity(stream: Stream): StreamEntity = StreamEntity(
            id = stream.stream_id,
            name = stream.name
        )
    }
}
