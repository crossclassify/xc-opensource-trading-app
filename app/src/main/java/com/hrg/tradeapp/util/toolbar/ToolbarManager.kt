package com.hrg.tradeapp.util.toolbar

import android.view.View
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.hrg.tradeapp.databinding.ToolbarMainBinding
import com.hrg.tradeapp.databinding.ToolbarWithTabLayoutBinding
import com.hrg.tradeapp.util.loadImage
import javax.inject.Inject

class ToolbarManager @Inject constructor() {
    fun initMainToolbar(
        toolbar: ToolbarMainBinding,
        title: String,
        primaryBtnIcon: Int? = null,
        primaryBtnClickListener: View.OnClickListener,
        actionBtnIcon: Int? = null,
        actionBtnClickListener: View.OnClickListener? = null
    ) {
        toolbar.toolbarTvTitle.text = title

        primaryBtnIcon?.let { toolbar.toolbarBtnBack.loadImage(primaryBtnIcon) }
        toolbar.toolbarBtnBack.setOnClickListener(primaryBtnClickListener)

        actionBtnIcon?.let { toolbar.toolbarBtnAction.loadImage(actionBtnIcon) }
        toolbar.toolbarBtnAction.setOnClickListener(actionBtnClickListener)
    }

    fun clearMainToolbarClickListener(
        toolbar: ToolbarMainBinding
    ) {
        toolbar.toolbarBtnBack.setOnClickListener(null)
        toolbar.toolbarBtnAction.setOnClickListener(null)
    }

    fun initToolbarWithTabLayout(
        toolbar: ToolbarWithTabLayoutBinding,
        title: String,
        primaryBtnIcon: Int? = null,
        primaryBtnClickListener: View.OnClickListener,
        actionBtnIcon: Int? = null,
        actionBtnClickListener: View.OnClickListener? = null,
        tabs: List<TabLayout.Tab>,
        tabSelectedListener: OnTabSelectedListener
    ) {
        toolbar.toolbarTvTitle.text = title

        primaryBtnIcon?.let { toolbar.toolbarBtnBack.loadImage(primaryBtnIcon) }
        toolbar.toolbarBtnBack.setOnClickListener(primaryBtnClickListener)

        actionBtnIcon?.let { toolbar.toolbarBtnAction.loadImage(actionBtnIcon) }
        toolbar.toolbarBtnAction.setOnClickListener(actionBtnClickListener)

        toolbar.toolbarTl.addOnTabSelectedListener(tabSelectedListener)
        if (toolbar.toolbarTl.tabCount == 0) {
            for (tab in tabs) {
                val selected = tabs.indexOf(tab) == 0
                toolbar.toolbarTl.addTab(tab, selected)
            }
        }
    }
}