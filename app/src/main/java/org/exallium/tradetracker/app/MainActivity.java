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
import io.realm.Realm;
import org.exallium.tradetracker.app.controller.adapters.DrawerNavAdapter;
import org.exallium.tradetracker.app.controller.adapters.ViewModelAdapterFactory;
import org.exallium.tradetracker.app.model.RealmManager;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.LineItem;
import org.exallium.tradetracker.app.model.entities.Person;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.joda.time.LocalDate;
import rx.Subscriber;

import java.util.EnumMap;
import java.util.UUID;

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

        fab.setOnClickListener(v -> {
            Realm realm = RealmManager.INSTANCE.getRealm();

            realm.beginTransaction();
            Person person = realm.allObjects(Person.class).where()
                    .equalTo("name", "Alex")
                    .findFirst();
            if (person == null) {
                person = realm.createObject(Person.class);
                person.setName("Alex");
            }

            Card card = realm.allObjects(Card.class).where().contains("name", "Stoneforge").findFirst();
            LineItem lineItem = realm.createObject(LineItem.class);
            lineItem.setUid(UUID.randomUUID().toString());
            lineItem.setValue(3500);
            lineItem.setLastUpdated(LocalDate.now().toDate());
            lineItem.setCard(card);

            Trade trade = realm.createObject(Trade.class);
            trade.setUid(UUID.randomUUID().toString());
            trade.setPerson(person);
            trade.getLineItems().add(lineItem);

            trade.setTradeDate(localDate.toDate());
            realm.commitTransaction();
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