package com.example.mymqttclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mymqttclient.HandlerHelper.HandlerHelper
import com.example.mymqttclient.MainActivity
import com.example.mymqttclient.dataClass.TopicItem
import com.example.mymqttclient.databinding.FragmentSubscribeBinding
import com.example.mymqttclient.mqttClientHelper


class SubscribeFragment : Fragment() {

    private lateinit var binding:FragmentSubscribeBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!::binding.isInitialized){
            binding = FragmentSubscribeBinding.inflate(inflater, container, false)
            HandlerHelper.registerSubscribeBinding(binding)
        }
        // 获取父类的context
        if (!::mainActivity.isInitialized){
            mainActivity = requireActivity() as MainActivity
        }

        // 订阅按钮
        binding.ButtonSubscribe.setOnClickListener{
            val topic = binding.EdittextSubscribeTopic.text.toString()

            // 查找列表中是否含有该数据
            val id = mainActivity.dataHosting.searchTopicID(topic).toInt()
            mainActivity.mqttClienthelper.subscribe(topic)
            if (id == -1){
                // 添加订阅到数据库中
                val topicItem = TopicItem(topic)
                mainActivity.dataHosting.addTopic(topicItem,mainActivity.topicDao)
            }


        }

        // 取消订阅按钮
        binding.ButtonUnsubscirbe.setOnClickListener{
            val topic = binding.EdittextSubscribeTopic.text.toString()
            val id = mainActivity.dataHosting.searchTopicID(topic).toInt()
            if (id != -1){
                mainActivity.mqttClienthelper.unsubscribe(topic)
                // 删除
                mainActivity.dataHosting.deleteTopic(topic,mainActivity.topicDao)
            }else{
                mqttClientHelper.snackBar("不存在该订阅!")
            }

        }


        // 发布按钮
        binding.ButtonPublish.setOnClickListener{
            val topic = binding.EdittextPublishTopic.text.toString()
            val message = binding.EdittextPublishMessage.text.toString()
            mainActivity.mqttClienthelper.publish(topic,message)
        }




        return binding.root
    }


}