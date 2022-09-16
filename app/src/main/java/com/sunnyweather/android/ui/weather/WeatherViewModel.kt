package com.sunnyweather.android.ui.weather

import com.sunnyweather.android.logic.model.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository

class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()
    //定义3个与界面相关的数据,保障手机屏幕旋转时,数据不会丢失
    var locationLng = ""
    var locationLat = ""
    var placeName = ""
    //调用Transformations的switchMap()方法观察该对象
    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }
    //创建一个refreshWeather()方法:刷新天气信息,并将传入的经纬度参数封装成Location对象后赋值给locationLiveData对象
    fun refreshWeather(lng:String, lat:String) {
        locationLiveData.value = Location(lng, lat)
    }
}