package com.hrg.tradeapp.ui.main.search

import androidx.lifecycle.LiveData
import com.hrg.tradeapp.App
import com.hrg.tradeapp.domain.models.Basket
import com.hrg.tradeapp.domain.models.Order
import com.hrg.tradeapp.domain.models.Price
import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.util.OrderType
import com.hrg.tradeapp.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : BaseViewModel() {
    val basket: LiveData<List<Basket>>
        get() = socketConnectionTools.basket

    val price: LiveData<List<Price>>
        get() = socketConnectionTools.price

    val user: LiveData<User?>
        get() = socketConnectionTools.userResponse

    val orders: LiveData<List<Order>>
        get() = socketConnectionTools.order

    val chart: LiveData<HashMap<Long, Double>>
        get() = socketConnectionTools.chart

    val prices = ArrayList<Price>()

    fun getData() {
        val packet = socketPacketCreator.createGetBasket(App.userId)
        socketConnectionTools.sendData(packet)
//        getChartData()
    }

    fun getChartData(stockName: String) {
        val startDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, -7)
        }.time
        val endDate = Calendar.getInstance().time
        val startOutputDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startDate)
        val endOutputDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDate)
        println("$startOutputDate $endOutputDate")

        val packet2 = socketPacketCreator.getChart(stockName, startOutputDate, endOutputDate)
        socketConnectionTools.sendData(packet2)
    }

    fun modifyMyShare(orderType: OrderType, price: Price, amount: Int) {
        val packet = socketPacketCreator.createOrder(
            orderType.name.lowercase(),
            price.sym,
            App.userId,
            amount,
            price.price
        )
        socketConnectionTools.sendData(packet)
    }
}