package com.example.mymqttclient.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TopicItem(val topic:String)
{
    @PrimaryKey(autoGenerate = false)
    var id:Long = 0
}