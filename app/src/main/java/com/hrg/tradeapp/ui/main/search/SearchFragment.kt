package com.hrg.tradeapp.ui.main.search

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.crossclassify.trackersdk.utils.ScreenNavigationTracking
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import com.hrg.tradeapp.App
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.FragmentSearchBinding
import com.hrg.tradeapp.domain.models.Basket
import com.hrg.tradeapp.domain.models.Price
import com.hrg.tradeapp.ui.adapter.SharesAdapter
import com.hrg.tradeapp.util.MessageType
import com.hrg.tradeapp.util.OrderType
import com.hrg.tradeapp.util.alert.CustomSnackBar
import com.hrg.tradeapp.util.base.BaseFragment
import com.hrg.tradeapp.util.customPopupWindow.CustomPopupWindow
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.mInterface.ItemClick
import com.hrg.tradeapp.util.show
import com.hrg.tradeapp.util.toolbar.ToolbarManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>(), View.OnClickListener,
    TabLayout.OnTabSelectedListener, AdapterView.OnItemClickListener, ItemClick<String> {

    @Inject
    lateinit var snackBar: CustomSnackBar

    @Inject
    lateinit var toolbarManager: ToolbarManager

    @Inject
    lateinit var popupWindow: CustomPopupWindow

    private val baskets = ArrayList<Basket>()

    private val adapter by lazy { SharesAdapter(this) }

    private val arg by navArgs<SearchFragmentArgs>()

    private var selectedShare: Price? = null
        set(value) {
            value?.let {
//                mViewBindingFrag.ivChart.loadImage(getChart(requireContext()))
//                mViewBindingFrag.lcChart.show()
                mViewBindingFrag.tvShareCurrentPriceField.text =
                    getString(R.string.str_share_current_price, "${it.sym} ")
                mViewBindingFrag.tvShareCurrentPrice.text = "${value.price} $"
                if (value != field) {
                    mViewModelFrag.getChartData(value.sym)
                    loadingChart = true
                }
            }
            field = value
            mViewBindingFrag.etAmount.setText("")
            mViewBindingFrag.etShareSymbol.setText(value?.sym ?: "")
            if (value == null) {
                mViewBindingFrag.lcChart.clear()
                mViewBindingFrag.tvShareCurrentPriceField.text =
                    getString(R.string.str_share_current_price, "")
                mViewBindingFrag.tvShareCurrentPrice.text = "0 $"
            }
        }

    private var selectedMyShare: Basket? = null
        set(value) {
            field = value
            selectedShare = mViewModelFrag.prices.find { price -> price.sym == value?.stockName }
            value?.let {
                baskets.find { basket -> basket.stockName == it.stockName }.let {
                    it?.let {
                        mViewBindingFrag.tvCurrentAmount.text = "${it.amount} ${it.stockName}"
                    }
                }
            }
        }

    private var orderType: OrderType = OrderType.BUY
        set(value) {
            field = value
            when (value) {
                OrderType.BUY -> {
                    if (!mViewBindingFrag.rbBuy.isChecked)
                        mViewBindingFrag.rbBuy.isChecked = true
                    mViewBindingFrag.divider.gone()
                    mViewBindingFrag.tvCurrentAmountField.gone()
                    mViewBindingFrag.tvCurrentAmount.gone()
                }
                OrderType.SELL -> {
                    if (!mViewBindingFrag.rbSell.isChecked)
                        mViewBindingFrag.rbSell.isChecked = true
                    mViewBindingFrag.divider.show()
                    mViewBindingFrag.tvCurrentAmountField.show()
                    mViewBindingFrag.tvCurrentAmount.show()
                }
            }
        }

    private val myBalance: Float
        get() = App.user?.balance ?: 0f

    private val tabItems by lazy {
        List(2) { index ->
            val tab = mViewBindingFrag.toolbar.toolbarTl.newTab()
            if (index == 0) tab.text = getString(R.string.str_title_tab_shares)
            else tab.text = getString(R.string.str_title_tab_my_shares)
            tab
        }
    }

    private var selectedTab: TabLayout.Tab? = null

    private var loading = false
        set(value) {
            when (value) {
                true -> {
                    mViewBindingFrag.btnSubmit.isEnabled = false
                    mViewBindingFrag.btnSubmit.text = ""
                    mViewBindingFrag.pbLoading.show()
                    mViewBindingFrag.pbLoading2.show()
                    mViewBindingFrag.btnSubmit.setOnClickListener(null)
                }
                false -> {
                    mViewBindingFrag.btnSubmit.isEnabled = true
                    mViewBindingFrag.btnSubmit.text = when (orderType) {
                        OrderType.BUY -> getString(R.string.str_submit_buy_order)
                        OrderType.SELL -> getString(R.string.str_submit_sell_order)
                    }
                    mViewBindingFrag.pbLoading.gone()
                    mViewBindingFrag.pbLoading2.gone()
                    mViewBindingFrag.btnSubmit.setOnClickListener(this)

                }
            }
            field = value
        }

    private var loadingChart: Boolean = false
        set(value) {
            when (value) {
                true -> mViewBindingFrag.pbLoadingChart.show()
                false -> mViewBindingFrag.pbLoadingChart.gone()
            }
            field = value
        }

    override fun onResume() {
        super.onResume()
        ScreenNavigationTracking().trackNavigation(
            "/main_activity/fragment_search", "order-form"
        )

        clearFields()
        clearSelected()
        init()
        mViewModelFrag.getData()
    }

    private fun init() {
        initToolbar()
        setBalance()
        initRecyclerView()
        initClicks()
        initCheckChange()
        initObserver()
        initTextWatcher()
        checkArgs()
        initChart()
    }

    private fun initChart() {
        mViewBindingFrag.lcChart.let { lineChart ->
            val xAxis = lineChart.xAxis
            xAxis.valueFormatter = object : ValueFormatter() {
                private val mFormat: SimpleDateFormat = SimpleDateFormat("MM/dd", Locale.ENGLISH)
                override fun getFormattedValue(value: Float): String {
                    return mFormat.format(Date(value.toLong()))
                }
            }
            lineChart.description = null
            lineChart.setNoDataText(getString(R.string.str_choose_symbol))
            lineChart.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.blue_100))
        }

    }

    private fun checkArgs() {
        mNavController.currentBackStackEntry?.savedStateHandle?.remove<String>("data")?.let {
            focusOnMyShare(it)
        }
        arg.inputModel?.let {
            focusOnMyShare(it.symbol)
        }
    }

    private fun setBalance() {
        mViewBindingFrag.tvYourBalance.text = "${App.user?.balance} $"
    }

    private fun initTextWatcher() {
        mViewBindingFrag.etAmount.addTextChangedListener {
            mViewBindingFrag.tvErrorAmount.gone()
            val number: Float = when (it.toString()) {
                "" -> 0f
                else -> it.toString().toFloat()
            }
            val result = number * (selectedShare?.price ?: 0f)
            mViewBindingFrag.tvTotalOrderPrice.text = "$result $"
        }
        mViewBindingFrag.etShareSymbol.addTextChangedListener {
            mViewBindingFrag.tvErrorShare.gone()
        }
    }

    private fun initObserver() {
        mViewModelFrag.basket.observe(viewLifecycleOwner) {
            baskets.clear()
            baskets.addAll(it)
            adapter.setShares(it)
            loading = false
        }

        mViewModelFrag.price.observe(viewLifecycleOwner) {
            mViewModelFrag.prices.clear()
            mViewModelFrag.prices.addAll(it)
        }
        mViewModelFrag.socketErrors.observe(viewLifecycleOwner) {
            if (it == "Ok.") {
                mViewModelFrag.getData()
                changeTab(1)
                clearFields()
            } else {
                snackBar.showSnackBar(
                    requireContext(), mViewBindingFrag.root, MessageType.ERROR, it
                )
            }
        }

        mViewModelFrag.chart.observe(viewLifecycleOwner) {
            val data = ArrayList<Entry>()
            for (i in it.entries) {
                data.add(Entry(i.key.toFloat(), i.value.toFloat()))
            }
            val comparator: Comparator<Entry> = Comparator<Entry> { o1, o2 -> o1.x.compareTo(o2.x) }
            data.sortWith(comparator)
            mViewBindingFrag.lcChart.data =
                LineData(LineDataSet(data.toList(), selectedShare?.sym ?: "").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.green_500)
                    setDrawCircles(false)
                    setDrawCircleHole(false)
                    setDrawValues(true)
                })

            mViewBindingFrag.lcChart.invalidate()
            loadingChart = false
        }
    }

    private fun initToolbar() {
        toolbarManager.initToolbarWithTabLayout(
            mViewBindingFrag.toolbar,
            getString(R.string.str_title_shares),
            R.drawable.ic_left_arrow,
            {
                mNavController.navigateUp()
            },
            null,
            null,
            tabItems,
            this
        )
        selectedTab = tabItems[0]
        mViewBindingFrag.toolbar.toolbarTl.selectTab(selectedTab)
    }

    private fun initRecyclerView() {
        mViewBindingFrag.rvMyShares.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBindingFrag.rvMyShares.adapter = adapter

//        val dividerItemDecoration = DividerItemDecoration(
//            mViewBindingFrag.rvMyShares.context, LinearLayoutManager.VERTICAL
//        )
//        mViewBindingFrag.rvMyShares.addItemDecoration(dividerItemDecoration)
    }

    private fun initClicks() {
        mViewBindingFrag.etShareSymbol.isFocusable = false
        mViewBindingFrag.etShareSymbol.setOnClickListener(this)

        mViewBindingFrag.btnSubmit.setOnClickListener(this)
    }

    private fun initCheckChange() {
        mViewBindingFrag.rgTypePage.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                R.id.rbBuy -> {
                    orderType = OrderType.BUY

                    mViewBindingFrag.btnSubmit.text = getString(R.string.str_submit_buy_order)
                }
                R.id.rbSell -> {
                    orderType = OrderType.SELL
                    mViewBindingFrag.divider.show()
                    mViewBindingFrag.tvCurrentAmountField.show()
                    mViewBindingFrag.tvCurrentAmount.show()
                    mViewBindingFrag.btnSubmit.text = getString(R.string.str_submit_sell_order)
                    clearSelected()
                }
            }
        }
        mViewBindingFrag.rbBuy.isChecked = true
    }

    private fun clearSelected() {
        val temp = baskets.find { basket -> basket.stockName == selectedShare?.sym }
        when (temp) {
            null -> {
                clearFields()
            }
            else -> {
                selectedMyShare = temp
            }
        }
    }

    private fun clearFields() {
        selectedMyShare = null
        selectedShare = null
//        mViewBindingFrag.lcChart.gone()
        mViewBindingFrag.etAmount.setText("")
        mViewBindingFrag.tvCurrentAmount.text = ""
//        mViewBindingFrag.ivChart.setImageDrawable(null)
    }

    private fun checkFields(): Boolean {
        var result = true
        if (selectedShare == null) {
            mViewBindingFrag.tvErrorShare.text = getString(R.string.error_select_share)
            mViewBindingFrag.tvErrorShare.show()
            result = false
        }
        if (mViewBindingFrag.etAmount.text.toString().isEmpty()) {
            mViewBindingFrag.tvErrorAmount.text = getString(R.string.error_enter_amount)
            mViewBindingFrag.tvErrorAmount.show()
            result = false
        }
        val amount =
            if (mViewBindingFrag.etAmount.text?.isNotEmpty() == true) mViewBindingFrag.etAmount.text.toString()
                .toFloat()
            else 0f
        if (amount == 0f) {
            mViewBindingFrag.tvErrorAmount.text = getString(R.string.error_enter_amount)
            mViewBindingFrag.tvErrorAmount.show()
            result = false
        } else {
            if (orderType == OrderType.SELL && amount > (selectedMyShare?.amount?.toFloat()
                    ?: 0f)
            ) {
                mViewBindingFrag.tvErrorAmount.text =
                    getString(R.string.error_entered_amount_not_valid)
                mViewBindingFrag.tvErrorAmount.show()
                result = false
            }
            if (orderType == OrderType.BUY) {
                val price = amount * (selectedShare?.price ?: 0f)
                if (price > myBalance) {
                    snackBar.showSnackBar(
                        requireContext(),
                        mViewBindingFrag.root,
                        MessageType.ERROR,
                        getString(R.string.error_amount_greater_balance),
                    )
                    result = false
                }
            }
        }
        return result
    }

    private fun getChart(context: Context): Drawable? {
        try {
            val icons: TypedArray = context.resources.obtainTypedArray(R.array.arrayCharts)
            val ran = (0..27).random()
            val d1 = icons.getDrawable(ran)!!
            icons.recycle()
            return d1
        } catch (e: Exception) {
        }
        return null
    }

    private fun changeTab(index: Int) {
        mViewBindingFrag.toolbar.toolbarTl.selectTab(tabItems[index])
        selectedTab = tabItems[index]
    }

    override fun getViewModel(): SearchViewModel {
        val viewModel by viewModels<SearchViewModel>()
        return viewModel
    }

    override fun getViewBinding(): FragmentSearchBinding =
        FragmentSearchBinding.inflate(layoutInflater)

    override fun getFormName(): String = "order-form"

    override fun onClick(view: View) {
        when (view) {
            mViewBindingFrag.etShareSymbol -> {
                val hasDifferent = baskets.size == 0
                val shares = when (orderType) {
                    OrderType.BUY -> {
                        List<String>(mViewModelFrag.prices.size) { index ->
                            mViewModelFrag.prices[index].sym
                        }
                    }
                    OrderType.SELL -> {
                        if (baskets.size != 0) {
                            List<String>(baskets.size) { index ->
                                baskets[index].stockName
                            }
                        } else {
                            listOf("Buy Shares!")
                        }
                    }
                }

                popupWindow.openPopupMenu(
                    requireContext(),
                    view,
                    shares,
                    this,
                    hasDifferent && orderType == OrderType.SELL
                )
            }
            mViewBindingFrag.btnSubmit -> {
                if (checkFields()) {
                    val amount = mViewBindingFrag.etAmount.text.toString().toFloat()
                    mViewModelFrag.modifyMyShare(
                        orderType!!,
                        selectedShare!!,
                        mViewBindingFrag.etAmount.text.toString().toInt()
                    )
                    trackerClickSubmitButton()
                    loading = true
//                    changeBalance(orderType!!, amount * selectedShare!!.price)
//                    clearFields()
//                    changeTab(1)
                }
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let { selectedTab = tab }
        when (tab?.position) {
            0 -> mViewBindingFrag.svSearch.show()
            1 -> mViewBindingFrag.clMyShares.show()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> mViewBindingFrag.svSearch.gone()
            1 -> mViewBindingFrag.clMyShares.gone()
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        when (orderType) {
            OrderType.BUY -> {
                selectedShare = mViewModelFrag.prices[position]
            }
            OrderType.SELL -> {
                if (baskets.size == 0) {
                    mViewBindingFrag.rbBuy.isChecked = true
                } else {
                    selectedMyShare = baskets[position]
                }
            }
        }

        popupWindow.closePopupMenu()
    }

    override fun onItemClick(item: String) {
        focusOnMyShare(item)
    }

    private fun focusOnMyShare(item: String) {
        changeTab(0)
        mViewBindingFrag.rbSell.isChecked = true
        selectedMyShare = baskets.find { data ->
            data.stockName == item
        }
    }
}