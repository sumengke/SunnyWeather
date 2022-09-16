package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*


class PlaceFragment : Fragment() {
    //使用lazy函数这种懒加载技术来获取PlaceViewModel的实例
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter
    //在onCreateView()方法中加载了fragment_place布局
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }
    //在onActivityCreated()方法中,给RecyclerView设置LayoutManager和适配器,使用PlaceViewModel中的placeList集合作为数据源
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //对存储的状态进行判断和读取:如果以存在存储的数据,就获取已存储的数据并解析为Place对象
        //只有当PlaceFragment被嵌入MainActivity中,且之前已经存在选中的城市时,才会跳转到WeatherActivity,解决了无限循环跳转的问题
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply{
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }//使用Place对象的经纬度坐标和城市名直接跳转并传递给WeatherActivity,这样用户就不需要每次重新搜索城市了
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        //调用EditText的addTextChangedListener()方法,监听搜索框内容变化的情况,每当搜索框发生新变化时就获取新内容,传递给PlaceViewModel的searchPlaces()方法,发起搜索城市数据的网络请求
        searchPlaceEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val content = editable.toString()
                if (content.isNotEmpty()) {
                    viewModel.searchPlaces(content)
                } else {
                    recyclerView.visibility = View.GONE
                    bgImageView.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    adapter.notifyDataSetChanged()
                }//当搜索框输入内容为空时,将RecyclerView隐藏起来,同时将之发挥美观作用的ImageView显示出来
            }
        })
        //要能够获取服务器响应的数据,借助LiveData来完成.
        //观察PlaceViewModel中的placeLiveData对象,当发生数据变化时,回调到Observe()接口中进行实现
        viewModel.placeLiveData.observe(this, Observer{ result ->
            //判断回调数据,不为空就将数据添加到PlaceViewModel中的placeList集合中,并通知PlaceAdapter进行刷新界面.
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}