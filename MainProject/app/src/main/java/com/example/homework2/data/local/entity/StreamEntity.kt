package com.example.homework2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.homework2.dataclasses.streamsandtopics.Stream

@Entity(tableName = "streams")
data class StreamEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "stream_name")
    val streamName: String,

    @ColumnInfo(name = "subscribedOrAll")
    val subscribedOrAll: String = ALL

) {

    fun toStream(): Stream = Stream(
        stream_id = id,
        name = streamName,
        topicList = mutableListOf()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StreamEntity

        if (id != other.id) return false
        if (streamName != other.streamName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + streamName.hashCode()
        return result
    }

    companion object {
        const val SUBSCRIBED = "subscribed"
        const val ALL = "all"

        fun toEntity(stream: Stream, type: String): StreamEntity = StreamEntity(
            id = stream.stream_id,
            streamName = stream.name,
            subscribedOrAll = type
        )
    }
}
