package com.sunnyweather.android.logic.model


import com.google.gson.annotations.SerializedName
import java.util.*
//获取未来几天天气信息接口的数据模型类.注意:此时返回的天气数据全部是数组形式的,数组中的每个元素都对应着一天的数据.在数据模型中,使用List集合对JSON中的数组元素进行映射
data class DailyResponse(val status:String, val result: Result) {
    data class Result(val daily: Daily)

    data class Daily(val temperature: List<Temperature>, val skycon : List<Skycon>, @SerializedName("life_index") val lifeIndex : LifeIndex)

    data class Temperature(val max: Float, val min : Float)

    data class Skycon(val value:String, val date: Date)

    data class LifeIndex(val coldRisk : List<LifeDescription>, val carWashing: List<LifeDescription>, val ultraviolet: List<LifeDescription>, val dressing: List<LifeDescription>)

    data class LifeDescription(val desc: String)
}