package org.exallium.tradetracker.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TradeFragment extends Fragment {

    public static TradeFragment createInstance(@Nullable Bundle bundle) {
        TradeFragment fragment = new TradeFragment();
        Bundle args = new Bundle();
        args.putLong(BundleConstants.TRADE_ID, bundle == null ? BundleConstants.NEW_OBJECT : bundle.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
