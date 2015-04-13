package org.exallium.tradetracker.app.controller.adapters;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.utils.date.DateFormat;
import org.exallium.tradetracker.app.view.models.TradeViewModel;
import rx.Observable;

public class TradeViewModelAdapter extends ViewModelAdapter<TradeViewModel> {

    public TradeViewModelAdapter(Observable<TradeViewModel> observable) {
        super(observable, (lhs, rhs) -> lhs.getDate().compareTo(rhs.getDate()));
    }

    @Override
    protected ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new HeaderViewHolder(itemView);
    }

    @Override
    public ViewModelAdapter<TradeViewModel>.ViewHolder onCreateModelViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trade, parent, false);
        return new TradeViewHolder(itemView);
    }

    class HeaderViewHolder extends ViewModelAdapter<TradeViewModel>.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(TradeViewModel viewModel) {
            ((TextView) itemView).setText(DateFormat.toString(itemView.getResources(), viewModel.getDate()));
        }
    }

    class TradeViewHolder extends ViewModelAdapter<TradeViewModel>.ViewHolder {

        @InjectView(R.id.trade_image)       ImageView tradeImage;
        @InjectView(R.id.trade_value_count)  TextView tradeValueCount;
        @InjectView(R.id.trade_person_cards) TextView tradePersonCards;

        public TradeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onBind(TradeViewModel viewModel) {

            // TODO: Circular Mask Clipping
            Picasso.with(tradeImage.getContext())
                    .load(viewModel.getImagePath())
                    .into(tradeImage);

            tradeValueCount.setText(String.format("%s with %d items traded", viewModel.getFormattedValue(), viewModel.getLineItemCount()));

            SpannableStringBuilder tradePersonCardsBuilder = new SpannableStringBuilder()
                    .append(String.format("with %s - ", viewModel.getWith()));
            int start = tradePersonCardsBuilder.length();
            int end = start + viewModel.getCardsTraded().length();
            tradePersonCardsBuilder.append(viewModel.getCardsTraded());
            tradePersonCardsBuilder.setSpan(new ForegroundColorSpan(Color.LTGRAY), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tradePersonCards.setText(tradePersonCardsBuilder);

        }
    }

}
