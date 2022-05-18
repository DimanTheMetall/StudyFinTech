package com.example.homework2.di.zulipcomponent

import android.content.Context
import androidx.room.Room
import com.example.homework2.Constants
import com.example.homework2.data.ZulipDataBase
import dagger.Module
import dagger.Provides

@Module
class RoomModule {

    @Provides
    fun provideZulipDataBase(context: Context): ZulipDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            ZulipDataBase::class.java,
            Constants.DBNAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
