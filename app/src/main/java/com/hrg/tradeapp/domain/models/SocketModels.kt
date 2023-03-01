package com.hrg.tradeapp.domain.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

data class UserResult(
    val result: List<User>,
    val message: String
)

data class User(
    @Json(name = "_id")
    val id: IdModel,
    val name: String,
    val password: String,
    val email: String,
    val balance: Float = 0f,
    val shareValue: Float = 0f,
    val shareValueUpdateAt: String = "",
    var cardsNumber: List<Long> = emptyList(),
    val totalValue: Float = 0f
)

data class Card(
    @Json(name = "_id")
    val id: IdModel,
    val cardNumber: Long,
    val balance: Float
)

data class Basket(
    @Json(name = "_id")
    val id: IdModel,
    val stockName: String,
    val amount: Int = 0,
    val orderPrice: Float = 0f,
    val orderPriceUpdateAt: String = "",
    val userId: String,
    @Json(name = "WACC")
    val wacc: Float = 0f
)

@Parcelize
data class Transaction(
    @Json(name = "_id")
    val id: IdModel,
    val timestamp: String = "",
    val amount: Int = 0,
    val cardNumber: Long,
    val userId: String = ""
) : Parcelable

data class Order(
    @Json(name = "_id")
    val id: IdModel,
    val type: String,
    val stockName: String,
    val userId: String,
    val amount: String,
    val orderPrice: String,
    val orderPriceTime: String,
    val status: String
)

data class Price(
    val sym: String,
    val price: Float,
)

@Parcelize
data class IdModel(
    @Json(name = "\$oid")
    val id: String
) : Parcelable