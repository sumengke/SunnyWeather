package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

//在PlaceDao单例类中封装几个必要的存储和读取数据饿接口
object PlaceDao {
    //savePlace()方法用于将Place对象存储到SharedPreferences文件中
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))  //此处是通过GSON将Place对象转成一个JSON字符串,使用字符串的存储方式来保存数据
        }
    }
    //getSavedPlace()方法中,先将JSON字符串从SharedPreferences文件中读取出来,
    fun getSavedPlace() : Place {
        val placeJson = sharedPreferences().getString("place", "")
        //然后通过GSON将JSON字符串解析成Place对象并返回
        return Gson().fromJson(placeJson, Place::class.java)
    }
    //isPlaceSaved()方法用于判断是否已有数据已被存储
    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}