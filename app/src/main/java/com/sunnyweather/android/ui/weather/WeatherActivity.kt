package com.sunnyweather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView  //调用getWindow().getDecorView()方法获取到当前Activity的DecorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE  //再调用它的setSystemUiVisibility()方法来改变系统UI的显示.此处传入的两个值表示Activity的布局会显示到状态栏上面
        window.statusBarColor = Color.TRANSPARENT//调用setStatusBarColor()方法将状态栏设置成透明色
        setContentView(R.layout.activity_weather)
        //先从Intent中取出经纬度和地区名称,并赋值给WeatherViewModel的相应变量
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        //观察weatherLiveData对象,当获取到服务器返回的天气数据时,调用showWeatherInfo()方法进行解析与展示
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()  //result.getOrNull()返回一个可空类型,若失败则为空,否则为value
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        //调用WeatherViewModel的refreshWeather()方法执行以此刷新天气的请求
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }
    //  showWeatherInfo()方法解析数据并展示天气信息
    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据,将weather中的当前天气信息数据显示在now.xml布局中
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml布局中的数据,将weather中的未来几天的天气信息数据显示在forecast.xml布局中
        forecastLayout.removeAllViews()  //删除所有的视图
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            //动态加载forecast_item.xml布局,并设置相应的数据,添加到父布局中
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  //使用SimpleDateFormat格式化日期, Locale.getDefault()获取当前的语言环境，把返回值放进SimpleDateFormat的构造里，就能实现通用化
            dateInfo.text = simpleDateFormat.format(skycon.date) //format的用法是将当前时间格式转换为指定格式
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        //填充life_index.xml中的数据,让生活指数显示在life_index.xml布局中
        val lifeIndex = daily.lifeIndex
        //生活指数方面服务器会返回很多的数据,界面上只需要当天的数据即可,因此这对所有的生活指数取下标为0的那个元素的数据.
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE  //让ScrollView变成可见状态
    }
}
