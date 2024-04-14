package com.example.mymqttclient

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.mymqttclient.HandlerHelper.HandlerHelper
import com.google.android.material.snackbar.Snackbar
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import kotlin.concurrent.thread


class mqttClientHelper(private val context: Context) {
    private lateinit var mqttClient: MqttAndroidClient // mqttClient


    companion object{
        const val TAG = "AndroidMqttClient"

        lateinit var mqttServerURI: String
        lateinit var mqttClientID: String
        // 用户名和密码可空
        var mqttUsername: String = ""
        var mqttPassword:String = ""

        // 设置参数
        fun mqttSetElement(serverURI: String,clientID: String,username: String,password: String){
            mqttServerURI = serverURI
            mqttClientID = clientID
            mqttUsername = username
            mqttPassword = password

        }


        fun snackBar(content:String) =
            HandlerHelper.mainActivityBinding?.let { Snackbar.make(it.root, content, Snackbar.LENGTH_LONG).setAction("Action", null).show() }


        var connectFlag = false
    }



    public fun connect(){ // 连接


        // 实例化mqttClient
        mqttClient = MqttAndroidClient(context,mqttServerURI, mqttClientID)

        try {
            mqttClient.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String) {
                    if (reconnect) {
                        Log.d(TAG, "Reconnected: $serverURI.")

                    } else {
                        Log.d(TAG, "Connected: $serverURI.")
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(TAG, "The Connection was lost.")
                }

                override fun messageArrived(topic: String, message: MqttMessage) {
                    Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
                    appendMessage("Receive message: ${message.toString()} from  $topic")

                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {}
            })
        }catch (e:MqttException){
            e.printStackTrace()
        }

        val mqttConnectOptions  = MqttConnectOptions()
        // 是否选择匿名登录
        if (mqttUsername != "" && mqttPassword!= ""){
            mqttConnectOptions.userName = mqttUsername
            mqttConnectOptions.password = mqttPassword.toCharArray()
        }

        // 设置cleansession
//        mqttConnectOptions.isAutomaticReconnect = true
//        mqttConnectOptions.isCleanSession = false
        try {
            mqttClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                    snackBar("连接成功!")
                    HandlerHelper.mainActivityBinding?.let {
                        it.toolbar.title = "已连接 ${mqttServerURI}"
                    }
                    connectFlag = true

                    // 设置其他参数
//                    val disconnectedBufferOptions = DisconnectedBufferOptions()
//                    disconnectedBufferOptions.isBufferEnabled = true
//                    disconnectedBufferOptions.bufferSize = 100
//                    disconnectedBufferOptions.isPersistBuffer = false
//                    disconnectedBufferOptions.isDeleteOldestMessages = false
//                    mqttClient.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure")
                    snackBar("连接失败")
                    connectFlag = false
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }



    }

    public fun subscribe(topic: String,qos: Int = 1) { //  订阅
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d(TAG, "subscribed $topic")
                    snackBar("成功订阅 ${topic}")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe $topic")
                    snackBar("订阅失败")
                }
            })


        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    public fun unsubscribe(topic: String) { // 取消订阅
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                    snackBar("取消订阅成功 ${topic}")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                    snackBar("取消订阅成功失败")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    public fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) { // 发布消息
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg published to $topic")
                    appendMessage("Published: $msg to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                    appendMessage("Published failed")

                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    public fun disconnect() { // 断开连接
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                    snackBar("取消连接成功")
                    HandlerHelper.mainActivityBinding?.let {
                        it.toolbar.title = "未连接"
                    }
                    connectFlag = false
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to disconnect")
                    snackBar("取消连接失败")
                    connectFlag = true
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    private fun appendMessage(content: String){
        val textview = HandlerHelper.subscribeBinding?.TextViewMessage
        textview?.append("${content}\n")
        textview?.let {
            val offest = it.lineCount * it.lineHeight
            if ( offest > it.height){
                it.scrollTo(0,offest - it.height)
            }
        }
    }


}