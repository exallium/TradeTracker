package org.exallium.tradetracker.app.controller.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import org.exallium.tradetracker.app.BundleConstants;
import org.exallium.tradetracker.app.DialogScreen;

public class LineItemDialogFactory {

    public static DialogFragment createDialog(Bundle bundle) {

        DialogScreen screen = DialogScreen.values()[bundle.getInt(BundleConstants.SCREEN_ID)];

        switch (screen) {
            case LINE_ITEM_CARD_DIALOG:
                return new LineItemCardDialog();
            case LINE_ITEM_CASH_DIALOG:
                return new LineItemCashDialog();
            case LINE_ITEM_MISC_DIALOG:
                return new LineItemMiscDialog();
            default:
                return LineItemTypeDialog.createInstance(bundle);
        }

    }

}
