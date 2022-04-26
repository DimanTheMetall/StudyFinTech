package com.example.homework2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.homework2.data.local.entity.MessageEntity
import com.example.homework2.data.local.entity.ReactionEntity
import com.example.homework2.data.local.entity.StreamEntity
import com.example.homework2.data.local.entity.TopicEntity

@Database(
    version = 1,
    entities = [
        MessageEntity::class,
        ReactionEntity::class,
        StreamEntity::class,
        TopicEntity::class
    ]
)
abstract class ZulipDataBase : RoomDatabase() {

    abstract fun getStreamsAndTopicsDao(): StreamsAndTopicsDao

    abstract fun getMessagesAndReactionDao(): MessagesAndReactionDao

}
