package com.sunnyweather.android.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import kotlinx.android.synthetic.main.place_item.view.*

//给RecyclerView创建一个适配器:PlaceAdapter,继承自RecyclerView.Adapter,泛型指定为PlaceAdapter.ViewHolder
class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    //定义一个内部类ViewHolder,继承自RecyclerView.ViewHolder.其主构造函数中传入一个view参数,这个参数通常是RecyclerView子项的最外层布局,然后通过findViewById()方法获取TextView的实例
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName : TextView = view.findViewById(R.id.placeName)
        val placeAddress : TextView = view.findViewById(R.id.placeAddress)
    }
    //由于其继承自RecyclerView.Adapter,需要重写onCreateViewHolder()方法,onBindViewHolder()方法和getItemCount()方法
    //在 onCreateViewHolder()方法中创建ViewHolder的实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }
    //在onBindViewHolder()方法中对RecyclerView子项的数据进行赋值,会在每个子项被滚动到屏幕内时执行.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //通过position参数得到当前项的place实例,再将数据设置到ViewHolder的TextView中
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }
    //getItemCount()方法用于告诉RecyclerView一共有多少个子项,直接返回数据源的长度即可.
    override fun getItemCount() = placeList.size
}