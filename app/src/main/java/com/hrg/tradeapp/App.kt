package com.hrg.tradeapp

import android.app.Application
import com.crossclassify.trackersdk.utils.base.TrackerSdkApplication
import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.util.socket.SocketConnectionTools
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : TrackerSdkApplication() {
    @Inject
    lateinit var socketConnectionTools: SocketConnectionTools

    companion object {
        var userId: String = ""
        var user: User? = null
    }

    override fun onCreate() {
        createDefaultConfig( 299, "Wz5C96h5dg37j4tlmVt3b6UD4O1GDLv34fHmfp6l")
        super.onCreate()
        socketConnectionTools.initConnection()
    }
}