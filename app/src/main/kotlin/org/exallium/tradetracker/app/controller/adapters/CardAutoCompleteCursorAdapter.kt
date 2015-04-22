package org.exallium.tradetracker.app.controller.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.model.entities.Card
import java.util.ArrayList

public class CardAutoCompleteCursorAdapter : BaseAdapter(), Filterable {

    private var currentItems = ArrayList<Card>()

    private fun defaultSelect(): Select<Card> {
        return Select.from(javaClass<Card>());
    }

    private inner class CardFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            val select = defaultSelect().where(Condition.prop("name").like("%s%%".format(constraint)))
            results.count = select.count().toInt()
            results.values = select.list()
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            currentItems.clear()
            currentItems.addAll(results.values as List<Card>)
            notifyDataSetChanged()
        }

    }

    override fun getFilter(): Filter? {
        return CardFilter()
    }

    override fun getCount(): Int {
        return currentItems.size()
    }

    override fun getItemId(position: Int): Long {
        return currentItems.get(position).getId()
    }

    override fun getItem(position: Int): String? {
        val card = currentItems.get(position)
        return "%s [%s]".format(card.name, card.cardSet?.code)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view : TextView? = convertView as TextView?
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false) as TextView?
        }
        val card = getItem(position)
        view?.setText(card)
        return view
    }

}
