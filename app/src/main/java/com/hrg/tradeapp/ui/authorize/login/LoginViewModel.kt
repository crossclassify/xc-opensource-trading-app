package com.hrg.tradeapp.ui.authorize.login

import com.hrg.tradeapp.util.AuthorizePageType
import com.hrg.tradeapp.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {
    fun login(email: String, password: String) {
        val payload = socketPacketCreator.createLoginPacket(email, password)
        socketConnectionTools.sendData(payload, AuthorizePageType.LOGIN)
    }
}