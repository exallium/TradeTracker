package org.exallium.tradetracker.app.controller.dialogs

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import com.exallium.djforms.lib.DJForm
import com.orm.SugarRecord
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.forms.*
import org.exallium.tradetracker.app.model.entities.LineItem
import org.exallium.tradetracker.app.model.entities.Trade
import rx.android.view.ViewObservable
import kotlin.reflect.KClass

public enum class DialogScreen(val id: Int, val clazz: Class<out DJForm>?, val rError: Int, val rTitle: Int) {
    LINE_ITEM_TYPE : DialogScreen(0, null, 0, 0)
    LINE_ITEM_CARD : DialogScreen(1, javaClass<CardForm>(), R.string.card_not_found, R.string.card_title)
    LINE_ITEM_CASH : DialogScreen(2, javaClass<CashForm>(), R.string.cash_not_found, R.string.cash_title)
    LINE_ITEM_MISC : DialogScreen(3, javaClass<MiscForm>(), R.string.misc_not_found, R.string.misc_title)

    companion object {
        public fun getById(id: Int): DialogScreen {
            DialogScreen.values().forEach { screen -> if (screen.id == id) return screen }
            return LINE_ITEM_TYPE
        }
    }
}

public fun createDialogFragment(bundle : Bundle) : DialogFragment {
    val dialogScreen = DialogScreen.values()[bundle.getInt(BundleConstants.SCREEN_ID)]
    return when (dialogScreen) {
        DialogScreen.LINE_ITEM_CARD -> createDialogFragment(javaClass<LineItemDialog>(), bundle)
        DialogScreen.LINE_ITEM_CASH -> createDialogFragment(javaClass<LineItemDialog>(), bundle)
        DialogScreen.LINE_ITEM_MISC -> createDialogFragment(javaClass<LineItemDialog>(), bundle)
        else -> createDialogFragment(javaClass<LineItemTypeDialog>(), bundle)
    }

}

private fun <T : DialogFragment> createDialogFragment(dialogClass: Class<T>, bundle: Bundle) : DialogFragment {
    var fragment = dialogClass.newInstance()
    fragment.setArguments(bundle)
    return fragment
}

private class LineItemTypeDialog : DialogFragment() {

    val cardButton : Button by bindView(R.id.type_card)
    val cashButton : Button by bindView(R.id.type_cash)
    val miscButton : Button by bindView(R.id.type_misc)

    public override fun onCreateDialog(savedInstance : Bundle?) : Dialog {
        val dialog = super.onCreateDialog(savedInstance)
        dialog.setTitle(R.string.type_select)
        return dialog
    }

    public override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View {
        return inflater.inflate(R.layout.form_type, container, false)
    }

    public override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        ViewObservable.clicks(cardButton).subscribe {
            onClickEvent -> doWorkflowStep(DialogScreen.LINE_ITEM_CARD)
        }

        ViewObservable.clicks(cashButton).subscribe {
            onClickEvent -> doWorkflowStep(DialogScreen.LINE_ITEM_CASH)
        }

        ViewObservable.clicks(miscButton).subscribe {
            onClickEvent -> doWorkflowStep(DialogScreen.LINE_ITEM_MISC)
        }
    }

    private fun doWorkflowStep(screen : DialogScreen) {
        val bundle = getArguments()
        bundle.putInt(BundleConstants.SCREEN_ID, screen.id)
        dismiss()
        createDialogFragment(bundle).show(getParentFragment().getChildFragmentManager(), "cardDialog")
    }

}

private class LineItemDialog: DialogFragment() {

    var submit: View? = null
    var dialogScreen = DialogScreen.LINE_ITEM_MISC
    var form: DJForm? = null

    public override fun onCreateDialog(savedInstance : Bundle?) : Dialog {
        val dialog = super.onCreateDialog(savedInstance)
        dialog.setTitle(dialogScreen.rTitle)
        return dialog
    }

    public override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        form = dialogScreen.clazz?.getConstructor(javaClass<Context>())?.newInstance(getActivity())
        val vg = form?.getFormViewGroup()

        // Insert the submit button into the view heirarchy
        submit = LayoutInflater.from(vg!!.getContext()).inflate(R.layout.submit, vg, false)
        vg.addView(submit)
        return vg
    }

    public override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (view != null) {
            ViewObservable.clicks(submit).subscribe {
                if (form != null && form?.isFormValid()?:false) {
                    val lineItem = LineItem()
                    form?.save(lineItem)
                    lineItem.trade = SugarRecord.findById(javaClass<Trade>(), getArguments().getLong(BundleConstants.TRADE_ID))
                    lineItem.direction = getArguments().getBoolean(BundleConstants.LINE_ITEM_DIRECTION)
                    lineItem.save()
                    dismiss()
                }else
                    Toast.makeText(getActivity(), dialogScreen.rError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    public override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        dialogScreen = DialogScreen.getById(args?.getInt(BundleConstants.SCREEN_ID)?:dialogScreen.id)
    }

}
