package com.hrg.tradeapp.ui.main.transaction.addCardBottomSheet

import androidx.lifecycle.LiveData
import com.hrg.tradeapp.App
import com.hrg.tradeapp.domain.models.Card
import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.util.AuthorizePageType
import com.hrg.tradeapp.util.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor() : BaseViewModel() {
    val cards: LiveData<List<Card>>
        get() = socketConnectionTools.card

    val user: LiveData<User?>
        get() = socketConnectionTools.userResponse

    fun getCards() {
        val packet = socketPacketCreator.getAllCards()
        socketConnectionTools.sendData(packet)
    }

    fun addCardToUser(card: Card) {
        App.user?.let {
            val cards = List<Long>(it.cardsNumber.size + 1) { index ->
                if (index == it.cardsNumber.size)
                    card.cardNumber
                else
                    it.cardsNumber[index]
            }
            it.cardsNumber = cards
            val packet = socketPacketCreator.updateCards(it)
            socketConnectionTools.sendData(packet, AuthorizePageType.LOGIN)
        }
    }
}