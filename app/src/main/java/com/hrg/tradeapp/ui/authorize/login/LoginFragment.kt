package com.hrg.tradeapp.ui.authorize.login

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.crossclassify.trackersdk.utils.ScreenNavigationTracking
import com.hrg.tradeapp.App
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.FragmentLoginBinding
import com.hrg.tradeapp.ui.authorize.AuthorizeActivity
import com.hrg.tradeapp.util.MessageType
import com.hrg.tradeapp.util.alert.CustomSnackBar
import com.hrg.tradeapp.util.base.BaseFragment
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>(), View.OnClickListener {

    @Inject
    lateinit var snackBar: CustomSnackBar

    private var loading: Boolean = false
        set(value) {
            when (value) {
                true -> {
                    mViewBindingFrag.btnLogin.isEnabled = false
                    mViewBindingFrag.btnLogin.text = ""
                    mViewBindingFrag.pbLoading.show()
                    disableClicks()
                }
                false -> {
                    mViewBindingFrag.btnLogin.isEnabled = true
                    mViewBindingFrag.btnLogin.text = getString(R.string.str_login)
                    mViewBindingFrag.pbLoading.gone()
                    initClick()
                }
            }
            field = value
        }

    private var lastPassword: String = ""

    override fun onResume() {
        super.onResume()
        ScreenNavigationTracking().trackNavigation(
            "/authorize_activity/fragment_login", "login-form"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initObserver()
        initClick()
        initTextWatchers()
        mViewBindingFrag.tvGoToSignup.setText(SpannableString(getString(R.string.str_btn_signup)).apply {
            setSpan(UnderlineSpan(), 21, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        })
    }

    private fun initObserver() {
        mViewModelFrag.socketConnectionTools.userResponse.observe(viewLifecycleOwner) {
            App.user = it
            when (it) {
                null -> {
                    snackBar.showSnackBar(
                        requireContext(),
                        mViewBindingFrag.root,
                        MessageType.ERROR,
                        "Username or password not correct"
                    )
                }
                else -> {
                    if (it.password == lastPassword) {
                        snackBar.showSnackBar(
                            requireContext(),
                            mViewBindingFrag.root,
                            MessageType.SUCCESS,
                            "Login Successful"
                        )
                        if (requireActivity() is AuthorizeActivity) (requireActivity() as AuthorizeActivity).goToNextPage(
                            it.id.id
                        )
                    } else {
                        snackBar.showSnackBar(
                            requireContext(),
                            mViewBindingFrag.root,
                            MessageType.ERROR,
                            "Username or password not correct"
                        )
                    }
                }
            }
            loading = false
        }
    }

    private fun initTextWatchers() {
        mViewBindingFrag.etPassword.addTextChangedListener {
            mViewBindingFrag.tvErrorPassword.gone()
        }
        mViewBindingFrag.etEmail.addTextChangedListener {
            mViewBindingFrag.tvErrorEmail.gone()
        }
    }

    private fun initClick() {
        mViewBindingFrag.btnLogin.setOnClickListener(this)
        mViewBindingFrag.tvGoToSignup.setOnClickListener(this)
    }

    private fun disableClicks() {
        mViewBindingFrag.btnLogin.setOnClickListener(null)
        mViewBindingFrag.tvGoToSignup.setOnClickListener(null)
    }

    private fun checkFields(): Boolean {
        var result = true

        if (mViewBindingFrag.etEmail.text.toString().isEmpty()) {
            mViewBindingFrag.tvErrorEmail.text = getString(R.string.error_enter_email)
            mViewBindingFrag.tvErrorEmail.show()
            result = false
        }
        if (mViewBindingFrag.etPassword.text.toString().isEmpty()) {
            mViewBindingFrag.tvErrorPassword.text = getString(R.string.error_enter_password)
            mViewBindingFrag.tvErrorPassword.show()
            result = false
        }
        return result
    }

    private fun clearFields() {
        mViewBindingFrag.etEmail.setText("")
        mViewBindingFrag.etPassword.setText("")
    }

    override fun getViewModel(): LoginViewModel {
        val viewModel by viewModels<LoginViewModel>()
        return viewModel
    }

    override fun getViewBinding(): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater)

    override fun onClick(view: View?) {
        when (view) {
            mViewBindingFrag.btnLogin -> {
                if (checkFields()) {
                    lastPassword = mViewBindingFrag.etPassword.text.toString()
                    loading = true
                    mViewModelFrag.login(
                        mViewBindingFrag.etEmail.text.toString(),
                        mViewBindingFrag.etPassword.text.toString()
                    )
                    trackerClickSubmitButton()
                }
            }
            mViewBindingFrag.tvGoToSignup -> {
                mNavController.navigate(R.id.action_loginFragment_to_signupFragment)
            }
        }
    }

    override fun getFormName(): String = "login-form"
}