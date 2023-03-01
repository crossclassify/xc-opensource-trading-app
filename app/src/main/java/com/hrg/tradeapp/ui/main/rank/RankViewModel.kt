package com.hrg.tradeapp.ui.main.rank

import androidx.lifecycle.LiveData
import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor() : BaseViewModel() {

    val ranks: LiveData<List<User>>
        get() = socketConnectionTools.ranks

    fun getData() {
        val packet = socketPacketCreator.getRanks()
        socketConnectionTools.sendData(packet)
    }
}