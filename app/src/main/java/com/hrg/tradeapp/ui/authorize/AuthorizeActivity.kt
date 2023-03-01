package com.hrg.tradeapp.ui.authorize

import android.content.Intent
import androidx.activity.viewModels
import com.hrg.tradeapp.App
import com.hrg.tradeapp.databinding.ActivityAuthorizeBinding
import com.hrg.tradeapp.ui.main.MainActivity
import com.hrg.tradeapp.util.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthorizeActivity : BaseActivity<AuthorizeViewModel, ActivityAuthorizeBinding>() {

    fun goToNextPage(userId: String) {
        App.userId = userId
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            Intent(this@AuthorizeActivity, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    override fun getViewModel(): AuthorizeViewModel {
        val viewModel by viewModels<AuthorizeViewModel>()
        return viewModel
    }

    override fun getViewBinding(): ActivityAuthorizeBinding =
        ActivityAuthorizeBinding.inflate(layoutInflater)
}