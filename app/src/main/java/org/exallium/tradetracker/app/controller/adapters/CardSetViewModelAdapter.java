package org.exallium.tradetracker.app.controller.adapters;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.exallium.tradetracker.app.MainApplication;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.Screen;
import org.exallium.tradetracker.app.view.models.CardSetViewModel;
import rx.Observable;
import rx.Subscriber;

import java.util.Comparator;

public class CardSetViewModelAdapter extends ViewModelAdapter<CardSetViewModel> {

    private Subscriber<Pair<Screen, Bundle>> subscriber = null;
    private final Observable<Pair<Screen, Bundle>> onNavigationClickedObservable = Observable.create(subscriber -> this.subscriber = (Subscriber<Pair<Screen, Bundle>>) subscriber);

    private final static Comparator<CardSetViewModel> comparator = ((lhs, rhs) -> lhs.getName().substring(0, 1).toLowerCase().compareTo(rhs.getName().substring(0, 1).toLowerCase()));

    public CardSetViewModelAdapter(Observable<CardSetViewModel> allObjectsObservable) {
        super(allObjectsObservable, comparator);
        onNavigationClickedObservable.subscribe(MainApplication.fragmentRequestSubject);
    }

    @Override
    protected ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        view.setTextColor(view.getContext().getResources().getColor(R.color.abc_secondary_text_material_light));
        return new HeaderViewHolder(view);
    }

    @Override
    protected ViewHolder onCreateModelViewHolder(ViewGroup parent) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        view.setEllipsize(TextUtils.TruncateAt.END);
        return new ModelViewHolder(view);
    }

    private class HeaderViewHolder extends ViewModelAdapter<CardSetViewModel>.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(CardSetViewModel viewModel) {
            ((TextView) itemView).setText(viewModel.getName().substring(0,1));
        }
    }

    private class ModelViewHolder extends ViewModelAdapter<CardSetViewModel>.ViewHolder implements View.OnClickListener {

        private CardSetViewModel model;

        public ModelViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onBind(CardSetViewModel viewModel) {
            this.model = viewModel;
            ((TextView) itemView).setText(viewModel.getName());
        }

        @Override
        public void onClick(View v) {
            if (subscriber != null) {
                Bundle bundle = new Bundle();
                bundle.putString(ViewModelAdapterFactory.CARD_SET, model.getCode());
                subscriber.onNext(new Pair<>(Screen.CARDS, bundle));
            }
        }
    }
}
