package com.hrg.tradeapp.ui.main.transaction.addCardBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.crossclassify.trackersdk.data.model.FieldMetaData
import com.crossclassify.trackersdk.utils.ScreenNavigationTracking
import com.crossclassify.trackersdk.utils.base.TrackerBottomSheetDialogFragment
import com.hrg.tradeapp.App
import com.hrg.tradeapp.R
import com.hrg.tradeapp.databinding.BottomSheetAddCardBinding
import com.hrg.tradeapp.domain.models.Card
import com.hrg.tradeapp.util.MessageType
import com.hrg.tradeapp.util.alert.CustomSnackBar
import com.hrg.tradeapp.util.customPopupWindow.CustomPopupWindow
import com.hrg.tradeapp.util.gone
import com.hrg.tradeapp.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddCardBottomSheet : TrackerBottomSheetDialogFragment(), View.OnClickListener,
    AdapterView.OnItemClickListener {

    @Inject
    lateinit var snackBar: CustomSnackBar

    @Inject
    lateinit var popupWindow: CustomPopupWindow

    private val mViewModel by viewModels<AddCardViewModel>()

    private val mBinding: BottomSheetAddCardBinding by lazy {
        BottomSheetAddCardBinding.inflate(
            layoutInflater
        )
    }

    private val cards = ArrayList<Card>()

    private var selectedCard: Card? = null
        set(value) {
            mBinding.etCardNumber.setText(value?.cardNumber.toString())
            field = value
        }

    private val mNavController: NavController by lazy { findNavController() }

    private val checkField: Boolean
        get() {
            val cardNumbers = App.user?.cardsNumber
            if (cardNumbers != null) {
                for (i in cardNumbers) {
                    if (mBinding.etCardNumber.text.toString().toLong() == i) {
                        snackBar.showSnackBar(
                            requireContext(),
                            mBinding.root,
                            MessageType.ERROR,
                            getString(R.string.error_selected_card_exist)
                        )
                        return false
                    }
                }
            }
            return true
        }

    private var loading: Boolean = false
        set(value) {
            when (value) {
                true -> {
                    mBinding.loading.show()
                    mBinding.btnSubmit.setOnClickListener(null)
                    mBinding.etCardNumber.setOnClickListener(null)
                }
                false -> {
                    mBinding.loading.gone()
                    mBinding.btnSubmit.setOnClickListener(this)
                    mBinding.etCardNumber.setOnClickListener(this)
                }
            }
            field = value
        }

    override fun getExternalMetaData(): List<FieldMetaData>? = null

    override fun getFormName(): String = "add-card-form"

    override fun onResume() {
        super.onResume()
        ScreenNavigationTracking().trackNavigation(
            "/main_activity/bottom_sheet_add_card", "add-card-form"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.cards.observe(viewLifecycleOwner) {
            popupWindow.closePopupMenu()
            // store cards
            cards.clear()
            cards.addAll(it)

            // open popup
            val cards = List<String>(it.size) { index ->
                it[index].cardNumber.toString()
            }
            loading = false
            popupWindow.openPopupMenu(
                requireContext(), mBinding.etCardNumber, cards, this, false
            )
        }

        mViewModel.user.observe(viewLifecycleOwner) {
            App.user = it
            dismiss()
        }

        mBinding.etCardNumber.isFocusable = false
        mBinding.etCardNumber.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            mBinding.etCardNumber -> {
                loading = true
                mViewModel.getCards()
            }
            mBinding.btnSubmit -> {
                if (checkField) {
                    mViewModel.addCardToUser(selectedCard!!)
                    trackerClickSubmitButton()
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(3000)
                        dismiss()
                    }
                    trackerClickSubmitButton()
                    loading = true
                }
            }
        }
    }

    override fun onItemClick(adapter: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        selectedCard = cards[position]
        popupWindow.closePopupMenu()
    }
}