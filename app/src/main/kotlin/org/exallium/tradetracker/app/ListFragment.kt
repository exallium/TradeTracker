package org.exallium.tradetracker.app

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.exallium.tradetracker.app.controller.adapters.ViewModelAdapter

public class ListFragment : Fragment() {

    private var linearLayoutManager: LinearLayoutManager? = null
    private var viewModelAdapter: ViewModelAdapter<out ViewModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_list, container, false) as RecyclerView
        linearLayoutManager = LinearLayoutManager(this.getActivity())
        recyclerView.setLayoutManager(linearLayoutManager)
        val screen = Screen.getById(getArguments().getInt(BundleConstants.SCREEN_ID))
        viewModelAdapter = ViewModelAdapter.create(screen, getArguments().getBundle(BundleConstants.BUNDLE_ID))
        recyclerView.setAdapter(viewModelAdapter)
        return recyclerView
    }

    override fun onResume() {
        super.onResume()
        viewModelAdapter!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModelAdapter!!.onPause()
    }

    companion object {

        public fun createInstance(screen: Screen, bundle: Bundle?): ListFragment {
            val arguments = Bundle()
            val fragment = ListFragment()
            arguments.putInt(BundleConstants.SCREEN_ID, screen.getId())
            arguments.putBundle(BundleConstants.BUNDLE_ID, bundle)
            fragment.setArguments(arguments)
            return fragment
        }
    }


}