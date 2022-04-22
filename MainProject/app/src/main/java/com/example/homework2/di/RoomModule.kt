package com.example.homework2.di

import android.content.Context
import androidx.room.Room
import com.example.homework2.Constance
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
            Constance.DBNAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}