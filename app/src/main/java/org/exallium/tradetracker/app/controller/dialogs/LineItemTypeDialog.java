package org.exallium.tradetracker.app.controller.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.exallium.tradetracker.app.BundleConstants;
import org.exallium.tradetracker.app.DialogScreen;
import org.exallium.tradetracker.app.R;
import rx.android.view.ViewObservable;

public class LineItemTypeDialog extends DialogFragment {

    @InjectView(R.id.type_card) Button cardButton;
    @InjectView(R.id.type_cash) Button cashButton;
    @InjectView(R.id.type_misc) Button miscButton;

    public static LineItemTypeDialog createInstance(Bundle bundle) {
        Bundle args = new Bundle();
        args.putBundle(BundleConstants.BUNDLE_ID, bundle);
        LineItemTypeDialog lineItemTypeDialog = new LineItemTypeDialog();
        lineItemTypeDialog.setArguments(args);
        return lineItemTypeDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.type_select);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_line_item_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.inject(this, view);

        ViewObservable.clicks(cardButton).subscribe(onClickEvent -> {
            Bundle bundle = getArguments().getBundle(BundleConstants.BUNDLE_ID);
            bundle.putInt(BundleConstants.SCREEN_ID, DialogScreen.LINE_ITEM_CARD_DIALOG.getId());
            LineItemDialogFactory.createDialog(bundle).show(getChildFragmentManager(), "cardDialog");
            dismiss();
        });
    }
}
