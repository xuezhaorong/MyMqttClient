package com.example.mymqttclient.HandlerHelper

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.mymqttclient.MainActivity
import com.example.mymqttclient.databinding.ActivityMainBinding
import com.example.mymqttclient.databinding.FragmentSubscribeBinding
import com.google.android.material.snackbar.Snackbar

object HandlerHelper {

    var mainActivityBinding: ActivityMainBinding?= null
    var subscribeBinding:FragmentSubscribeBinding? = null

    fun registerMainActivityBinding(binding: ActivityMainBinding){
        mainActivityBinding = binding
    }

    fun registerSubscribeBinding(binding: FragmentSubscribeBinding){
        subscribeBinding = binding
    }


    val messageHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
//            when(msg.what){
//
//        }
    }

//    fun handlerSendMessage(Data:String){
//        thread {
//            val message = Message()
//            message.what = BluetoothHelper.TXFlag
//            message.data = Bundle().apply {
//                putString("data",Data)
//            }
//            messageHandler.sendMessage(message)
//        }
//
    }
}