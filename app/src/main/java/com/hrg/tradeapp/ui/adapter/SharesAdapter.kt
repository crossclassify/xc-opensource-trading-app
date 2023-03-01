package com.hrg.tradeapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.ItemSharesBinding
import com.hrg.tradeapp.domain.models.Basket
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.mInterface.ItemClick
import com.hrg.tradeapp.util.show

class SharesAdapter(private val listener: ItemClick<String>) :
    RecyclerView.Adapter<SharesAdapter.ViewHolder>() {
    private val data: ArrayList<Basket> = ArrayList()

    class ViewHolder(
        private val binding: ItemSharesBinding,
        private val listener: ItemClick<String>
    ) : RecyclerView.ViewHolder(binding.root), OnClickListener {

        private val context: Context
            get() = binding.root.context

        private var _item: Basket? = null
        fun bind(item: Basket) {
            _item = item
            when (adapterPosition) {
                0 -> binding.divider.gone()
                else -> binding.divider.show()
            }
            binding.tvAmount.text = item.amount.toString()
            binding.tvSymbol.text = item.stockName
            binding.tvValue.text = context.getString(
                R.string.str_dollar_placeholder,
                (item.orderPrice * item.amount).toString()
            )
//            val percentage = ((item.wacc / item.orderPrice) * 100).toInt() - 100
            val percentage = ((item.orderPrice - item.wacc) / item.wacc) * 100
            val profit = (item.orderPrice - item.wacc) * item.amount
            binding.tvPercentage.text =
                "${String.format("%.02f", profit)} \$\n(${String.format("%.02f", percentage)} %)"
            binding.tvPercentage.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (percentage >= 0) R.color.green_300 else R.color.red_500
                )
            )
            binding.trRoot.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            _item?.stockName?.let { listener.onItemClick(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemSharesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun setShares(data: List<Basket>) {
        val numberItems = this.data.size
        if (numberItems != 0) {
            this.data.clear()
        }
        this.data.addAll(data)
        if (numberItems != 0) {
            when {
                data.size > numberItems -> {
                    notifyItemRangeChanged(0, numberItems)
                    notifyItemRangeInserted(numberItems, data.size - numberItems)
                }
                data.size == numberItems -> {
                    notifyItemRangeChanged(0, numberItems)
                }
                data.size < numberItems -> {
                    notifyItemRangeChanged(0, data.size)
                    notifyItemRangeRemoved(data.size, numberItems - data.size)
                }
            }
        } else {
            notifyItemRangeInserted(0, data.size)
        }
    }
}