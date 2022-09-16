package com.sunnyweather.android.logic.network


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


//创建一个单例类用于指定一个统一的网络数据源访问入口,对所有的网络请求的API进行封装
object SunnyWeatherNetwork {
    //创建一个weatherService接口的动态代理对象
    private val weatherService = ServiceCreator.create(WeatherService::class.java)
    //定义一个挂起函数,调用weatherService接口中定义的getDailyWeather()方法和getRealtimeWeather()方法
    suspend fun getDailyWeather(lng:String, lat:String) = weatherService.getDailyWeather(lng, lat).await()
    suspend fun getRealtimeWeather(lng:String, lat:String) = weatherService.getRealtimeWeather(lng, lat).await()

    //首先按创建一个PlaceService接口的动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()
    //定义一个挂起函数:searchPlaces()函数,在这里调用在PlaceService接口中定义的searchPlaces()方法(用于获取城市信息的方法),以便发起搜索城市数据请求
    suspend fun searchPlaces(query:String) = placeService.searchPlaces(query).await()
    //简化Retrofit的回调写法:借助suspendCoroutine()等协程技术实现的,定义一个挂起函数await(),给他声明一个泛型T,并将其定义成Call<T>的扩展函数,这样所有返回值是Call类型的Retrofit网络请求接口都可以直接调用await()函数
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }//当外部调用SunnyWeatherNetwork的searchPlaces()函数时,Retrofit就会立即发起网络请求,同时当前协程也会被阻塞.直到服务器响应请求之后,
    // await()函数会将解析出来的数据模型对象取出并返回,同时恢复当前协程,searchPlaces()函数得到await()函数返回值后会将数据再返回到上一层
}