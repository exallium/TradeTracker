package org.exallium.tradetracker.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.EnumMap;

public class MainActivity extends Activity {

    private Screen currentScreen = Screen.NONE;
    private final EnumMap<Screen, ListFragment> fragmentMap = new EnumMap<>(Screen.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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