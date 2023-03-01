package com.hrg.tradeapp.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.crossclassify.trackersdk.utils.ScreenNavigationTracking
import com.hrg.tradeapp.App
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.FragmentPortfolioBinding
import com.hrg.tradeapp.ui.adapter.SharesAdapter
import com.hrg.tradeapp.ui.authorize.AuthorizeActivity
import com.hrg.tradeapp.ui.main.MainActivity
import com.hrg.tradeapp.util.base.BaseFragment
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.mInterface.ItemClick
import com.hrg.tradeapp.util.show
import com.hrg.tradeapp.util.toolbar.ToolbarManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PortFolioFragment : BaseFragment<PortfolioViewModel, FragmentPortfolioBinding>(),
    ItemClick<String> {

    @Inject
    lateinit var toolbarManager: ToolbarManager

    private var loadingUser: Boolean = false
        set(value) {
            when (value) {
                true -> {
                    mViewBindingFrag.loadingUser.show()
                }
                false -> {
                    mViewBindingFrag.loadingUser.gone()
                }
            }
            field = value
        }

    private var loadingBasket: Boolean = false
        set(value) {
            when (value) {
                true -> {
                    mViewBindingFrag.loadingShares.show()
                }
                false -> {
                    mViewBindingFrag.loadingShares.gone()
                }
            }
            field = value
        }

    private val adapter by lazy { SharesAdapter(this) }

    override fun onResume() {
        super.onResume()
        ScreenNavigationTracking().trackNavigation(
            "/main_activity/fragment_portFolio", "portFolio-page"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        getData()
    }

    private fun getData() {
        loadingUser = true
        loadingBasket = true
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            mViewModelFrag.getUser()
            mViewModelFrag.getBasket()
        }
    }

    private fun init() {
        initRecyclerView()
        initToolbar()
        initObserver()
    }

    private fun initObserver() {
        mViewModelFrag.socketConnectionTools.userResponse.observe(viewLifecycleOwner) {
            App.user = it
            it?.let {
                mViewBindingFrag.tvUsername.text = it.name
                mViewBindingFrag.tvBalance.text = getString(R.string.str_dollar_placeholder,it.balance.toString())
                mViewBindingFrag.tvTotalShareValue.text = getString(R.string.str_dollar_placeholder,it.shareValue.toString())
                mViewBindingFrag.tvTotalValue.text = getString(R.string.str_dollar_placeholder,it.totalValue.toString())
            }
            loadingUser = false
        }
        mViewModelFrag.socketConnectionTools.basket.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.setShares(if (it.size < 3) it else it.subList(0, 3))
                mViewBindingFrag.rlShares.show()
            } else {
                mViewBindingFrag.rlShares.gone()
            }
            loadingBasket = false
        }
    }

    private fun initRecyclerView() {
        mViewBindingFrag.rvCrypto.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBindingFrag.rvCrypto.adapter = adapter
    }

    private fun initToolbar() {
        toolbarManager.initMainToolbar(
            mViewBindingFrag.toolbar,
            getString(R.string.str_title_profile),
            R.drawable.ic_logout,
            {
                logout()
            }
        )
    }

    private fun logout() {
        Intent(requireContext(), AuthorizeActivity::class.java).also {
            requireContext().startActivity(it)
            requireActivity().finish()
        }
    }

    override fun getViewModel(): PortfolioViewModel {
        val viewModel by viewModels<PortfolioViewModel>()
        return viewModel
    }

    override fun getViewBinding(): FragmentPortfolioBinding =
        FragmentPortfolioBinding.inflate(layoutInflater)

    override fun getFormName(): String = "portFolio-page"

    override fun onItemClick(item: String) {
        (requireActivity() as MainActivity).change(item)
    }
}