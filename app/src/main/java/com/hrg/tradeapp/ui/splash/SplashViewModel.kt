package com.hrg.tradeapp.ui.splash

import androidx.lifecycle.LiveData
import com.hrg.tradeapp.util.base.BaseViewModel
import com.hrg.tradeapp.util.socket.SocketConnectionTools
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    fun initSocketConnection() {
//        socketConnectionTools.initConnection()
    }
}