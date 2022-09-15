package com.sunnyweather.android.logic.model


import com.google.gson.annotations.SerializedName

//在PlaceResponse.kt文件中定义类与属性字段与在彩云天气app中通过使用API检索到的城市数据一致.返回的JSON文件中涉及status,place.其中place是一个数组,包括name,location,address.location中包括经纬度
data class PlaceResponse(val status:String, val places: List<Place>)
//使用@SerializedName注解,让JSON字段和Kotlin字段之间建立映射关系.
data class Place(val name:String, val location: Location, @SerializedName("formatted_address") val address:String)

data class Location(val lng: String, val lat: String)