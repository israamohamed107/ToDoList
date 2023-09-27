package com.example.todolist.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.todolist.data.MyDao
import com.example.todolist.data.MyDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ToDoModule {

    @Provides
    fun provideDao(db: MyDataBase): MyDao = db.getDao()

    @Provides
    @Singleton
    fun getDataBase(application: Application): MyDataBase =
        Room.databaseBuilder(application, MyDataBase::class.java, "tasks_database")
            .fallbackToDestructiveMigration()
            .build()


}