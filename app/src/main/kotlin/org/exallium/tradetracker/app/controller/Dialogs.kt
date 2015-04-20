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
import org.exallium.tradetracker.app.DialogScreen
import org.exallium.tradetracker.app.R
import rx.android.view.ViewObservable


public fun createDialogFragment(bundle : Bundle) : DialogFragment {
    val dialogScreen = DialogScreen.values()[bundle.getInt(BundleConstants.SCREEN_ID)]
    return when (dialogScreen) {
        DialogScreen.LINE_ITEM_CARD_DIALOG -> createDialogFragment(javaClass<LineItemCardDialog>(), bundle)
        DialogScreen.LINE_ITEM_CASH_DIALOG -> createDialogFragment(javaClass<LineItemCashDialog>(), bundle)
        DialogScreen.LINE_ITEM_MISC_DIALOG -> createDialogFragment(javaClass<LineItemMiscDialog>(), bundle)
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
        return inflater.inflate(R.layout.dialog_line_item_type, container, false);
    }

    public override fun onViewCreated(view : View, savedInstanceState : Bundle) {
        ViewObservable.clicks(cardButton).subscribe {
            onClickEvent -> doWorkflowStep(DialogScreen.LINE_ITEM_CARD_DIALOG)
        }

        ViewObservable.clicks(cashButton).subscribe {
            onClickEvent -> doWorkflowStep(DialogScreen.LINE_ITEM_CASH_DIALOG)
        }

        ViewObservable.clicks(miscButton).subscribe {
            onClickEvent -> doWorkflowStep(DialogScreen.LINE_ITEM_MISC_DIALOG)
        }
    }

    private fun doWorkflowStep(screen : DialogScreen) {
        val bundle = getArguments().getBundle(BundleConstants.BUNDLE_ID)
        bundle.putInt(BundleConstants.SCREEN_ID, screen.getId())
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
