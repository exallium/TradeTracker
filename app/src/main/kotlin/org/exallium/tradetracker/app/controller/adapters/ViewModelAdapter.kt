package org.exallium.tradetracker.app.controller.adapters

import android.app.Application
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.controller.MainApplication
import org.exallium.tradetracker.app.controller.Screen
import org.exallium.tradetracker.app.model.Observables
import org.exallium.tradetracker.app.view.models.TradeViewModel
import org.exallium.tradetracker.app.view.models.ViewModel
import rx.Observable
import rx.Subscriber
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.TreeSet

public abstract class ViewModelAdapter<VM : ViewModel>(private val observable : Observable<VM>, private val comparator : Comparator<VM>?) : RecyclerView.Adapter<ViewModelAdapter<VM>.ViewHolder>() {

    override final fun onBindViewHolder(holder: ViewHolder<VM>?, position: Int) {
        holder?.onBind(viewModels.get(position))
    }

    override fun getItemCount(): Int {
        return viewModels.size()
    }

    override final fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder<VM>? {
        return when (viewType) {
            HEADER_TYPE -> onCreateHeaderViewHolder(parent)
            else -> onCreateModelViewHolder(parent)
        }
    }

    override fun getItemViewType(position : Int) : Int {
        return if (headerPositions.contains(position)) HEADER_TYPE else MODEL_TYPE
    }

    protected abstract fun onCreateHeaderViewHolder(parent : ViewGroup?) : ViewHolder<VM>?
    protected abstract fun onCreateModelViewHolder(parent : ViewGroup?) : ViewHolder<VM>?

    public fun onPause() {
        unsubscribe()
        sugarUpdateSubscriber?.unsubscribe()
    }

    public fun onResume() {
        subscribe()
        sugarUpdateSubscriber = SugarUpdateSubscriber()
        MainApplication.onObjectSavedSubject.subscribe(sugarUpdateSubscriber)
    }

    private fun subscribe() {
        viewModels.clear()
        headerPositions.clear()
        notifyDataSetChanged()
        viewModelSubscriber = ViewModelSubscriber()
        observable.subscribe(viewModelSubscriber)
    }

    private fun unsubscribe() {
        viewModelSubscriber?.unsubscribe()
    }

    companion object {
        val TAG = "ViewModelAdapter"
        val HEADER_TYPE = 0
        val MODEL_TYPE = 1

        public fun create(screen : Screen, bundle : Bundle?) : ViewModelAdapter<out ViewModel> {

            val cardSetCode = bundle?.getString(BundleConstants.CARD_SET, null)
            val lineItemDirection = if (bundle != null) bundle.getBoolean(BundleConstants.LINE_ITEM_DIRECTION, false) else false
            val tradeId = if (bundle != null) bundle.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT) else BundleConstants.NEW_OBJECT

            return when (screen) {
                Screen.TRADE -> LineItemViewModelAdapter(Observables.getLineItemsObservable(lineItemDirection, tradeId))
                Screen.CARD_SETS -> CardSetViewModelAdapter(Observables.getCardSetObservable())
                Screen.CARDS -> CardViewModelAdapter(Observables.getCardObservable(cardSetCode))
                else -> TradeViewModelAdapter(Observables.getTradeObservable())
            }
        }
    }

    val viewModels = Collections.synchronizedList(ArrayList<VM>())
    val headerPositions = Collections.synchronizedSet(TreeSet<Int>())

    var sugarUpdateSubscriber : SugarUpdateSubscriber? = null
    var viewModelSubscriber : ViewModelSubscriber? = null

    private inner class SugarUpdateSubscriber : Subscriber<Any>() {
        override fun onCompleted() { }
        override fun onNext(t: Any?) { subscribe() }
        override fun onError(e: Throwable?) { Log.d(TAG, "Something Bad Happened", e) }
    }

    private inner class ViewModelSubscriber : Subscriber<VM>() {
        override fun onCompleted() { unsubscribe() }

        override fun onNext(t: VM?) {
            val prev = if (viewModels.size() == 0) null else viewModels.get(viewModels.lastIndex)

            if (comparator != null) {
                if (prev != null || comparator.compare(prev, t) != 0) {
                    headerPositions.add(viewModels.size())
                    viewModels.add(t)
                }
            }
            viewModels.add(t)
            notifyDataSetChanged()
        }

        override fun onError(e: Throwable?) { Log.d(TAG, "Something Bad Happened", e) }

    }

    public abstract class ViewHolder<VM : ViewModel>(itemView : View) : RecyclerView.ViewHolder(itemView) {
        public abstract fun onBind(viewModel : VM)
    }

}


