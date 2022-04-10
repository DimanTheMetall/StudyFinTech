package com.example.homework2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.homework2.dataclasses.Topic

@Entity(
    tableName = "topics",
    primaryKeys = ["stream_id", "name"],
    foreignKeys = [ForeignKey(
        entity = StreamEntity::class,
        parentColumns = ["id"],
        childColumns = ["stream_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TopicEntity(

    @ColumnInfo(name = "stream_id")
    val streamId: Int,

    @ColumnInfo(name = "name")
    val name: String
) {
    fun toTopic(): Topic = Topic(
        name = name
    )

    companion object {
        fun toEntity(topic: Topic, streamId: Int): TopicEntity = TopicEntity(
            streamId = streamId,
            name = topic.name
        )
    }
}
