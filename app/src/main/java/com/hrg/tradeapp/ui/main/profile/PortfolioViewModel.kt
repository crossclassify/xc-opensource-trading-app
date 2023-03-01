package com.hrg.tradeapp.ui.main.profile

import com.hrg.tradeapp.App
import com.hrg.tradeapp.util.AuthorizePageType
import com.hrg.tradeapp.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor() : BaseViewModel() {
    fun getUser() {
        val getUserPacket = socketPacketCreator.getUser(App.userId)
        socketConnectionTools.sendData(getUserPacket, AuthorizePageType.LOGIN)
    }

    fun getBasket() {
        val getBasketPacket = socketPacketCreator.createGetBasket(App.userId)
        socketConnectionTools.sendData(getBasketPacket)
    }
}