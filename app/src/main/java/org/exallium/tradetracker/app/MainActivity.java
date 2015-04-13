package org.exallium.tradetracker.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ImageButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import org.exallium.tradetracker.app.model.entities.Person;
import org.exallium.tradetracker.app.model.entities.Trade;
import org.joda.time.LocalDate;

import java.util.Date;
import java.util.EnumMap;
import java.util.UUID;

public class MainActivity extends Activity {

    private Screen currentScreen = Screen.NONE;
    private final EnumMap<Screen, ListFragment> fragmentMap = new EnumMap<>(Screen.class);

    @InjectView(R.id.fab) ImageButton fab;

    private LocalDate localDate = LocalDate.now();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        fab.setOnClickListener(v -> {
            Realm realm = Realm.getInstance(this);

            realm.beginTransaction();
            Person person = realm.allObjects(Person.class).where()
                    .equalTo("name", "Alex")
                    .findFirst();
            if (person == null) {
                person = realm.createObject(Person.class);
                person.setName("Alex");
            }

            Trade trade = realm.createObject(Trade.class);
            trade.setUid(UUID.randomUUID().toString());
            trade.setPerson(person);

            trade.setTradeDate(localDate.toDate());
            realm.commitTransaction();
            localDate = localDate.minusDays(1);
        });

        showFragment(Screen.TRADES);
    }

    private void showFragment(Screen screen) {
        if (screen != currentScreen) {
            if (!fragmentMap.containsKey(screen)) {
                fragmentMap.put(screen, ListFragment.createInstance(screen));
            }

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentMap.get(screen));
            transaction.commit();

            currentScreen = screen;
        }
    }
}