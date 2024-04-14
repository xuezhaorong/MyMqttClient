package com.example.mymqttclient.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mymqttclient.MainActivity
import com.example.mymqttclient.databinding.FragmentConnectBinding
import com.example.mymqttclient.mqttClientHelper


class ConnectFragment : Fragment() {
    private lateinit var binding:FragmentConnectBinding
    private lateinit var mainActivity:MainActivity



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!::binding.isInitialized){
            binding = FragmentConnectBinding.inflate(inflater, container, false)
        }
        // 获取父类的context
        if (!::mainActivity.isInitialized){
            mainActivity = requireActivity() as MainActivity
        }

        // 清除按钮
        binding.ButtonClean.setOnClickListener{
            binding.EditTextServerID.text.clear()
            binding.EditTextUsername.text.clear()
            binding.EditTextPassword.text.clear()
            binding.EditTextClientID.text.clear()
        }

        // 点击连接按钮
        binding.ButtonConnect.setOnClickListener{
            val serverURI = binding.EditTextServerID.text.toString()
            val clientID = binding.EditTextClientID.text.toString()
            val username = binding.EditTextUsername.text.toString()
            val password = binding.EditTextPassword.text.toString()
            // 设置参数
            mqttClientHelper.mqttSetElement(serverURI,clientID,username,password)
            // 连接
            mainActivity.mqttClienthelper.connect()
        }

        // 取消连接按钮
        binding.ButtonDisconnect.setOnClickListener{
            if (mqttClientHelper.connectFlag){
                mainActivity.mqttClienthelper.disconnect()

            }
        }


        // 预设值
        binding.ButtonPrefill.setOnClickListener{
            binding.EditTextServerID.setText("tcp://47.115.221.208:1883")
            binding.EditTextClientID.setText("android_mqttClient")
        }

        return binding.root
    }


}