package org.exallium.tradetracker.app.controller.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import io.realm.Realm;
import org.exallium.tradetracker.app.MainApplication;
import org.exallium.tradetracker.app.model.RealmManager;
import org.exallium.tradetracker.app.view.models.ViewModel;
import rx.Observable;
import rx.Subscriber;

import java.util.*;

public abstract class ViewModelAdapter<VM extends ViewModel> extends RecyclerView.Adapter<ViewModelAdapter<VM>.ViewHolder> {

    private static final String TAG = ViewModelAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_MODEL = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    private final Observable<VM> allObjectsObservable;
    private final Comparator<VM> headerComparator;

    private final List<VM> viewModels = Collections.synchronizedList(new ArrayList<>());
    private final Set<Integer> headerPositions = Collections.synchronizedSet(new TreeSet<>());
    private Realm realm;

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

            VM prev = viewModels.size() == 0 ? null : viewModels.get(viewModels.size() - 1);

            if (prev == null || headerComparator.compare(prev, vm) != 0) {
                headerPositions.add(viewModels.size());
                viewModels.add(vm);
            }
            viewModels.add(vm);

            notifyDataSetChanged();
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(viewModels.get(position));
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return onCreateHeaderViewHolder(parent);
            case VIEW_TYPE_MODEL:
                return onCreateModelViewHolder(parent);
        }

        return null;
    }

    protected abstract ViewHolder onCreateHeaderViewHolder(ViewGroup parent);
    protected abstract ViewHolder onCreateModelViewHolder(ViewGroup parent);

    public ViewModelAdapter(Observable<VM> allObjectsObservable, Comparator<VM> headerComparator) {
        this.allObjectsObservable = allObjectsObservable;
        this.headerComparator = headerComparator;
    }

    public void onResume() {
        subscribe();
        realm = RealmManager.INSTANCE.getRealm();
        realm.addChangeListener(this::subscribe);
    }

    public void onPause() {
        unsubscribe();
        realm.removeChangeListener(this::subscribe);
    }

    private void subscribe() {
        viewModels.clear();
        headerPositions.clear();
        notifyDataSetChanged();
        allObjectsObservable.subscribe(vmSubscriber);
    }

    private void unsubscribe() {
        vmSubscriber.unsubscribe();
    }

    @Override
    public int getItemViewType(int position) {
        return headerPositions.contains(position) ? VIEW_TYPE_HEADER : VIEW_TYPE_MODEL;
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
