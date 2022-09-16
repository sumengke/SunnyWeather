package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.place_item.view.*

//给RecyclerView创建一个适配器:PlaceAdapter,继承自RecyclerView.Adapter,泛型指定为PlaceAdapter.ViewHolder
//将Fragment修改为PlaceFragment对象,这样就可以调用PlaceFragment所对应的PlaceViewModel
class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    //定义一个内部类ViewHolder,继承自RecyclerView.ViewHolder.其主构造函数中传入一个view参数,这个参数通常是RecyclerView子项的最外层布局,然后通过findViewById()方法获取TextView的实例
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName : TextView = view.findViewById(R.id.placeName)
        val placeAddress : TextView = view.findViewById(R.id.placeAddress)
    }
    //由于其继承自RecyclerView.Adapter,需要重写onCreateViewHolder()方法,onBindViewHolder()方法和getItemCount()方法
    //在 onCreateViewHolder()方法中创建ViewHolder的实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        //修改代码,使得搜索城市界面和天气数据界面之间可以跳转
        val holder = ViewHolder(view)
        //给place_item.xml的最外层布局RecyclerView注册一个点击事件监听器
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }//在点击事件中,要获取点击项的经纬度和地区名称,并将它们传入到Intent中
            fragment.viewModel.savePlace(place)  //在点击任何子项布局时,在跳转到WeatherActivity之前,先调用PlaceViewModel的savePlace()方法来存储选中的城市
            fragment.startActivity(intent)  //调用Fragment的startActivity()方法启动WeatherActivity
            fragment.activity?.finish()
        }
        return holder
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