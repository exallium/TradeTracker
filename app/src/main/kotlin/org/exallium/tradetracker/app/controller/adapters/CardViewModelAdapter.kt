package org.exallium.tradetracker.app.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.view.models.CardViewModel
import rx.Observable

public class CardViewModelAdapter(allObjectsObservable: Observable<CardViewModel>) : ViewModelAdapter<CardViewModel>(allObjectsObservable, CardViewModelAdapter.modelComparator) {

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<CardViewModel>? {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false) as TextView
        view.setTextColor(view.getContext().getResources().getColor(R.color.abc_secondary_text_material_light))
        return HeaderViewHolder(view)
    }

    override fun onCreateModelViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<CardViewModel>? {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
        return ModelViewHolder(view)
    }

    private inner class HeaderViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<CardViewModel>(itemView) {

        public override fun onBind(viewModel: CardViewModel) {
            (itemView as TextView).setText(viewModel.name.substring(0, 1))
        }
    }

    private inner class ModelViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<CardViewModel>(itemView) {

        public override fun onBind(viewModel: CardViewModel) {
            (itemView as TextView).setText(viewModel.name)
        }
    }

    companion object {

        private val modelComparator = comparator { lhs: CardViewModel, rhs: CardViewModel ->
            lhs.name.substring(0, 1).toLowerCase().compareTo(rhs.name.substring(0, 1).toLowerCase())
        }
    }
}