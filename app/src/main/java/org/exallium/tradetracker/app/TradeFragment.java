package org.exallium.tradetracker.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.exallium.tradetracker.app.controller.adapters.ViewModelAdapterFactory;
import org.exallium.tradetracker.app.controller.dialogs.DialogsPackage;
import org.exallium.tradetracker.app.controller.forms.TradeForm;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.exallium.tradetracker.app.view.widgets.SlidingTabLayout;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class TradeFragment extends Fragment {

    public static final Subject<Void, Void> createLineItemSubject = PublishSubject.create();

    private static final String TAG = TradeFragment.class.getSimpleName();

    @InjectView(R.id.trade_pager) ViewPager viewPager;
    @InjectView(R.id.trade_tabs) SlidingTabLayout slidingTabLayout;

    private TradeForm tradeForm;

    private ActivityViewHolder viewHolder = new ActivityViewHolder();
    class ActivityViewHolder {
        @InjectView(R.id.fab) ImageButton fab;
    }

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

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                viewHolder.fab.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ButterKnife.inject(viewHolder, activity);

        viewHolder.fab.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() != 0) {

                long tradeId = getArguments().getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT);
                if (tradeId == BundleConstants.NEW_OBJECT) {
                    tradeForm.forceSave();
                    Trade trade = tradeForm.getEntity();
                    tradeId = trade.getId();
                }

                Bundle bundle = new Bundle();
                bundle.putLong(BundleConstants.TRADE_ID, tradeId);
                bundle.putBoolean(BundleConstants.LINE_ITEM_DIRECTION, viewPager.getCurrentItem() == 1);
                bundle.putInt(BundleConstants.SCREEN_ID, DialogScreen.LINE_ITEM_TYPE_DIALOG.getId());
                DialogsPackage.createDialogFragment(bundle).show(getChildFragmentManager(), "lineItemDialog");
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewHolder.fab = null;
    }

    private class TradePagerAdapter extends PagerAdapter {

        private int [] tabTitles = new int[] {R.string.trade_tab_details, R.string.trade_tab_from, R.string.trade_tab_to};

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
            long tradeId = getArguments().getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT);
            switch (tabTitles[position]) {
                case R.string.trade_tab_details:
                    view = LayoutInflater.from(container.getContext()).inflate(R.layout.form_trade, container, false);
                    tradeForm = new TradeForm(view);
                    if (tradeId != BundleConstants.NEW_OBJECT) {
                        tradeForm.initialize(tradeId);
                    }
                    container.addView(view);
                    break;
                default:
                    view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_list, container, false);
                    RecyclerView rv = (RecyclerView) view;
                    boolean direction = tabTitles[position] == R.string.trade_tab_from;
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(BundleConstants.LINE_ITEM_DIRECTION, direction);
                    bundle.putLong(BundleConstants.TRADE_ID, tradeId);
                    rv.setAdapter(ViewModelAdapterFactory.createAdapter(Screen.TRADE, bundle));
                    container.addView(view);
            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
