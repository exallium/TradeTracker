package org.exallium.tradetracker.app.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.view.models.LineItemViewModel
import rx.Observable

public class LineItemViewModelAdapter(allObjectsObservable: Observable<LineItemViewModel>) : ViewModelAdapter<LineItemViewModel>(allObjectsObservable, null) {

    // This should never get called
    override fun onCreateHeaderViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<LineItemViewModel>? {
        return null
    }

    override fun onCreateModelViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<LineItemViewModel>? {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
        return ModelViewHolder(view)
    }

    private inner class ModelViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<LineItemViewModel>(itemView) {

        override fun onBind(viewModel: LineItemViewModel) {
            (itemView as TextView).setText(viewModel.description)
        }
    }

}