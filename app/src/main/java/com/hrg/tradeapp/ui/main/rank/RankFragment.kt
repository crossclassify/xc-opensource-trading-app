package com.hrg.tradeapp.ui.main.rank

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.crossclassify.trackersdk.utils.ScreenNavigationTracking
import com.hrg.tradeapp.App
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.FragmentRankBinding
import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.ui.adapter.RankAdapter
import com.hrg.tradeapp.util.DATE_PATTERN
import com.hrg.tradeapp.util.base.BaseFragment
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.show
import com.hrg.tradeapp.util.toolbar.ToolbarManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RankFragment : BaseFragment<RankViewModel, FragmentRankBinding>() {

    @Inject
    lateinit var toolbarManager: ToolbarManager

    private val adapter by lazy { RankAdapter() }

    private var loading = false
        set(value) {
            when (value) {
                true -> mViewBindingFrag.loading.show()
                false -> mViewBindingFrag.loading.gone()
            }
            field = value
        }

    override fun onResume() {
        super.onResume()
        ScreenNavigationTracking().trackNavigation(
            "/main_activity/fragment_rank", "rank-page"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarManager.initMainToolbar(mViewBindingFrag.toolbar,
            getString(R.string.str_title_rank),
            R.drawable.ic_left_arrow,
            {
                mNavController.navigateUp()
            })

        loading = true
        mViewModelFrag.getData()

        mViewBindingFrag.rvRanks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBindingFrag.rvRanks.adapter = adapter

        mViewModelFrag.ranks.observe(viewLifecycleOwner) {
            adapter.setRank(it)
            findMe(it)
            val time = Calendar.getInstance().time
            mViewBindingFrag.tvUpdate.text = getString(
                R.string.str_update_time,
                SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH).format(time)
            )
            loading = false
        }
    }

    private fun findMe(it: List<User>) {
        val rank = it.indexOfFirst { user -> user.id.id == App.userId }
        if (rank != -1) {
            val rankText = rank + 1
            mViewBindingFrag.tvRank.text = rankText.toString()
            mViewBindingFrag.tvProfit.text =
                getString(R.string.str_dollar_placeholder, it[rank].totalValue.toString())
        }
    }

    override fun getViewModel(): RankViewModel {
        val viewModel by viewModels<RankViewModel>()
        return viewModel
    }

    override fun getViewBinding(): FragmentRankBinding = FragmentRankBinding.inflate(layoutInflater)
    override fun getFormName(): String = "rank-page"
}