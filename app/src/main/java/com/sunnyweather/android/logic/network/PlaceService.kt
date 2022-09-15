package com.sunnyweather.android.logic.network


import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Query


interface PlaceService {
    //使用@GET注解,表示在searchPlaces()调用时,Retrofit会自动发起一条GET请求,去访问@GET注解中配置的地址
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    //搜索城市的API只有query参数是需要动态指定的,使用@Query注解的方式来实现,另外两个参数不变,因此卸载@GET注解中即可
    fun searchPlaces(@Query("query") query:String) : Call<PlaceResponse>
    //searchPlaces()方法的返回值声明为Call<PlaceResponse>,这样Retrofit就会将服务器返回的JSON数据自动解析成PlaceResponse对象.
}