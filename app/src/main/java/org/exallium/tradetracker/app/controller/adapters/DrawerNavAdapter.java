package org.exallium.tradetracker.app.controller.adapters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.Screen;
import rx.Observable;
import rx.Subscriber;

public class DrawerNavAdapter extends RecyclerView.Adapter<DrawerNavAdapter.ViewHolder> {

    private final Screen[] navItems;

    private Subscriber<Pair<Screen, Bundle>> subscriber = null;
    private final Observable<Pair<Screen, Bundle>> onNavigationClickedObservable = Observable.create(subscriber -> this.subscriber = (Subscriber<Pair<Screen, Bundle>>) subscriber);

    public DrawerNavAdapter(Screen [] navItems, Subscriber<Pair<Screen, Bundle>> subscriber) {
        this.navItems = navItems;
        onNavigationClickedObservable.subscribe(subscriber);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.screen = navItems[position];
        ((TextView) holder.itemView).setText(navItems[position].getName());
    }

    @Override
    public int getItemCount() {
        return navItems.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        private Screen screen = Screen.NONE;

        @Override
        public void onClick(View v) {
            if (subscriber != null) {
                subscriber.onNext(new Pair<>(screen, null));
            }
        }
    }

}
