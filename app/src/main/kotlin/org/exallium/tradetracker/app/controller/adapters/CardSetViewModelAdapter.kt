package org.exallium.tradetracker.app.controller.adapters

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.controller.MainApplication
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.Screen
import org.exallium.tradetracker.app.view.models.CardSetViewModel
import rx.Observable
import rx.Subscriber
import java.util.Comparator

public class CardSetViewModelAdapter(allObjectsObservable: Observable<CardSetViewModel>) : ViewModelAdapter<CardSetViewModel>(allObjectsObservable, CardSetViewModelAdapter.modelComparator) {

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<CardSetViewModel>? {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false) as TextView
        view.setTextColor(view.getContext().getResources().getColor(R.color.abc_secondary_text_material_light))
        return HeaderViewHolder(view)
    }

    override fun onCreateModelViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<CardSetViewModel>? {
        val view = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false) as TextView
        view.setEllipsize(TextUtils.TruncateAt.END)
        return ModelViewHolder(view)
    }

    private inner class HeaderViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<CardSetViewModel>(itemView) {
        override fun onBind(viewModel: CardSetViewModel) {
            (itemView as TextView).setText(viewModel.name.substring(0, 1))
        }
    }

    private inner class ModelViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<CardSetViewModel>(itemView), View.OnClickListener {

        private var model: CardSetViewModel? = null

        init {
            itemView.setOnClickListener(this)
        }

        override fun onBind(viewModel: CardSetViewModel) {
            this.model = viewModel
            (itemView as TextView).setText(viewModel.name)
        }

        override fun onClick(v: View) {
            val bundle = Bundle()
            bundle.putString(BundleConstants.CARD_SET, model!!.code)
            MainApplication.fragmentRequestedSubject.onNext(Pair<Screen, Bundle?>(Screen.CARDS, bundle))
        }
    }

    companion object {
        val modelComparator = comparator {
            rhs: CardSetViewModel, lhs: CardSetViewModel ->
            lhs.name.substring(0, 1).toLowerCase().compareTo(rhs.name.substring(0, 1).toLowerCase())
        }
    }
}