package com.hrg.tradeapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.crossclassify.trackersdk.data.model.FieldMetaData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hrg.tradeapp.BuildConfig
import com.hrg.tradeapp.databinding.ActivitySplashBinding
import com.hrg.tradeapp.ui.authorize.AuthorizeActivity
import com.hrg.tradeapp.util.SocketConnection
import com.hrg.tradeapp.util.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding.tvVersionName.text = BuildConfig.VERSION_NAME

        mViewModel.socketConnectionStatus.observe(this) { status ->
            if (status == SocketConnection.CONNECTED) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(500)
                    Intent(this@SplashActivity, AuthorizeActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
            } else if (status == SocketConnection.FAILED) {
                MaterialAlertDialogBuilder(this).setTitle("Connection Failed")
                    .setMessage("Cannot connect to server.")
                    .setCancelable(false)
                    .setPositiveButton("Try again") { _, _ ->
                        mViewModel.socketConnectionTools.tryAgain()
                    }.setNegativeButton("Exit") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }.show()
            }
        }
    }

    override fun getViewModel(): SplashViewModel {
        val viewModel by viewModels<SplashViewModel>()
        return viewModel
    }

    override fun getViewBinding(): ActivitySplashBinding =
        ActivitySplashBinding.inflate(layoutInflater)
}