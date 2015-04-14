package org.exallium.tradetracker.app.controller.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.exallium.tradetracker.app.R;

public class DrawerNavAdapter extends RecyclerView.Adapter<DrawerNavAdapter.ViewHolder> {

    private final String[] navItems;

    public DrawerNavAdapter(String [] navItems) {
        this.navItems = navItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((TextView) holder.itemView).setText(navItems[position]);
    }

    @Override
    public int getItemCount() {
        return navItems.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}
