package org.exallium.tradetracker.app.controller.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ViewModelAdapter<VM> extends RecyclerView.Adapter<ViewModelAdapter<VM>.ViewHolder> {

    private static final String TAG = ViewModelAdapter.class.getSimpleName();

    private final Observable<VM> observable;
    private final List<VM> viewModels = Collections.synchronizedList(new ArrayList<>());

    private final Subscriber<VM> vmSubscriber = new Subscriber<VM>() {

        @Override
        public void onCompleted() {
            Log.d(TAG, "Subscriber onComplete called");
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "Something bad happened", e);
        }

        @Override
        public void onNext(VM vm) {
            viewModels.add(vm);
            notifyDataSetChanged();
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(viewModels.get(position));
    }

    public ViewModelAdapter(Observable<VM> observable) {
        this.observable = observable;
    }

    public void subscribe() {
        observable.subscribe(vmSubscriber);
    }

    public void unsubscribe() {
        vmSubscriber.unsubscribe();
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void onBind(VM viewModel);
    }

}
