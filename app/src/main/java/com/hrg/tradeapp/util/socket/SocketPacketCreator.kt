package com.hrg.tradeapp.util.socket

import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.util.SocketActions
import com.hrg.tradeapp.util.SocketResources
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketPacketCreator @Inject constructor() {
    fun createLoginPacket(
        email: String,
        password: String
    ): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.USERS)
        json.apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(EMAIL_FIELD_NAME, email)
                put(PASSWORD_FIELD_NAME, password)
            })
        }
        return json.toString()
    }

    fun createSignupPacket(
        email: String,
        name: String,
        password: String
    ): String {
        val json = createBasePacket(SocketActions.POST, SocketResources.USERS)
        json.apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(EMAIL_FIELD_NAME, email)
                put(NAME_FIELD_NAME, name)
                put(PASSWORD_FIELD_NAME, password)
                put(BALANCE_FIELD_NAME, 0)
                put(SHARE_VALUE_FIELD_NAME, 0)
                put(TOTAL_VALUE_FIELD_NAME, 0)
                put(CARDS_NUMBER_FIELD_NAME, JSONArray())
            })
        }
        return json.toString()
    }

    fun getUser(userId: String): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.USERS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(ID_FIELD_NAME, userId)
            })
        }
        return json.toString()
    }

    fun createSubscribePrices(): String {
        val json = createBasePacket(SocketActions.SUBSCRIBE, SocketResources.PRICES).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(ID_FIELD_NAME, ALL_VALUE)
            })
        }
        return json.toString()
    }

    fun createTransaction(
        userId: String,
        amount: Int,
        cardNumber: Long,
        timestamp: String
    ): String {
        val json = createBasePacket(SocketActions.POST, SocketResources.TRANSACTIONS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(USER_ID_FIELD_NAME, userId)
                put(AMOUNT_FIELD_NAME, amount)
                put(CARD_NUMBER_FIELD_NAME, cardNumber)
                put(TIMESTAMP_FIELD_NAME, timestamp)
            })
        }
        return json.toString()
    }

    fun createGetTransactions(userId: String): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.TRANSACTIONS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(USER_ID_FIELD_NAME, userId)
            })
        }
        return json.toString()
    }

    fun createOrder(
        type: String,
        stockName: String,
        userId: String,
        amount: Int,
        orderPrice: Float,
        status: String = "open"
    ): String {
        val json = createBasePacket(SocketActions.POST, SocketResources.ORDERS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(TYPE_FIELD_NAME, type)
                put(STOCK_NAME_FIELD_NAME, stockName)
                put(USER_ID_FIELD_NAME, userId)
                put(AMOUNT_FIELD_NAME, amount)
                put(ORDER_PRICE_FIELD_NAME, orderPrice)
                put(STATUS_FIELD_NAME, status)
            })
        }
        return json.toString()
    }

    fun createGetOrders(userId: String): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.ORDERS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(USER_ID_FIELD_NAME, userId)
            })
        }
        return json.toString()
    }

    fun createGetBasket(userId: String): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.BASKETS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(USER_ID_FIELD_NAME, userId)
            })
        }
        return json.toString()
    }

    fun checkExistEmail(email: String): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.USERS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(EMAIL_FIELD_NAME, email)
            })
        }
        return json.toString()
    }

    fun getAllCards(): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.CARDS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(ID_FIELD_NAME, ALL_VALUE)
            })
        }
        return json.toString()
    }

    fun updateCards(user: User): String {
        val json = createBasePacket(SocketActions.PATCH, SocketResources.USERS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(ID_FIELD_NAME, user.id.id)
                put(CARDS_NUMBER_FIELD_NAME, JSONArray().apply {
                    user.cardsNumber.forEach { card -> put(card) }
                })
            })
        }
        return json.toString()
    }

    fun getRanks(): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.RANKS)
        return json.toString()
    }

    fun getChart(
        stockName: String,
        start: String = "2022-12-22",
        end: String = "2023-01-21"
    ): String {
        val json = createBasePacket(SocketActions.GET, SocketResources.CHARTS).apply {
            put(PAYLOAD_FIELD_NAME, JSONObject().apply {
                put(STOCK_NAME_FIELD_NAME, stockName)
                put(START_FIELD_NAME, start)
                put(END_FIELD_NAME, end)
            })
        }
        return json.toString()
    }

    private fun createBasePacket(
        socketActions: SocketActions,
        socketResources: SocketResources
    ): JSONObject = JSONObject().apply {
        put(ACTION_FIELD_NAME, socketActions.name.lowercase())
        put(RESOURCE_FIELD_NAME, socketResources.name.lowercase())
    }

    companion object {
        const val ACTION_FIELD_NAME = "action"
        const val RESOURCE_FIELD_NAME = "resource"
        const val PAYLOAD_FIELD_NAME = "payload"

        const val ID_FIELD_NAME = "_id"
        const val EMAIL_FIELD_NAME = "email"
        const val NAME_FIELD_NAME = "name"
        const val TIMESTAMP_FIELD_NAME = "timestamp"
        const val PASSWORD_FIELD_NAME = "password"
        const val BALANCE_FIELD_NAME = "balance"
        const val SHARE_VALUE_FIELD_NAME = "shareValue"
        const val CARDS_NUMBER_FIELD_NAME = "cardsNumber"
        const val TOTAL_VALUE_FIELD_NAME = "totalValue"
        const val USER_ID_FIELD_NAME = "userId"
        const val AMOUNT_FIELD_NAME = "amount"
        const val TYPE_FIELD_NAME = "type"
        const val STOCK_NAME_FIELD_NAME = "stockName"
        const val ORDER_PRICE_FIELD_NAME = "orderPrice"
        const val CARD_NUMBER_FIELD_NAME = "cardNumber"
        const val STATUS_FIELD_NAME = "status"
        const val START_FIELD_NAME = "start"
        const val END_FIELD_NAME = "end"

        const val ALL_VALUE = "all"
    }
}