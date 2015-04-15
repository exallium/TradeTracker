package org.exallium.tradetracker.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TradeFragment extends Fragment {

    public static final String TRADE_ID = "TradeFragment.TradeId";

    public static TradeFragment createInstance(Bundle bundle) {
        TradeFragment fragment = new TradeFragment();
        Bundle args = new Bundle();
        args.putInt(TRADE_ID, bundle.containsKey(TRADE_ID) ? bundle.getInt(TRADE_ID) : -1);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
