package com.hrg.tradeapp.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

object FakeData {
    var fakeMyBalance: Float = 2000f

    val fakeCard = arrayListOf(
        FakeCard(
            "Card 1",
            "6037603760376037"
        ),
        FakeCard(
            "Card 2",
            "5859585958595859"
        )
    )

    val fakeTransactions = arrayListOf(
        FakeTransaction(
            "2022/12/04",
            "10:20",
            "1300",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.DEPOSIT
        ),
        FakeTransaction(
            "2022/12/04",
            "10:21",
            "1200",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.WITHDRAW
        ),
        FakeTransaction(
            "2022/12/04",
            "10:22",
            "1500",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.DEPOSIT
        ),
        FakeTransaction(
            "2022/12/04",
            "10:23",
            "1500",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.WITHDRAW
        ), FakeTransaction(
            "2022/12/04",
            "10:24",
            "1300",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.DEPOSIT
        ),
        FakeTransaction(
            "2022/12/04",
            "10:25",
            "1200",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.WITHDRAW
        ),
        FakeTransaction(
            "2022/12/04",
            "10:26",
            "1500",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.DEPOSIT
        ),
        FakeTransaction(
            "2022/12/04",
            "10:27",
            "1500",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.WITHDRAW
        ), FakeTransaction(
            "2022/12/04",
            "10:28",
            "1300",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.DEPOSIT
        ),
        FakeTransaction(
            "2022/12/04",
            "10:29",
            "1200",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.WITHDRAW
        ),
        FakeTransaction(
            "2022/12/04",
            "10:30",
            "1500",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.DEPOSIT
        ),
        FakeTransaction(
            "2022/12/04",
            "10:40",
            "1500",
            fakeCard[Random.nextInt(2)].number,
            TransactionType.WITHDRAW
        )
    )

    //    val fakeShares = arrayOf(
//        FakeShares(
//            "AAPL",
//            "0.5",
//            "72"
//        ),
//        FakeShares(
//            "TSLA",
//            "2",
//            "364"
//        ),
//        FakeShares(
//            "BTC",
//            "0.5",
//            "15000"
//        )
//    )
    val fakeRank = arrayOf(
        FakeRank(
            "35135",
            "1",
            "500"
        ),
        FakeRank(
            "35165135",
            "2",
            "400"
        ),
        FakeRank(
            "351515135",
            "3",
            "300"
        ),
        FakeRank(
            "35135",
            "4",
            "200"
        ),
        FakeRank(
            "3515134235",
            "5",
            "100",
            true
        ),
    )
    val fakeShares = listOf(
        FakeShare(
            "AAPL",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "AMD",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "AMZN",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "GOOGLE",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "META",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "MSFT",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "NDAQ",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "NFLX",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "QQQ",
            Random.nextInt(100, 300).toFloat()
        ),
        FakeShare(
            "TSLA",
            Random.nextInt(100, 300).toFloat()
        )
    )
    val fakeMyShare = arrayListOf(
        FakeMyShare(
            fakeShares[0],
            5f
        ),
        FakeMyShare(
            fakeShares[2],
            0.5f
        ),
        FakeMyShare(
            fakeShares[3],
            4f
        )
    )
}

@Parcelize
data class FakeTransaction(
    val date: String,
    val time: String,
    val amount: String,
    val card: String,
    val type: TransactionType
) : Parcelable

data class FakeRank(
    val id: String,
    val rank: String,
    val profit: String,
    val me: Boolean = false
)

data class FakeCard(
    val name: String,
    val number: String
)

@Parcelize
data class FakeShare(
    val symbol: String,
    val price: Float,
    val chartData: List<FakeChartData>? = null
) : Parcelable

@Parcelize
data class FakeChartData(
    val price: Float
) : Parcelable

@Parcelize
data class FakeMyShare(
    val share: FakeShare,
    val amount: Float
) : Parcelable