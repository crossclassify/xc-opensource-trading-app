package com.hrg.tradeapp.util.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.crossclassify.trackersdk.data.model.FieldMetaData
import com.crossclassify.trackersdk.utils.base.TrackerFragment

abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : TrackerFragment() {

    protected val mNavController: NavController by lazy { findNavController() }
    protected val mViewModelFrag by lazy { getViewModel() }
    protected val mViewBindingFrag: VB by lazy { getViewBinding() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mViewBindingFrag.root
    }

    abstract fun getViewModel(): VM

    abstract fun getViewBinding(): VB

    override fun getExternalMetaData(): List<FieldMetaData>? = null
}