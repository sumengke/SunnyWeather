package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()
    //在PlaceViewModel中定义一个placeList集合,用于对界面上显示的城市数据进行缓存,在编写UI层时会用到该集合
    val placeList = ArrayList<Place>()
    //调用Transformations的switchMap()方法来观察这个对象,将searchLiveData转换为一个Activity可观察的placeLiveData对象.
    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query) //调用仓库层的searchPlaces()方法发起网络请求
    }

    //在PlaceViewModel中定义一个searchPlaces()方法,将传入的搜索参数赋值给一个searchLiveData对象
    fun searchPlaces(query:String) {
        searchLiveData.value = query
    }
    //对这些接口进行封装
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}
//原则上与界面相关的数据都应该放在ViewModel中,保证手机屏幕旋转时,数据不会丢失