package com.hrg.tradeapp.util.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hrg.tradeapp.util.SocketConnection
import com.hrg.tradeapp.util.socket.SocketConnectionTools
import com.hrg.tradeapp.util.socket.SocketPacketCreator
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
abstract class BaseViewModel : ViewModel() {
    @Inject
    lateinit var socketConnectionTools: SocketConnectionTools

    @Inject
    lateinit var socketPacketCreator: SocketPacketCreator

    val socketConnectionStatus: LiveData<SocketConnection>
        get() = socketConnectionTools.status

    val socketErrors: LiveData<String>
        get() = socketConnectionTools.errorMessage
}
