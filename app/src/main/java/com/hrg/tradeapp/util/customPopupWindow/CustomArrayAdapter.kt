package com.hrg.tradeapp.util.customPopupWindow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.ItemCardAdapterBinding

class CustomArrayAdapter(
    context: Context,
    list: List<String>,
    private val hasLastDifferentItem: Boolean = true
) : ArrayAdapter<String>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = when (convertView) {
            null -> ItemCardAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
            else -> ItemCardAdapterBinding.bind(convertView)
        }
        bindDataToView(view, position)
        return view.root
    }

    private fun bindDataToView(view: ItemCardAdapterBinding, position: Int) {
        if (count - 1 == position && hasLastDifferentItem) {
            view.tvBody.setTextColor(ContextCompat.getColor(context, R.color.red_500))
        } else {
            view.tvBody.setTextColor(ContextCompat.getColor(context, R.color.black_200))
        }
        view.tvBody.text = getItem(position)
    }
}