package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
//创建一个获取全局Context的类继承自Application.
class SunnyWeatherApplication : Application() {
    //在伴生对象companion object定义一个context变量
    companion object {
        const val TOKEN = "mIghyZhgX15LRPJ7"  //将在彩云天气App中获取到的可以用于连接API的令牌值设置其中
        //@SuppressLint注解标注忽略指定的警告
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    //重写父类的onCreate()方法,调用getApplicationContext()方法将返回值赋值给context变量,这样就可以一静态变量的形式获取Context对象了
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}