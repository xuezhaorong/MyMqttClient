package com.example.mymqttclient.DataHosting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymqttclient.Room.TopicDao
import com.example.mymqttclient.dataClass.TopicItem
import kotlin.concurrent.thread

class DataHosting:ViewModel() {
    companion object{

        private val _topicList = MutableLiveData<ArrayList<TopicItem>>()
    }

    val topicList: LiveData<ArrayList<TopicItem>>
        get() = _topicList


    // 新增一个topic
    public fun addTopic(topicItem: TopicItem,topicDao: TopicDao){
        thread {
            val currentList = _topicList.value ?: ArrayList()

            topicItem.id = currentList.size.toLong()
            currentList.add(topicItem)
            topicDao.insertTopic(topicItem)
            _topicList.postValue(currentList)
        }

    }

    // 加载所有的topic
    public fun loadAllTopics(topicDao: TopicDao){
        thread {
            val currentList = _topicList.value ?: ArrayList()
            currentList.clear()
            for(plan in topicDao.loadAllTopics()){
                currentList.add(plan)
            }
            _topicList.postValue(currentList)
        }
    }

    // 找到topic的id
    public fun searchTopicID(topic:String):Long{
        var id:Long = -1
        val currentList = _topicList.value ?: ArrayList()
        currentList.forEach{
            if (it.topic == topic){
                id = it.id
            }
        }
        return id
    }

    // 调整序号
    public fun indexAdjust(index: Long,topicDao: TopicDao){
        thread {
            val currentList = _topicList.value ?: ArrayList()
            for (id in (index+1 until getTopicSize())){
                currentList[id.toInt()].id -= 1
            }
            _topicList.postValue(currentList)

            // 调整数据库
            topicDao.indexAdjust(index)
        }
    }

//    public fun getPlan(index:Int):Plan{
//        val currentList = _planList.value ?: ArrayList()
//        return currentList[index]
//    }
//
//    public fun getPlanList():ArrayList<Plan> {
//        val currentList = _planList.value ?: ArrayList()
//        return currentList
//    }

    // 删除一个topic
    public fun deleteTopic(topic:String,topicDao: TopicDao){
        thread {
            val id = searchTopicID(topic).toInt()
            if (id != -1){
                topicDao.deleteTopic(id.toLong())
                val currentList = _topicList.value ?: ArrayList()
                currentList.remove(currentList[id])
                _topicList.postValue(currentList)
                // 进行序号调整
                indexAdjust(id.toLong(),topicDao)

            }

        }
    }

//    public fun setPlan(index:Int,plan:Plan) {
//        val currentList = _planList.value ?: ArrayList()
//        currentList[index] = plan
//        _planList.postValue(currentList)
//    }

//    public fun refreshPlan(){
//        val currentList = _planList.value ?: ArrayList()
//        _planList.postValue(currentList)
//    }

    // 获得topic数量
    public fun getTopicSize():Int{
        val currentList = _topicList.value ?: ArrayList()
        return currentList.size
    }

//    public fun clearAllTopic(topicDao: TopicDao){
//        val currentList = _topicList.value ?: ArrayList()
//        currentList.clear()
//        topicDao.clearAllTopics()
//        _topicList.postValue(currentList)
//
//    }

}