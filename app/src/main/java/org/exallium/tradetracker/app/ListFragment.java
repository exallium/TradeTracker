package org.exallium.tradetracker.app;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListFragment extends Fragment {

    private static final String SCREEN_ID = "ListFragment.screenId";

    private LinearLayoutManager linearLayoutManager;

    public static ListFragment createInstance(Screen screen) {
        Bundle arguments = new Bundle();
        ListFragment fragment = new ListFragment();
        arguments.putInt(SCREEN_ID, screen.getId());
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_list, container, false);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Set Adapter


        return recyclerView;
    }
}
