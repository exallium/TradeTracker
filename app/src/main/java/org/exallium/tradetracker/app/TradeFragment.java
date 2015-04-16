package org.exallium.tradetracker.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.exallium.tradetracker.app.controller.forms.Form;
import org.exallium.tradetracker.app.controller.forms.TradeForm;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.exallium.tradetracker.app.view.widgets.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class TradeFragment extends Fragment {

    private static final String TAG = TradeFragment.class.getSimpleName();

    @InjectView(R.id.trade_pager) ViewPager viewPager;
    @InjectView(R.id.trade_tabs) SlidingTabLayout slidingTabLayout;

    private List<Form> forms = new ArrayList<>();

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
        return inflater.inflate(R.layout.fragment_trade, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        viewPager.setAdapter(new TradePagerAdapter());
        slidingTabLayout.setViewPager(viewPager);
    }

    private class TradePagerAdapter extends PagerAdapter {

        private int [] tabTitles = new int[] {R.string.trade_tab_details, R.string.trade_tab_details};


        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(tabTitles[position]);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            switch (tabTitles[position]) {
                default:
                    view = LayoutInflater.from(container.getContext()).inflate(R.layout.form_trade, container, false);
                    TradeForm form = new TradeForm(Trade.class, view);
                    forms.add(form);
                    long tradeId = getArguments().getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT);
                    if (tradeId != BundleConstants.NEW_OBJECT) {
                        form.initialize(tradeId);
                    }
                    container.addView(view);
            }

            return view;
        }
    }
}
