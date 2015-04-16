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
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.exallium.tradetracker.app.controller.adapters.DrawerNavAdapter;
import org.exallium.tradetracker.app.model.entities.Trade;
import rx.Subscriber;

public class MainActivity extends Activity {

    private Screen currentScreen = Screen.NONE;

    @InjectView(R.id.left_drawer) RecyclerView navRecyclerView;
    @InjectView(R.id.drawer_container) DrawerLayout drawerContainer;
    @InjectView(R.id.toolbar) Toolbar toolbar;


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
            toolbar.setTitle(screen.getName());
            currentScreen = screen;
        }

        drawerContainer.closeDrawer(Gravity.LEFT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fragmentRequestSubscriber != null && !fragmentRequestSubscriber.isUnsubscribed())
            fragmentRequestSubscriber.unsubscribe();
    }
}