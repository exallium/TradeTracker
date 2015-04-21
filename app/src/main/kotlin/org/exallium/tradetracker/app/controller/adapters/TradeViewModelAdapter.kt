package org.exallium.tradetracker.app.controller.adapters

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.controller.MainApplication
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.Screen
import org.exallium.tradetracker.app.utils.printForDisplay
import org.exallium.tradetracker.app.view
import org.exallium.tradetracker.app.view.models.TradeViewModel
import org.exallium.tradetracker.app.view.transformations.CircleTransformation
import org.exallium.tradetracker.app.view.transformations.MTGCardCropTransformation
import rx.Observable
import java.util.Arrays

public class TradeViewModelAdapter(observable: Observable<TradeViewModel>) : ViewModelAdapter<TradeViewModel>(observable, TradeViewModelAdapter.modelComparator) {

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<TradeViewModel>? {
        val itemView = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
        return HeaderViewHolder(itemView)
    }

    override fun onCreateModelViewHolder(parent: ViewGroup?): ViewModelAdapter.ViewHolder<TradeViewModel>? {
        val itemView = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.list_item_trade, parent, false)
        return TradeViewHolder(itemView)
    }

    class HeaderViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<TradeViewModel>(itemView) {

        override fun onBind(viewModel: TradeViewModel) {
            (itemView as TextView).setText(viewModel.date.printForDisplay(itemView.getContext().getResources()))
        }
    }

    class TradeViewHolder(itemView: View) : ViewModelAdapter.ViewHolder<TradeViewModel>(itemView), View.OnClickListener {

        val tradeImage: ImageView by bindView(R.id.trade_image)
        val tradeValueCount: TextView by bindView(R.id.trade_value_count)
        val tradePersonCards: TextView by bindView(R.id.trade_person_cards)

        private var tradeViewModel: TradeViewModel? = null

        override fun onBind(viewModel: TradeViewModel) {

            tradeViewModel = viewModel

            Picasso.with(tradeImage.getContext()).load(viewModel.imagePath).transform(Arrays.asList<Transformation>(MTGCardCropTransformation(), CircleTransformation())).into(tradeImage)

            tradeValueCount.setText("%s with %d items traded".format(viewModel.formattedValue, viewModel.lineItemCount))

            val tradePersonCardsBuilder = SpannableStringBuilder().append("with %s - ".format(viewModel.with))
            val start = tradePersonCardsBuilder.length()
            val end = start + viewModel.cardsTraded.length()
            tradePersonCardsBuilder.append(viewModel.cardsTraded)
            tradePersonCardsBuilder.setSpan(ForegroundColorSpan(Color.LTGRAY), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            tradePersonCards.setText(tradePersonCardsBuilder)

        }

        override fun onClick(v: View) {
            if (tradeViewModel != null) {
                val bundle = Bundle()
                bundle.putLong(BundleConstants.TRADE_ID, tradeViewModel!!.id)
                MainApplication.fragmentRequestedSubject.onNext(Pair<Screen, Bundle?>(Screen.TRADE, bundle))
            }
        }
    }

    companion object {

        private val modelComparator = comparator { lhs : TradeViewModel, rhs : TradeViewModel -> lhs.date.compareTo(rhs.date) }
    }

}