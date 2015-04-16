package org.exallium.tradetracker.app.controller.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.view.models.LineItemViewModel;
import rx.Observable;

public class LineItemViewModelAdapter extends ViewModelAdapter<LineItemViewModel> {

    public LineItemViewModelAdapter(Observable<LineItemViewModel> allObjectsObservable) {
        super(allObjectsObservable, null);
    }

    // This should never get called
    @Override
    protected ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    protected ViewHolder onCreateModelViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        return new ModelViewHolder(view);
    }

    private class ModelViewHolder extends ViewModelAdapter<LineItemViewModel>.ViewHolder {
        public ModelViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(LineItemViewModel viewModel) {
            ((TextView) itemView).setText(viewModel.getDescription());
        }
    }

}
