package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//为了能够使用Retrofit接口,需要创建一个Retrofit构建器
object ServiceCreator {
    //定义一个BASE_URL常量,用于指定Retrofit的根路径
    private const val BASE_URL = "https://api.caiyunapp.com/"
    //在内部使用Retrofit.Builder()构建一个Retrofit对象,再调用其create()方法创建动态代理对象.
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    //提供一个外部可见的create()方法,并接收一个class类型的参数
    fun <T> create(serviceClass: Class<T>) : T = retrofit.create(serviceClass)
    //使用泛型实化功能简化
    inline fun <reified T> create() : T = create(T::class.java)
}