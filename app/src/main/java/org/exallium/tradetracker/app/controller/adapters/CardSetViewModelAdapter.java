package org.exallium.tradetracker.app.controller.adapters;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.Screen;
import org.exallium.tradetracker.app.view.models.CardSetViewModel;
import rx.Observable;
import rx.Subscriber;

public class CardSetViewModelAdapter extends ViewModelAdapter<CardSetViewModel> {

    private Subscriber<Pair<Screen, Bundle>> subscriber = null;
    private final Observable<Pair<Screen, Bundle>> onNavigationClickedObservable = Observable.create(subscriber -> this.subscriber = (Subscriber<Pair<Screen, Bundle>>) subscriber);

    public CardSetViewModelAdapter(Observable<CardSetViewModel> allObjectsObservable) {
        super(allObjectsObservable, ((lhs, rhs) -> lhs.getCode().substring(0,1).compareTo(rhs.getCode().substring(0,1))));
        onNavigationClickedObservable.subscribe(ViewModelAdapterFactory.adapterSubject);
    }

    @Override
    protected ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    protected ViewHolder onCreateModelViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new ModelViewHolder(view);
    }

    private class HeaderViewHolder extends ViewModelAdapter<CardSetViewModel>.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(CardSetViewModel viewModel) {
            ((TextView) itemView).setText(viewModel.getCode().substring(0,1));
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
            ((TextView) itemView).setText(viewModel.getCode());
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
