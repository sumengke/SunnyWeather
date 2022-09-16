package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    //定义两个方法:getRealtimeWeather()和getDailyWeather()方法.在每个方法上使用@GET注解声明要访问的API接口,使用@Path注解向请求接口中动态传入经纬度坐标
    //getRealtimeWeather()方法用于获取实时天气信息.
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String) : Call<RealtimeResponse>
    //getDailyWeather()方法用来获取未来天气信息
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng : String, @Path("lat") lat : String) : Call<DailyResponse>
}