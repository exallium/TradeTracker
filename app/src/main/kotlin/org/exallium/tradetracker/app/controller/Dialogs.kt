package org.exallium.tradetracker.app.controller.dialogs

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.ButterKnifeViewHolder
import butterknife.bindView
import org.exallium.tradetracker.app.BundleConstants
import org.exallium.tradetracker.app.R
import rx.android.view.ViewObservable

public enum class DialogScreen(val id: Int) {
    LINE_ITEM_TYPE : DialogScreen(0)
    LINE_ITEM_CARD : DialogScreen(1)
    LINE_ITEM_CASH : DialogScreen(2)
    LINE_ITEM_MISC : DialogScreen(3)
}

public fun createDialogFragment(bundle : Bundle) : DialogFragment {
    val dialogScreen = DialogScreen.values()[bundle.getInt(BundleConstants.SCREEN_ID)]
    return when (dialogScreen) {
        DialogScreen.LINE_ITEM_CARD -> createDialogFragment(javaClass<LineItemCardDialog>(), bundle)
        DialogScreen.LINE_ITEM_CASH -> createDialogFragment(javaClass<LineItemCashDialog>(), bundle)
        DialogScreen.LINE_ITEM_MISC -> createDialogFragment(javaClass<LineItemMiscDialog>(), bundle)
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

    public override fun onCreateDialog(savedInstance : Bundle) : Dialog {
        val dialog = super.onCreateDialog(savedInstance)
        dialog.setTitle(R.string.type_select)
        return dialog
    }

    public override fun onCreateView(inflater : LayoutInflater, container : ViewGroup, savedInstanceState : Bundle) : View {
        return inflater.inflate(R.layout.dialog_line_item_type, container, false)
    }

    public override fun onViewCreated(view : View, savedInstanceState : Bundle) {
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
        val bundle = getArguments().getBundle(BundleConstants.BUNDLE_ID)
        bundle.putInt(BundleConstants.SCREEN_ID, screen.id)
        dismiss()
        createDialogFragment(bundle).show(getParentFragment().getChildFragmentManager(), "cardDialog")
    }

}

private class LineItemCardDialog : DialogFragment() {

}

private class LineItemCashDialog : DialogFragment() {

}

private class LineItemMiscDialog : DialogFragment() {

}
