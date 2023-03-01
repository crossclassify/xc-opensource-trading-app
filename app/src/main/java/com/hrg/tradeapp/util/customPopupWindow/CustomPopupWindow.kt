package com.hrg.tradeapp.util.customPopupWindow

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow
import com.hrg.tradeapp.util.hideKeyboard
import javax.inject.Inject

class CustomPopupWindow @Inject constructor() {
    private var mPopupMenu: PopupWindow? = null

    fun openPopupMenu(
        context: Context,
        view: View,
        dataList: List<String>,
        onItemClickListener: AdapterView.OnItemClickListener,
        hasLastDifferentItem: Boolean
    ) {
        context.hideKeyboard(view)
        mPopupMenu?.dismiss()
        mPopupMenu = createPopupMenu(
            context,
            dataList,
            onItemClickListener,
            view.width,
            hasLastDifferentItem
        )
        mPopupMenu?.showAsDropDown(view, 0, 0) // show popup like dropdown list
    }

    fun closePopupMenu() {
        mPopupMenu?.dismiss()
    }

    private fun createPopupMenu(
        context: Context,
        dataList: List<String>,
        onItemClickListener: AdapterView.OnItemClickListener,
        width: Int,
        hasLastDifferentItem: Boolean
    ): PopupWindow {
        val popupWindow = PopupWindow(context)
        val adapter = CustomArrayAdapter(context, dataList, hasLastDifferentItem)
        // TODO: Check data list size and show item when data list is empty
        val listViewSort = ListView(context)
        listViewSort.adapter = adapter
        listViewSort.onItemClickListener = onItemClickListener
        popupWindow.isFocusable = true
        popupWindow.width = width
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.contentView = listViewSort
        return popupWindow
    }
}