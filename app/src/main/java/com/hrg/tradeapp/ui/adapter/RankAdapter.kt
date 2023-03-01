package com.hrg.tradeapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrg.tradeapp.App
import com.hrg.tradeapp.databinding.ItemRankBinding
import com.hrg.tradeapp.domain.models.User
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.show

class RankAdapter : RecyclerView.Adapter<RankAdapter.ViewHolder>() {
    private val data: ArrayList<User> = ArrayList()

    class ViewHolder(private val binding: ItemRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User) {
            when (adapterPosition) {
                0 -> binding.divider.gone()
                else -> binding.divider.show()
            }
            binding.tvId.text = item.name
            binding.tvRank.text = "${adapterPosition + 1}"
            binding.tvProfit.text = "${item.totalValue}$"
            binding.root.isSelected = item.id.id == App.userId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    fun setRank(data: List<User>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
}