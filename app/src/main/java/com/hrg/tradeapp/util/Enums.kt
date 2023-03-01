package com.hrg.tradeapp.util

enum class TransactionType {
    DEPOSIT, WITHDRAW
}

enum class OrderType {
    BUY, SELL
}

enum class AuthorizePageType {
    LOGIN, SIGNUP
}

enum class SocketResources {
    USERS, BASKETS, CARDS, TRANSACTIONS, ORDERS, PRICES, RANKS, CHARTS
}

enum class SocketActions {
    POST, GET, PATCH, SUBSCRIBE
}

enum class SocketConnection {
    CONNECTED, FAILED, CONNECTING
}

enum class MessageType {
    INFO, ERROR, SUCCESS
}