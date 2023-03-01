package com.hrg.tradeapp.ui.authorize.signup

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
import com.hrg.tradeapp.databinding.FragmentSignupBinding
import com.hrg.tradeapp.ui.authorize.AuthorizeActivity
import com.hrg.tradeapp.util.MessageType
import com.hrg.tradeapp.util.alert.CustomSnackBar
import com.hrg.tradeapp.util.base.BaseFragment
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : BaseFragment<SignupViewModel, FragmentSignupBinding>(),
    View.OnClickListener {

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
                    mViewBindingFrag.btnLogin.text = getString(R.string.str_singup)
                    mViewBindingFrag.pbLoading.gone()
                    initClick()
                }
            }
            field = value
        }

    private var lastPassword: String = ""
    private var lastName: String = ""
    private var lastEmail: String = ""

    override fun onResume() {
        super.onResume()
        ScreenNavigationTracking().trackNavigation(
            "/authorize_activity/fragment_signup", "signup-form"
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
        mViewBindingFrag.tvGoToSignup.setText(SpannableString(getString(R.string.str_btn_login)).apply {
            setSpan(UnderlineSpan(), 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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
        mViewModelFrag.socketConnectionTools.signupResponse.observe(viewLifecycleOwner) {
            when (it) {
                null -> {
                    // signup failed
                    snackBar.showSnackBar(
                        requireContext(),
                        mViewBindingFrag.root,
                        MessageType.ERROR,
                        "Signup Failed"
                    )
                }
                else -> {
                    // signup success
                    mViewModelFrag.getUser(it.id)
                }
            }
        }
        mViewModelFrag.socketConnectionTools.checkExistEmail.observe(viewLifecycleOwner) {
            if (it) {
                mViewBindingFrag.tvErrorEmail.text = getString(R.string.error_email_already_use)
                mViewBindingFrag.tvErrorEmail.show()
                loading = false
            } else {
                mViewModelFrag.signup(
                    lastEmail,
                    lastName,
                    lastPassword
                )
            }
        }
    }

    private fun initTextWatchers() {
        mViewBindingFrag.etName.addTextChangedListener {
            mViewBindingFrag.tvErrorName.gone()
        }
        mViewBindingFrag.etPassword.addTextChangedListener {
            mViewBindingFrag.tvErrorPassword.gone()
        }
        mViewBindingFrag.etEmail.addTextChangedListener {
            mViewBindingFrag.tvErrorEmail.gone()
        }
        mViewBindingFrag.etConfirmPassword.addTextChangedListener {
            mViewBindingFrag.tvErrorConfirmPassword.gone()
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
        lastEmail = mViewBindingFrag.etEmail.text.toString()
        lastName = mViewBindingFrag.etName.text.toString()

        var result = true
        if (mViewBindingFrag.etEmail.text.toString().isEmpty()) {
            mViewBindingFrag.tvErrorEmail.text = getString(R.string.error_enter_email)
            mViewBindingFrag.tvErrorEmail.show()
            result = false
        }
        if (mViewBindingFrag.etName.text.toString().isEmpty()) {
            mViewBindingFrag.tvErrorName.text = getString(R.string.error_enter_name)
            mViewBindingFrag.tvErrorName.show()
            result = false
        }
        if (mViewBindingFrag.etPassword.text.toString().isEmpty()) {
            mViewBindingFrag.tvErrorPassword.text = getString(R.string.error_enter_password)
            mViewBindingFrag.tvErrorPassword.show()
            result = false
        }
        if (mViewBindingFrag.etConfirmPassword.text.toString().isEmpty()) {
            mViewBindingFrag.tvErrorConfirmPassword.text =
                getString(R.string.error_enter_password)
            mViewBindingFrag.tvErrorConfirmPassword.show()
            result = false
        } else if (mViewBindingFrag.etConfirmPassword.text.toString() != mViewBindingFrag.etPassword.text.toString()) {
            mViewBindingFrag.tvErrorConfirmPassword.text =
                getString(R.string.error_enter_password_not_match)
            mViewBindingFrag.tvErrorConfirmPassword.show()
            result = false

        }

        return result
    }

    private fun clearFields() {
        mViewBindingFrag.etEmail.setText("")
        mViewBindingFrag.etName.setText("")
        mViewBindingFrag.etPassword.setText("")
        mViewBindingFrag.etConfirmPassword.setText("")
    }

    override fun getViewModel(): SignupViewModel {
        val viewModel by viewModels<SignupViewModel>()
        return viewModel
    }

    override fun getViewBinding(): FragmentSignupBinding =
        FragmentSignupBinding.inflate(layoutInflater)

    override fun getFormName(): String = "signup-form"
    override fun onClick(view: View?) {
        when (view) {
            mViewBindingFrag.btnLogin -> {
                if (checkFields()) {
                    lastPassword = mViewBindingFrag.etPassword.text.toString()
                    loading = true
                    mViewModelFrag.checkExist(mViewBindingFrag.etEmail.text.toString())
                }
                trackerClickSubmitButton()
            }
            mViewBindingFrag.tvGoToSignup -> {
                mNavController.navigateUp()
            }
        }
    }
}