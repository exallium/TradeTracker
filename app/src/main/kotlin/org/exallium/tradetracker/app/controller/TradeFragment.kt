package org.exallium.tradetracker.app.controller

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import butterknife.ButterKnife
import butterknife.bindView
import com.orm.SugarRecord
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.controller.BundleConstants
import org.exallium.tradetracker.app.R
import org.exallium.tradetracker.app.controller.adapters.ViewModelAdapter
import org.exallium.tradetracker.app.controller.dialogs.DialogScreen
import org.exallium.tradetracker.app.controller.dialogs.createDialogFragment
import org.exallium.tradetracker.app.controller.forms.TradeForm
import org.exallium.tradetracker.app.model.entities.Record
import org.exallium.tradetracker.app.model.entities.Trade
import org.exallium.tradetracker.app.view.widgets.SlidingTabLayout
import org.joda.time.LocalDate
import rx.subjects.PublishSubject
import rx.subjects.Subject

public class TradeFragment : Fragment() {

    val viewPager: ViewPager by bindView(R.id.trade_pager)
    val slidingTabLayout: SlidingTabLayout by bindView(R.id.trade_tabs)

    private var tradeForm: TradeForm? = null
    private val trade: Trade = SugarRecord.findById(javaClass<Trade>(), getArguments().getLong(BundleConstants.TRADE_ID))

    var fab: ImageButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trade, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.inject(this, view)

        viewPager.setAdapter(TradePagerAdapter())
        slidingTabLayout.setViewPager(viewPager)

        slidingTabLayout.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Do nothing
            }

            override fun onPageSelected(position: Int) {
                fab!!.setVisibility(if (position == 0) View.GONE else View.VISIBLE)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Do nothing
            }
        })

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        fab = activity?.findViewById(R.id.fab) as ImageButton?
        fab!!.setOnClickListener({
            if (viewPager.getCurrentItem() != 0) {
                var bundle = Bundle();
                bundle.putLong(BundleConstants.TRADE_ID, getArguments().getLong(BundleConstants.TRADE_ID));
                bundle.putBoolean(BundleConstants.LINE_ITEM_DIRECTION, viewPager.getCurrentItem() == 1);
                bundle.putInt(BundleConstants.SCREEN_ID, DialogScreen.LINE_ITEM_TYPE.id);
                createDialogFragment(bundle).show(getChildFragmentManager(), "lineItemDialog");
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onPause() {
        super.onPause()
        tradeForm?.save(trade)
        if (tradeForm?.isFormValid()?:false)
            trade.isTemporary = false
        trade.save()
    }


    private inner class TradePagerAdapter : PagerAdapter() {

        private val tabTitles = intArray(R.string.trade_tab_details, R.string.trade_tab_from, R.string.trade_tab_to)

        override fun getCount(): Int {
            return tabTitles.size()
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getString(tabTitles[position])
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return `object` == view
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view: View
            val tradeId = getArguments().getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT)
            val trade = SugarRecord.findById(javaClass<Trade>(), tradeId)
            when (tabTitles[position]) {
                R.string.trade_tab_details -> {
                    tradeForm = TradeForm(container.getContext())
                    view = tradeForm?.getFormViewGroup() as View
                    container.addView(view)
                }
                else -> {
                    view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_list, container, false)
                    val rv = view as RecyclerView
                    val direction = tabTitles[position] == R.string.trade_tab_from
                    rv.setLayoutManager(LinearLayoutManager(getActivity()))
                    val bundle = Bundle()
                    bundle.putBoolean(BundleConstants.LINE_ITEM_DIRECTION, direction)
                    bundle.putLong(BundleConstants.TRADE_ID, tradeId)
                    val adapter = ViewModelAdapter.create(Screen.TRADE, bundle)
                    rv.setAdapter(adapter)
                    container.addView(view)
                    adapter.onResume()
                }
            }

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            if (`object` is RecyclerView) {
                (`object`.getAdapter() as ViewModelAdapter<*>).onPause();
            } else {
                tradeForm?.save(trade)
                trade.save()
            }

            container.removeView(`object` as View)
        }
    }

    companion object {

        private val TAG = javaClass<TradeFragment>().getSimpleName()

        public fun createInstance(bundle: Bundle?): TradeFragment {
            val fragment = TradeFragment()
            val args = Bundle()
            var tradeId = bundle?.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT)?:BundleConstants.NEW_OBJECT;
            if (tradeId == BundleConstants.NEW_OBJECT) {
                val trade = Trade();
                trade.tradeDate = LocalDate.now().toDate()
                trade.isTemporary = true
                trade.save()
                tradeId = trade.getId()
            }

            args.putLong(BundleConstants.TRADE_ID, tradeId)
            fragment.setArguments(args)
            return fragment
        }
    }
}