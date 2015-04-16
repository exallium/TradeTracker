package org.exallium.tradetracker.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.orm.query.Condition;
import com.orm.query.Select;
import org.exallium.tradetracker.app.controller.adapters.DrawerNavAdapter;
import org.exallium.tradetracker.app.model.entities.Trade;
import rx.Subscriber;

public class MainActivity extends Activity {

    private Screen currentScreen = Screen.NONE;

    @InjectView(R.id.left_drawer) RecyclerView navRecyclerView;
    @InjectView(R.id.drawer_container) DrawerLayout drawerContainer;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.fab) ImageButton fab;


    private Subscriber<Pair<Screen, Bundle>> fragmentRequestSubscriber;

    private class FragmentRequestSubscriber extends Subscriber<Pair<Screen, Bundle>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Pair<Screen, Bundle> screenBundlePair) {
            showFragment(screenBundlePair.first, screenBundlePair.second);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        fragmentRequestSubscriber = new FragmentRequestSubscriber();
        MainApplication.fragmentRequestSubject.subscribe(fragmentRequestSubscriber);
        navRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navRecyclerView.setAdapter(new DrawerNavAdapter(new Screen[]{Screen.TRADES, Screen.CARD_SETS}, fragmentRequestSubscriber));

        Trade.deleteAll(Trade.class);
        showFragment(Screen.TRADES, null);
    }

    private void showFragment(Screen screen, Bundle bundle) {
        if (screen != currentScreen) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            switch(screen) {
                case TRADE:
                    transaction.replace(R.id.fragment_container, TradeFragment.createInstance(bundle));
                    break;
                default:
                    transaction.replace(R.id.fragment_container, ListFragment.createInstance(screen, bundle));
            }
            transaction.commit();
            currentScreen = screen;

            setupFabButton(screen);
            setupToolbar(screen, bundle);
        }

        drawerContainer.closeDrawer(Gravity.LEFT);
    }

    private void setupFabButton(Screen screen) {
        switch (screen) {
            case TRADES:
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(v -> MainApplication.fragmentRequestSubject.onNext(new Pair<>(Screen.TRADE, null)));
                break;
            default:
                fab.setVisibility(View.GONE);
        }
    }

    private void setupToolbar(Screen screen, Bundle bundle) {
        toolbar.setTitle(screen.getName());
        switch (screen) {
            case TRADE:
                if (bundle != null) {
                    long tradeId = bundle.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT);
                    if (tradeId != BundleConstants.NEW_OBJECT) {
                        Trade trade = Select.from(Trade.class).where(Condition.prop("id").eq(tradeId)).first();
                        toolbar.setSubtitle(getString(R.string.trade_with, trade.person.name));
                    } else {
                        toolbar.setSubtitle(getString(R.string.trade_subtitle_default));
                    }
                }
                break;
            default:
                toolbar.setSubtitle("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fragmentRequestSubscriber != null && !fragmentRequestSubscriber.isUnsubscribed())
            fragmentRequestSubscriber.unsubscribe();
    }
}