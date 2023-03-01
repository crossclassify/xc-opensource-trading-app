package com.hrg.tradeapp.ui.main.transaction

import androidx.lifecycle.LiveData
import com.hrg.tradeapp.App
import com.hrg.tradeapp.domain.models.Card
import com.hrg.tradeapp.domain.models.Transaction
import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.util.AuthorizePageType
import com.hrg.tradeapp.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor() : BaseViewModel() {

    val cards: LiveData<List<Card>>
        get() = socketConnectionTools.card

    val transactions: LiveData<List<Transaction>>
        get() = socketConnectionTools.transaction

    val result: LiveData<Boolean>
        get() = socketConnectionTools.actionResult

    val user: LiveData<User?>
        get() = socketConnectionTools.userResponse

    var cardList: List<Card>? = null

    private fun getCards() {
        val packet = socketPacketCreator.getAllCards()
        socketConnectionTools.sendData(packet)
    }

    fun getData(userId: String = App.userId) {
        val packet = socketPacketCreator.createGetTransactions(userId)
        socketConnectionTools.sendData(packet)
        getCards()
    }

    fun getUser() {
        val getUserPacket = socketPacketCreator.getUser(App.userId)
        socketConnectionTools.sendData(getUserPacket, AuthorizePageType.LOGIN)
    }

    fun addTransaction(userId: String, amount: Int, cardNumber: Long, timestamp: String) {
        val packet = socketPacketCreator.createTransaction(
            userId,
            amount,
            cardNumber,
            timestamp
        )
        socketConnectionTools.sendData(packet)
    }
}