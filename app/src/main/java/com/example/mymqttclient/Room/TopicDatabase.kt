package com.example.mymqttclient.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mymqttclient.dataClass.TopicItem

@Database(version = 1, entities = [TopicItem::class])
abstract class TopicDatabase: RoomDatabase() {

    abstract fun topicDao():TopicDao

    companion object{
        private var instance:TopicDatabase ?= null

        @Synchronized
        fun getDatabase(context: Context):TopicDatabase{
            instance?.let {
                return it
            }
            return  Room.databaseBuilder(context.applicationContext,TopicDatabase::class.java,"plan_database").build().apply {
                instance = this
            }
        }
    }
}