package org.exallium.tradetracker.app.controller.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import io.realm.Realm;
import org.exallium.tradetracker.app.MainApplication;
import org.exallium.tradetracker.app.view.models.ViewModel;
import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ViewModelAdapter<VM extends ViewModel> extends RecyclerView.Adapter<ViewModelAdapter<VM>.ViewHolder> {

    private static final String TAG = ViewModelAdapter.class.getSimpleName();

    private final Observable<VM> allObjectsObservable;

    private final List<VM> viewModels = Collections.synchronizedList(new ArrayList<>());
    private final Realm realm = Realm.getInstance(MainApplication.getInstance());

    private final Subscriber<VM> vmSubscriber = new Subscriber<VM>() {

        @Override
        public void onCompleted() {
            unsubscribe();
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

    public ViewModelAdapter(Observable<VM> allObjectsObservable) {
        this.allObjectsObservable = allObjectsObservable;
    }

    public void onResume() {
        subscribe();
        realm.addChangeListener(this::subscribe);
    }

    public void onPause() {
        unsubscribe();
        realm.removeChangeListener(this::subscribe);
    }

    private void subscribe() {
        viewModels.clear();
        notifyDataSetChanged();
        allObjectsObservable.subscribe(vmSubscriber);
    }

    private void unsubscribe() {
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
