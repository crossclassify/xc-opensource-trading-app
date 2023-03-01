package com.hrg.tradeapp.util.socket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hrg.tradeapp.domain.models.*
import com.hrg.tradeapp.util.*
import com.hrg.tradeapp.util.event.SingleLiveEvent
import io.crossbar.autobahn.websocket.WebSocketConnection
import io.crossbar.autobahn.websocket.interfaces.IWebSocketConnectionHandler
import io.crossbar.autobahn.websocket.types.ConnectionResponse
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketConnectionTools @Inject constructor(
    private val socketPacketCreator: SocketPacketCreator
) : IWebSocketConnectionHandler {

    private val mActionResult = SingleLiveEvent<Boolean>()
    val actionResult: LiveData<Boolean>
        get() = mActionResult

    private val mErrorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = mErrorMessage

    private val mUser = SingleLiveEvent<User?>()
    val userResponse: LiveData<User?>
        get() = mUser

    private val mSignup = SingleLiveEvent<IdModel?>()
    val signupResponse: LiveData<IdModel?>
        get() = mSignup

    private val mCheckExistEmail = SingleLiveEvent<Boolean>()
    val checkExistEmail: LiveData<Boolean>
        get() = mCheckExistEmail

    private val mBasket = SingleLiveEvent<List<Basket>>()
    val basket: LiveData<List<Basket>>
        get() = mBasket

    private val mTransaction = SingleLiveEvent<List<Transaction>>()
    val transaction: LiveData<List<Transaction>>
        get() = mTransaction

    private val mCard = MutableLiveData<List<Card>>()
    val card: LiveData<List<Card>>
        get() = mCard

    private val mPrice = MutableLiveData<List<Price>>()
    val price: LiveData<List<Price>>
        get() = mPrice

    private val mOrder = SingleLiveEvent<List<Order>>()
    val order: LiveData<List<Order>>
        get() = mOrder

    private val mRanks = SingleLiveEvent<List<User>>()
    val ranks: LiveData<List<User>>
        get() = mRanks

    private val mChart = SingleLiveEvent<HashMap<Long, Double>>()
    val chart: LiveData<HashMap<Long, Double>>
        get() = mChart

    private var retryConnection = 0
    private var mConnection: WebSocketConnection? = null

    private var mStatus: MutableLiveData<SocketConnection> = SingleLiveEvent()
    val status: LiveData<SocketConnection>
        get() = mStatus

    private var authorizeActionType: AuthorizePageType? = null

    fun initConnection() {
        mConnection = WebSocketConnection()
        mConnection?.connect(BASE_SOCKET_URL, this)
    }

    fun destroyConnection() {
        mConnection?.sendClose()
    }

    fun sendData(payload: String, action: AuthorizePageType? = null): Boolean {
        println(payload)
        return if (mConnection?.isConnected == true) {
            action?.let { authorizeActionType = action }
            mConnection?.sendMessage(payload)
            true
        } else {
            false
        }
    }

    override fun onConnect(response: ConnectionResponse?) {
        mStatus.postValue(SocketConnection.CONNECTED)
        retryConnection = 0
        sendData(socketPacketCreator.createSubscribePrices())
    }

    override fun onOpen() {
        // nothing
        println("onOpen")
    }

    override fun onClose(code: Int, reason: String?) {
        retryConnection++
        when (retryConnection) {
            in 0..2 -> {
                mStatus.postValue(SocketConnection.CONNECTING)
                initConnection()
            }
            else -> {
                mStatus.postValue(SocketConnection.FAILED)
            }
        }
    }

    override fun onMessage(payload: String) {
        takeMessage(payload)
    }

    override fun onMessage(payload: ByteArray?, isBinary: Boolean) {
        // nothing
    }

    override fun onPing() {
        // nothing
    }

    override fun onPing(payload: ByteArray?) {
        // nothing
    }

    override fun onPong() {
        // nothing
    }

    override fun onPong(payload: ByteArray?) {
        // nothing
    }

    override fun setConnection(connection: WebSocketConnection?) {
        // nothing
    }

    fun tryAgain() {
        retryConnection = 0
        initConnection()
    }

    private fun takeMessage(payload: String) {
        println(payload)
        JSONObject(payload).let {
            if (it.has("kind")) {
                when (it.get("kind")) {
                    SocketResources.USERS.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is JSONObject) {
                            // signup response
                            val result = it.get("result").toString()
                                .toConvertStringJsonToModel(IdModel::class.java)
                            mSignup.postValue(result)
                        } else if (it.has("result") && it.get("result") is JSONArray) {
                            // login response
                            val array = it.get("result") as JSONArray
                            if (authorizeActionType == AuthorizePageType.SIGNUP) {
                                mCheckExistEmail.postValue(array.length() > 0)
                            } else if (authorizeActionType == AuthorizePageType.LOGIN) {
                                if (array.length() > 0) {
                                    mUser.postValue(
                                        array[0].toString()
                                            .toConvertStringJsonToModel(User::class.java)
                                    )
                                } else {
                                    mUser.postValue(null)
                                }
                            }
                        }
                    }
                    SocketResources.BASKETS.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is JSONArray) {
                            val array = (it.get("result") as JSONArray)
                            val result = Array<Basket>(array.length()) { index ->
                                (array[index].toString()
                                    .toConvertStringJsonToModel(Basket::class.java))
                            }
                            mBasket.postValue(result.toList())
                        }
                    }
                    SocketResources.CARDS.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is JSONArray) {
                            val result = it.getJSONArray("result")
                            mCard.postValue(List<Card>(result.length()) { index ->
                                (result[index].toString()
                                    .toConvertStringJsonToModel(Card::class.java))
                            })
                        }
                    }
                    SocketResources.TRANSACTIONS.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is JSONArray) {
                            val result = it.getJSONArray("result")
                            mTransaction.postValue(List<Transaction>(result.length()) { index ->
                                result[index].toString()
                                    .toConvertStringJsonToModel(Transaction::class.java)
                            })
                        } else{
                            mActionResult.postValue(true)
                            mErrorMessage.postValue(it.get("message")?.toString())
                        }
                    }
                    SocketResources.PRICES.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is JSONArray) {
                            val result = it.getJSONArray("result")
                            mPrice.postValue(List<Price>(result.length()) { index ->
                                result[index].toString()
                                    .toConvertStringJsonToModel(Price::class.java)
                            })
                        } else {
                            if (mPrice.value == null) {
                                mPrice.postValue(
                                    listOf(
                                        Price(sym = "AMZN", price = 0f),
                                        Price(sym = "WBD", price = 0f),
                                        Price(sym = "F", price = 0f),
                                        Price(sym = "AAPL", price = 0f),
                                        Price(sym = "NVDA", price = 0f),
                                        Price(sym = "AMD", price = 0f),
                                        Price(sym = "GOOGL", price = 0f),
                                        Price(sym = "T", price = 0f),
                                        Price(sym = "BABA", price = 0f),
                                        Price(sym = "TSLA", price = 0f),
                                    )
                                )
                            }
                        }
                    }
                    SocketResources.ORDERS.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is JSONArray) {
                            val result = it.getJSONArray("result")
                            mOrder.postValue(List<Order>(result.length()) { index ->
                                result[index].toString()
                                    .toConvertStringJsonToModel(Order::class.java)
                            })
                        } else if (it.has("result") && it.get("result") is JSONObject) {
                            mActionResult.postValue(true)
                            mErrorMessage.postValue(it.get("message")?.toString())
                        }
                    }
                    SocketResources.RANKS.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is JSONArray) {
                            val result = it.getJSONArray("result")
                            mRanks.postValue(List<User>(result.length()) { index ->
                                result[index].toString()
                                    .toConvertStringJsonToModel(User::class.java)
                            })
                        }
                    }
                    SocketResources.CHARTS.name.lowercase() -> {
                        if (it.has("result") && it.get("result") is String) {
                            try {
                                val result = JSONObject(it.getString("result"))
                                val data = HashMap<Long, Double>()
                                for (i in result.keys()) {
                                    data[i.toLong()] = result.getDouble(i)
                                }
                                mChart.postValue(data)
                            } catch (e: Exception) {
                                mErrorMessage.postValue(e.message.toString())
                            }
                        }
                    }
                }
            } else {
                try {
                    val message = it.get("message").toString()
                    if (message != "Connected.")
                        mErrorMessage.postValue(message)
                } catch (e: Exception) {

                }
            }
        }
    }
}