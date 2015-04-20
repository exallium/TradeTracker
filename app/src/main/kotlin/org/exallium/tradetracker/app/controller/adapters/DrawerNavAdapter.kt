package org.exallium.tradetracker.app.controller.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.Screen
import rx.Subscriber

public class DrawerNavAdapter(private val navItems : Array<Screen>, private val subscriber : Subscriber<Pair<Screen, Bundle?>>?) : RecyclerView.Adapter<DrawerNavAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.screen = navItems.get(position)
        if (holder.itemView is TextView) {
            holder.itemView.setText(holder.screen.getName())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        return ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false))
    }

    override fun getItemCount(): Int {
        return navItems.size()
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View) {
            subscriber?.onNext(Pair<Screen, Bundle?>(screen, null))
        }

        var screen = Screen.NONE

    }
}
