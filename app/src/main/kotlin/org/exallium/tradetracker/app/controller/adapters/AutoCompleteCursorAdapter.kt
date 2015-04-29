package org.exallium.tradetracker.app.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.model.entities.Record
import java.util.ArrayList

public open class AutoCompleteCursorAdapter<E : Record<E>>(val clazz: Class<E>, val filterField: String) : BaseAdapter(), Filterable {

    private var currentItems = ArrayList<E>()

    private fun defaultSelect(): Select<E> {
        return Select.from(clazz);
    }

    override fun getCount(): Int {
        return currentItems.size()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view : TextView? = convertView as TextView?
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false) as TextView?
        }
        val obj = getItem(position)
        view?.setText(obj)
        return view
    }

    protected fun getRecord(position: Int): E {
        return currentItems.get(position)
    }

    override fun getItem(position: Int): String? {
        return "%s[%d]".format(clazz.getName(), getRecord(position))
    }

    override fun getItemId(position: Int): Long {
        return currentItems.get(position).getId()
    }

    override fun getFilter(): Filter? {
        return RecordFilter()
    }

    private inner class RecordFilter : Filter() {
        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            currentItems.clear()
            currentItems.addAll(results.values as List<E>)
            notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            val select = defaultSelect().where(Condition.prop(filterField).like("%s%%".format(constraint)))
            results.count = select.count().toInt()
            results.values = select.list()
            return results
        }

    }
}

