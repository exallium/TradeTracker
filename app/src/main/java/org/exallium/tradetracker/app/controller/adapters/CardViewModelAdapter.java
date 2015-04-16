package org.exallium.tradetracker.app.controller.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.view.models.CardSetViewModel;
import org.exallium.tradetracker.app.view.models.CardViewModel;
import rx.Observable;

import java.util.Comparator;

public class CardViewModelAdapter extends ViewModelAdapter<CardViewModel> {

    private final static Comparator<CardViewModel> comparator = ((lhs, rhs) -> lhs.getName().substring(0, 1).compareTo(rhs.getName().substring(0, 1)));

    public CardViewModelAdapter(Observable<CardViewModel> allObjectsObservable) {
        super(allObjectsObservable, comparator);
    }

    @Override
    protected ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        view.setTextColor(view.getContext().getResources().getColor(R.color.abc_secondary_text_material_light));
        return new HeaderViewHolder(view);
    }

    @Override
    protected ViewHolder onCreateModelViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new ModelViewHolder(view);
    }

    private class HeaderViewHolder extends ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(CardViewModel viewModel) {
            ((TextView) itemView).setText(viewModel.getName().substring(0,1));
        }
    }

    private class ModelViewHolder extends ViewHolder {

        public ModelViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(CardViewModel viewModel) {
            ((TextView) itemView).setText(viewModel.getName());
        }
    }
}
