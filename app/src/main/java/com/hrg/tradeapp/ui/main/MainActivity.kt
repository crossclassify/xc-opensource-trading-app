package com.hrg.tradeapp.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.crossclassify.trackersdk.data.model.FieldMetaData
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.ActivityMainBinding
import com.hrg.tradeapp.util.base.BaseActivity
import com.hrg.tradeapp.util.socket.SocketConnectionTools
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    @Inject
    lateinit var socketConnection: SocketConnectionTools

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding.navBottom.setupWithNavController(navHostFragment.navController)
    }

    override fun getViewModel(): MainViewModel {
        val viewModel by viewModels<MainViewModel>()
        return viewModel
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    fun change(item: String) {
        mViewBinding.navBottom.selectedItemId = R.id.searchFragment
        navHostFragment.navController.currentBackStackEntry?.savedStateHandle?.set("data", item)
    }
}