package com.hrg.tradeapp.ui.main.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.BottomSheetDetailTransactionBinding
import com.hrg.tradeapp.util.loadImage

class DetailTransactionBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val mBinding: BottomSheetDetailTransactionBinding by lazy {
        BottomSheetDetailTransactionBinding.inflate(
            layoutInflater
        )
    }

    private val args by navArgs<DetailTransactionBottomSheetDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.transaction.let { transaction ->
            mBinding.tvAmount.text =
                getString(R.string.str_dollar_placeholder, transaction.amount.toString())
            mBinding.tvDate.text = transaction.timestamp
            mBinding.tvCard.text = transaction.cardNumber.toString()
            mBinding.tvId.text = (10000 until 999999).random().toString()
            when (transaction.amount > 0) {
                true -> {
                    mBinding.tvAmount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green_300
                        )
                    )
                    mBinding.ivTransactionType.loadImage(R.drawable.ic_square_up)
                }
                false -> {
                    mBinding.tvAmount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_500
                        )
                    )
                    mBinding.ivTransactionType.loadImage(R.drawable.ic_square_down)
                }
            }
        }
    }
}