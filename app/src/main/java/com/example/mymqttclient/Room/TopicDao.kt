package com.example.mymqttclient.Room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mymqttclient.dataClass.TopicItem

@Dao
interface TopicDao {

    // 插入
    @Insert
    fun insertTopic(topicItem: TopicItem):Long

//    @Update
//    fun updateUser(newplan:Plan)
//
    @Query("select * from `TopicItem`")
    fun loadAllTopics():List<TopicItem>

    // 删除
    @Query("delete from `TopicItem` where id = :id")
    fun deleteTopic(id: Long)

//    // 更新记录的 id 值，将满足条件的记录的 id 减 1
    @Query("UPDATE `TopicItem` SET id = id - 1 WHERE id > :threshold")
    fun indexAdjust(threshold: Long)

    // 清楚所有记录
//    @Query("delete from `TopicItem`")
//    fun clearAllTopics()
}