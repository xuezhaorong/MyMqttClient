package com.example.mymqttclient

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymqttclient.DataHosting.DataHosting
import com.example.mymqttclient.HandlerHelper.HandlerHelper
import com.example.mymqttclient.Room.TopicDao
import com.example.mymqttclient.Room.TopicDatabase
//import com.example.mymqttclient.Room.TopicDatabase
import com.example.mymqttclient.dataClass.TopicItem
import com.example.mymqttclient.databinding.ActivityMainBinding
import com.example.mymqttclient.databinding.TopicitemBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mqttClienthelper: mqttClientHelper
    private val topicList = ArrayList<TopicItem>()
    lateinit var dataHosting: DataHosting
    lateinit var topicDao: TopicDao
    val TAG = "AndroidMqttClient"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!::binding.isInitialized){
            binding = ActivityMainBinding.inflate(layoutInflater)
            HandlerHelper.registerMainActivityBinding(binding)

        }
        val displayMetrics = resources.displayMetrics
        val colorCount = 5
        val left = 0
        val top = 0
        val right = displayMetrics.widthPixels
        val bottom = getStatusBarHeight()

        // 获取背景颜色
        val typedValue = TypedValue()
        // 获取当前主题的 windowBackground 属性
        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)


        // 使用 TypedArray 获取 windowBackground 的颜色值
        val backgroundResourceId = typedValue.resourceId
        val typedArray = obtainStyledAttributes(backgroundResourceId, intArrayOf(android.R.attr.colorBackground))
        val backgroundColor = typedArray.getColor(0, 0)
        typedArray.recycle()

        // 如果颜色值为 0，则表示获取失败，可以使用默认颜色
        val finalBackgroundColor = if (backgroundColor != 0) backgroundColor else Color.WHITE

        // finalBackgroundColor 包含了当前主题的 windowBackground 颜色
        val bitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)

        // 使用 Canvas 将颜色值填充到 Bitmap 上
        val canvas = Canvas(bitmap)
        canvas.drawColor(finalBackgroundColor)

        if (finalBackgroundColor == Color.WHITE){
            setLightStatusBar()
        }else{
            // 解析状态栏上的颜色
            Palette
                .from(bitmap)
                .maximumColorCount(colorCount)
                .setRegion(left, top, right, bottom)
                .generate {
                    it?.let { palette ->
                        var mostPopularSwatch: Palette.Swatch? = null
                        for (swatch in palette.swatches) {
                            if (mostPopularSwatch == null
                                || swatch.population > mostPopularSwatch.population) {
                                mostPopularSwatch = swatch
                            }
                        }
                        mostPopularSwatch?.let { swatch ->
                            val luminance = ColorUtils.calculateLuminance(swatch.rgb)
                            if (luminance < 0.5) {
                                setDarkStatusBar()
                            } else {
                                setLightStatusBar()
                            }
                        }
                    }
                }
        }
        setContentView(binding.root)
        //设置状态栏
        setToolbar()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navigation.setupWithNavController(navController)


        topicDao = TopicDatabase.getDatabase(this).topicDao()

        dataHosting = ViewModelProvider(this).get(DataHosting::class.java)


        // 实例化mqttClientHelper
        mqttClienthelper = mqttClientHelper(this)


        // 加载所有topic
        if (dataHosting.getTopicSize() == 0){
            topicList.clear()
            dataHosting.loadAllTopics(topicDao)
        }

        // 设置recyclerview
        val madapter = TopicsAdapter(this,topicList)

        // 高级写法
        with(binding.recyclerView){
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = madapter
        }


        // 观察数据变化
        dataHosting.topicList.observe(this){ it ->
            topicList.clear()
            topicList.addAll(it)
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }


    }

    private fun setToolbar(){
        // 设置状态栏的导航按钮
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.title)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        return true
    }

    private fun setLightStatusBar() { // 状态栏亮色 图标为黑色
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightStatusBars = true
    }


    private fun setDarkStatusBar() { // 状态栏黑色 图标白色
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightStatusBars = false
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    // 适配器
    inner class TopicsAdapter(val context: Context, val topicsList:List<TopicItem>): RecyclerView.Adapter<TopicsAdapter.ViewHolder>() {
        inner class ViewHolder(val adapterBinding: TopicitemBinding) : RecyclerView.ViewHolder(adapterBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val adapterBinding: TopicitemBinding =
                TopicitemBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(adapterBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val topicitem = topicsList[position]
            holder.adapterBinding.topicItem.text = topicitem.topic
            holder.itemView.setOnClickListener{
                HandlerHelper.subscribeBinding?.let{
                    it.EdittextPublishTopic.setText(topicitem.topic)
                    it.EdittextSubscribeTopic.setText(topicitem.topic)
                }
            }
        }

        override fun getItemCount() = topicsList.size

    }






    override fun onDestroy() {
        super.onDestroy()
        mqttClienthelper.disconnect() // 取消连接
    }


}