package com.sunnyweather.android.logic


import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.RuntimeException


//创建的该单例类是作为仓库层的统一封装入口的
//一般在仓库层中定义的方法,为了能将异步获取的数据以响应编程的方式通知给上一层,通常会返回有一个LiveData对象.
object Repository {
    //liveData()函数具有一个强大的功能:可以自动构建并返回一个LiveData对象,在他的代码块中提供一个挂起函数的上下文,可以调用任意的挂起函数.
    //将liveData()函数的线程参数指定为Dispatchers.IO,可以让此处的代码块中的所有代码都运行在子线程中
    fun searchPlaces(query:String) = liveData(Dispatchers.IO) {
        val result = try {
            //在这里调用SunnyWeatherNetwork的searchPlaces()函数搜索城市数据
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            //如果服务器响应的状态是ok,就调用Result.success()方法包装获取的城市数据列表
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))  //否侧使用Result.failure()方法包装异常信息
            }
        } catch (e : Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)  //使用emit()方法将包装结果发射出去.此时的emit()方法类似于调用LiveData的setValue()方法来通知数据变化.
    }
}