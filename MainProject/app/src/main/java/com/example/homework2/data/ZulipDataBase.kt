package com.example.homework2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.homework2.Constance
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

    companion object {

        @Volatile
        private var INSTANCE: ZulipDataBase? = null

        fun getInstance(context: Context): ZulipDataBase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ZulipDataBase::class.java,
                        Constance.DBNAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}