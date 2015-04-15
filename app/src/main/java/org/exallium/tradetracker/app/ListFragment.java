package org.exallium.tradetracker.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.exallium.tradetracker.app.controller.adapters.ViewModelAdapter;
import org.exallium.tradetracker.app.controller.adapters.ViewModelAdapterFactory;
import rx.Subscriber;
import rx.Subscription;

public class ListFragment extends Fragment {

    private LinearLayoutManager linearLayoutManager;
    private ViewModelAdapter viewModelAdapter;

    public static ListFragment createInstance(Screen screen, Bundle bundle) {
        Bundle arguments = new Bundle();
        ListFragment fragment = new ListFragment();
        arguments.putInt(Screen.SCREEN_ID, screen.getId());
        arguments.putBundle(Screen.BUNDLE_ID, bundle);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list, container, false);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        Screen screen = Screen.getById(getArguments().getInt(Screen.SCREEN_ID));
        viewModelAdapter = ViewModelAdapterFactory.createAdapter(screen, getArguments().getBundle(Screen.BUNDLE_ID));
        recyclerView.setAdapter(viewModelAdapter);

        return recyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModelAdapter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModelAdapter.onPause();
    }
}
