package com.hrg.tradeapp.ui.authorize.signup

import com.hrg.tradeapp.util.AuthorizePageType
import com.hrg.tradeapp.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupViewModel@Inject constructor() : BaseViewModel() {
    fun signup(email: String, name: String, password: String) {
        val payload = socketPacketCreator.createSignupPacket(email, name, password)
        socketConnectionTools.sendData(payload, AuthorizePageType.SIGNUP)
    }

    fun checkExist(email: String) {
        val checkExistPayload = socketPacketCreator.checkExistEmail(email)
        socketConnectionTools.sendData(checkExistPayload, AuthorizePageType.SIGNUP)
    }

    fun getUser(userId: String) {
        val getUserPacket = socketPacketCreator.getUser(userId)
        socketConnectionTools.sendData(getUserPacket, AuthorizePageType.LOGIN)
    }
}