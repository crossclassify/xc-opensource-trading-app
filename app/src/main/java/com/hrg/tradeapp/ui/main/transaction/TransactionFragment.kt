package com.hrg.tradeapp.ui.main.transaction

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.crossclassify.trackersdk.utils.ScreenNavigationTracking
import com.google.android.material.tabs.TabLayout
import com.hrg.tradeapp.App
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.FragmentTransactionBinding
import com.hrg.tradeapp.domain.models.Transaction
import com.hrg.tradeapp.ui.adapter.TransactionAdapter
import com.hrg.tradeapp.util.*
import com.hrg.tradeapp.util.alert.CustomSnackBar
import com.hrg.tradeapp.util.base.BaseFragment
import com.hrg.tradeapp.util.customPopupWindow.CustomPopupWindow
import com.hrg.tradeapp.util.mInterface.ItemClick
import com.hrg.tradeapp.util.toolbar.ToolbarManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TransactionFragment : BaseFragment<TransactionViewModel, FragmentTransactionBinding>(),
    View.OnClickListener, TabLayout.OnTabSelectedListener, AdapterView.OnItemClickListener,
    ItemClick<Transaction> {

    @Inject
    lateinit var snackBar: CustomSnackBar

    @Inject
    lateinit var toolbarManager: ToolbarManager

    @Inject
    lateinit var popupWindow: CustomPopupWindow

    private var loading = false
        set(value) {
            when (value) {
                true -> {
                    mViewBindingFrag.btnSubmit.isEnabled = false
                    mViewBindingFrag.btnSubmit.text = ""
                    mViewBindingFrag.pbLoading.show()
                    mViewBindingFrag.btnSubmit.setOnClickListener(null)
                }
                false -> {
                    mViewBindingFrag.btnSubmit.isEnabled = true
                    mViewBindingFrag.btnSubmit.text = when (mViewBindingFrag.rbDeposit.isChecked) {
                        true -> getString(R.string.str_submit_deposit)
                        false -> getString(R.string.str_submit_withdraw)
                    }
                    mViewBindingFrag.pbLoading.gone()
                    mViewBindingFrag.btnSubmit.setOnClickListener(this)

                }
            }
            field = value
        }

    private val adapter by lazy { TransactionAdapter(this) }

    private val tabItems by lazy {
        List(2) { index ->
            val tab = mViewBindingFrag.toolbar.toolbarTl.newTab()
            if (index == 0) tab.text = getString(R.string.str_title_tab_transaction)
            else tab.text = getString(R.string.str_title_tab_transaction_list)
            tab
        }
    }

    private var selectedTab: TabLayout.Tab? = null

    private var btnSub = false

    override fun onResume() {
        super.onResume()
        ScreenNavigationTracking().trackNavigation(
            "/main_activity/fragment_transaction", "transaction-form"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        mViewModelFrag.getData()
    }

    private fun init() {
        initToolbar()
        initRecyclerView()
        initClicks()
        initCheckChange()
        initObserver()
        initFields()
    }

    private fun initObserver() {
        mViewModelFrag.transactions.observe(viewLifecycleOwner) {
            mViewModelFrag.getUser()
            adapter.setTransaction(it)
            loading = false
            if (btnSub) {
                changeTab(1)
                btnSub = false
            }
        }
        mViewModelFrag.socketErrors.observe(viewLifecycleOwner) {
            snackBar.showSnackBar(requireContext(), mViewBindingFrag.root, MessageType.INFO, it)
            if (it == "Ok.")
                mViewModelFrag.getData()
            else
                loading = false
        }
        mViewModelFrag.result.observe(viewLifecycleOwner) {
//            loading = false
//            mViewModelFrag.getData()
        }
        mViewModelFrag.user.observe(viewLifecycleOwner) {
            App.user = it
        }
        mViewModelFrag.cards.observe(viewLifecycleOwner) {
            mViewModelFrag.cardList = it
        }
    }

    private fun initToolbar() {
        toolbarManager.initToolbarWithTabLayout(
            mViewBindingFrag.toolbar,
            getString(R.string.str_title_transaction),
            R.drawable.ic_left_arrow,
            {
                mNavController.navigateUp()
            },
            null,
            null,
            tabItems,
            this
        )
    }

    private fun initRecyclerView() {
        mViewBindingFrag.rvTransaction.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBindingFrag.rvTransaction.adapter = adapter

//        val dividerItemDecoration = DividerItemDecoration(
//            mViewBindingFrag.rvTransaction.context, LinearLayoutManager.VERTICAL
//        )
//        mViewBindingFrag.rvTransaction.addItemDecoration(dividerItemDecoration)
    }

    private fun initClicks() {
        mViewBindingFrag.etCard.isFocusable = false
        mViewBindingFrag.etCard.setOnClickListener(this)

        mViewBindingFrag.btnSubmit.setOnClickListener(this)
    }

    private fun initCheckChange() {
        mViewBindingFrag.rgTypePage.setOnCheckedChangeListener { _, checkId ->
            when (checkId) {
                R.id.rbDeposit -> {
                    mViewBindingFrag.btnSubmit.text = getString(R.string.str_submit_deposit)
                }
                R.id.rbWithdraw -> {
                    mViewBindingFrag.btnSubmit.text = getString(R.string.str_submit_withdraw)
                }
            }
        }
        mViewBindingFrag.rbDeposit.isChecked = true
    }

    private fun initFields() {
        mViewBindingFrag.etCard.addTextChangedListener {
            mViewBindingFrag.tvErrorCard.gone()
        }
        mViewBindingFrag.etAmount.addTextChangedListener {
            mViewBindingFrag.tvErrorAmount.gone()
        }
    }

    private fun checkFields(): Boolean {
        var result = true
        if (mViewBindingFrag.etCard.text?.isEmpty() == true) {
            mViewBindingFrag.tvErrorCard.text = getString(R.string.error_select_card)
            mViewBindingFrag.tvErrorCard.show()
            result = false
        }
        if (mViewBindingFrag.etAmount.text?.isEmpty() == true) {
            mViewBindingFrag.tvErrorAmount.text = getString(R.string.error_enter_amount)
            mViewBindingFrag.tvErrorAmount.show()
            result = false
        } else {
            val amount = mViewBindingFrag.etAmount.text.toString().toFloat()
            if (amount == 0f) {
                mViewBindingFrag.tvErrorAmount.text = getString(R.string.error_enter_amount)
                mViewBindingFrag.tvErrorAmount.show()
                result = false
            }
        }
        return result
    }

    override fun getViewModel(): TransactionViewModel {
        val viewModel by viewModels<TransactionViewModel>()
        return viewModel
    }

    override fun getViewBinding(): FragmentTransactionBinding =
        FragmentTransactionBinding.inflate(layoutInflater)

    override fun getFormName(): String = "transaction-form"

    override fun onClick(view: View) {
        when (view) {
            mViewBindingFrag.etCard -> {
                val cards = List<String>((App.user?.cardsNumber?.size ?: 0) + 1) { index ->
                    if (index == App.user?.cardsNumber?.size) {
                        getString(R.string.str_add_card)
                    } else {
                        App.user?.cardsNumber?.get(index)?.toString()!!
                    }
                }
//                openCardSelectorDialog()
                popupWindow.openPopupMenu(
                    requireContext(), view, cards, this, true
                )
            }
            mViewBindingFrag.btnSubmit -> {
                if (checkFields()) {
                    loading = true
                    btnSub = true
                    val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH)
                    val date = Calendar.getInstance().time
                    val timestamp = dateFormat.format(date)
                    mViewModelFrag.addTransaction(
                        App.userId, when {
                            mViewBindingFrag.rbDeposit.isChecked -> mViewBindingFrag.etAmount.text.toString()
                                .toInt()
                            mViewBindingFrag.rbWithdraw.isChecked -> mViewBindingFrag.etAmount.text.toString()
                                .toInt() * -1
                            else -> mViewBindingFrag.etAmount.text.toString().toInt()
                        }, mViewBindingFrag.etCard.text.toString().toLong(), timestamp
                    )
                    trackerClickSubmitButton()

                    mViewBindingFrag.etCard.setText("")
                    mViewBindingFrag.etAmount.setText("")

//                    mViewModelFrag.getData()
                }
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> mViewBindingFrag.clTransaction.show()
            1 -> mViewBindingFrag.clTransactionList.show()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> mViewBindingFrag.clTransaction.gone()
            1 -> mViewBindingFrag.clTransactionList.gone()
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        when (position) {
            in App.user?.cardsNumber?.indices!! -> {
                val cardNumber = App.user!!.cardsNumber[position]
                mViewBindingFrag.etCard.setText(cardNumber.toString())
                val balance =
                    mViewModelFrag.cardList?.find { card -> card.cardNumber == cardNumber }?.balance
                        ?: 0f
                mViewBindingFrag.tvCardBalance.text =
                    getString(R.string.str_dollar_placeholder, balance.toString())
            }
            else -> {
                // open add card
                mNavController.navigate(R.id.action_transactionFragment_to_addCardBottomSheet)
            }
        }
        popupWindow.closePopupMenu()
    }

    override fun onItemClick(item: Transaction) {
        val action =
            TransactionFragmentDirections.actionTransactionFragmentToDetailTransactionBottomSheetDialogFragment(
                item
            )
        mNavController.navigate(action)
    }

    private fun changeTab(index: Int) {
        mViewBindingFrag.toolbar.toolbarTl.selectTab(tabItems[index])
        selectedTab = tabItems[index]
    }
}