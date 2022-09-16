package com.sunnyweather.android.logic


import android.content.Context
import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext


//创建的该单例类是作为仓库层的统一封装入口的
//一般在仓库层中定义的方法,为了能将异步获取的数据以响应编程的方式通知给上一层,通常会返回有一个LiveData对象.
object Repository {
    //liveData()函数具有一个强大的功能:可以自动构建并返回一个LiveData对象,在他的代码块中提供一个挂起函数的上下文,可以调用任意的挂起函数.
    //将liveData()函数的线程参数指定为Dispatchers.IO,可以让此处的代码块中的所有代码都运行在子线程中
    //在某一个统一的入口函数中进行封装,使得只需要进行一次try catch处理即可:
    fun searchPlaces(query:String) = fire(Dispatchers.IO) {
        //在这里调用SunnyWeatherNetwork的searchPlaces()函数搜索城市数据
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        //如果服务器响应的状态是ok,就调用Result.success()方法包装获取的城市数据列表
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))  //否侧使用Result.failure()方法包装异常信息
        }
    }
    //refreshWeather()方法用来刷新天气信息,在该方法中进行获取实时天气信息和未来天气信息
    fun refreshWeather(lng:String, lat:String) = fire(Dispatchers.IO) {
        //因为async函数只有在协程作用域中才能调用,因此使用coroutineScope函数创建一个协程作用域
        coroutineScope {
            //使用async函数,调用它的await()方法,可以在都获得到两种天气信息之后再进一步执行程序
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)  //将Realtime和Daily对象取出并封装到一个Weather对象中
                Result.success(weather)  //使用Result.success()方法包装这个Weather对象
            } else {
                Result.failure(RuntimeException("realtime response status is ${realtimeResponse.status}" + "daily response status is ${dailyResponse.status}"))
            }
        }
    }
    //新增fire()函数,这是一个按照liveData()函数的参数接受标准定义的一个高阶函数.在fire()函数内部先调用一下liveData()函数,然后咋liveData()函数的代码块中统一进行try catch处理
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) = liveData<Result<T>>(context) {
        val result = try {
            block()  //在try语句中调用传入的Lambda表达式中的代码
        } catch (e: Exception) {
            Result.failure<T>(e)
        }
        emit(result)// //使用emit()方法将包装结果发射出去.此时的emit()方法类似于调用LiveData的setValue()方法来通知数据变化.
    }
    //在仓库层中进行接口封装
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}