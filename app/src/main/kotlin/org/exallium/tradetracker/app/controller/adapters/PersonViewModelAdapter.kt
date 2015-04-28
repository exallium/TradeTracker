package org.exallium.tradetracker.app.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.view.models.PersonViewModel
import rx.Observable

public class PersonViewModelAdapter(observable: Observable<PersonViewModel>) : ViewModelAdapter<PersonViewModel>(observable, PersonViewModelAdapter.modelComparator) {
    override fun onCreateHeaderViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<PersonViewModel>? {
        return HeaderViewHolder(LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false))
    }

    override fun onCreateModelViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<PersonViewModel>? {
        return ModelViewHolder(LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false))
    }

    companion object {
        private val modelComparator = comparator<PersonViewModel> { p0, p1 -> p0.name.charAt(0).compareTo(p1.name.charAt(0)) }
    }

    private inner class HeaderViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<PersonViewModel>(itemView) {
        override fun onBind(viewModel: PersonViewModel) {
            (itemView as TextView).setText(viewModel.name.substring(0, 1))
        }
    }

    private inner class ModelViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<PersonViewModel>(itemView) {
        override fun onBind(viewModel: PersonViewModel) {
            (itemView as TextView).setText(viewModel.name)
        }
    }
}
