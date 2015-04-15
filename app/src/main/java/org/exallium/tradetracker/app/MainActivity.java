package org.exallium.tradetracker.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.orm.query.Condition;
import com.orm.query.Select;
import org.exallium.tradetracker.app.controller.adapters.DrawerNavAdapter;
import org.exallium.tradetracker.app.controller.adapters.ViewModelAdapterFactory;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.LineItem;
import org.exallium.tradetracker.app.model.entities.Person;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.joda.time.LocalDate;
import rx.Subscriber;

public class MainActivity extends Activity {

    private Screen currentScreen = Screen.NONE;

    @InjectView(R.id.fab) ImageButton fab;
    @InjectView(R.id.left_drawer) RecyclerView leftDrawer;

    private LocalDate localDate = LocalDate.now();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        ViewModelAdapterFactory.adapterSubject.subscribe(fragmentRequestSubscriber);
        leftDrawer.setLayoutManager(new LinearLayoutManager(this));
        leftDrawer.setAdapter(new DrawerNavAdapter(new Screen[]{Screen.TRADES, Screen.CARD_SETS}, fragmentRequestSubscriber));

        Trade.deleteAll(Trade.class);

        fab.setOnClickListener(v -> {
            Person person = Select.from(Person.class).where(Condition.prop("name").eq("Alex")).first();
            if (person == null) {
                person = new Person();
                person.name = "Alex";
                person.save();
            }

            Card card = Select.from(Card.class).where(Condition.prop("name").eq("Stoneforge Mystic")).first();
            LineItem lineItem = new LineItem();
            lineItem.value = 3500;
            lineItem.lastUpdated = LocalDate.now().toDate();
            lineItem.card = card;

            Trade trade = new Trade();
            trade.person = person;
            trade.tradeDate = localDate.toDate();
            trade.save();

            lineItem.trade = trade;
            lineItem.save();

            localDate = localDate.minusDays(1);
        });

        showFragment(Screen.TRADES, null);
    }

    private void showFragment(Screen screen, Bundle bundle) {

        if (screen != currentScreen) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, ListFragment.createInstance(screen, bundle));
            transaction.commit();

            currentScreen = screen;
        }
    }

    private Subscriber<Pair<Screen, Bundle>> fragmentRequestSubscriber = new Subscriber<Pair<Screen, Bundle>>() {
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
    };

}