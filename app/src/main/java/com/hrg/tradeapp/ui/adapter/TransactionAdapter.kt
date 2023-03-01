package com.hrg.tradeapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.ItemRecentTransactionBinding
import com.hrg.tradeapp.domain.models.Transaction
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.mInterface.ItemClick
import com.hrg.tradeapp.util.show

class TransactionAdapter(private val listener: ItemClick<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private val data: ArrayList<Transaction> = ArrayList()

    class ViewHolder(
        private val listener: ItemClick<Transaction>,
        private val binding: ItemRecentTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root), OnClickListener {

        private val context: Context
            get() = binding.root.context

        private var _item: Transaction? = null
        fun bind(item: Transaction) {
            _item = item
            when (adapterPosition) {
                0 -> binding.divider.gone()
                else -> binding.divider.show()
            }

            binding.tvAmount.text =
                context.getString(R.string.str_dollar_placeholder, item.amount.toString())
            val date = item.timestamp.split(" ")
            if (date.size == 2) {
                binding.tvDate.text = date[0]
                binding.tvTime.text = date[1]
            } else {
                binding.tvDate.text = ""
                binding.tvTime.text = ""
            }
            when {
                item.amount > 0 -> binding.tvAmount.isSelected = true
                item.amount < 0 -> binding.tvAmount.isSelected = false
            }
            binding.root.setOnClickListener(this)
        }

        override fun onClick(p0: View) {
            _item?.let { listener.onItemClick(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemRecentTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(listener, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun setTransaction(data: List<Transaction>) {
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