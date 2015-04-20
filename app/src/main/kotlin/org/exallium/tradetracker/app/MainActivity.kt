package org.exallium.tradetracker.app

import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageButton
import butterknife.bindView
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.controller.adapters.DrawerNavAdapter
import org.exallium.tradetracker.app.model.entities.Trade
import rx.Subscriber

public class MainActivity : Activity() {

    private var currentScreen = Screen.TRADES

    val navRecyclerView : RecyclerView by bindView(R.id.left_drawer)
    val drawerContainer : DrawerLayout by bindView(R.id.drawer_container)
    val toolbar : Toolbar by bindView(R.id.toolbar)
    val fab : ImageButton by bindView(R.id.fab)

    var fragmentRequestSubscriber : FragmentRequestSubscriber? = null

    private inner class FragmentRequestSubscriber : Subscriber<Pair<Screen, Bundle?>>() {
        override fun onCompleted() {  }
        override fun onError(e: Throwable?) { }

        override fun onNext(t: Pair<Screen, Bundle?>?) {
            showFragment(t?.first, t?.second)
        }

    }

    private fun showFragment(screen : Screen?, bundle : Bundle?) {
        if (screen != currentScreen && screen != null) {
            var transaction = getFragmentManager().beginTransaction()
            when (screen) {
                Screen.TRADE -> transaction.replace(R.id.fragment_container, TradeFragment.createInstance(bundle))
                else -> transaction.replace(R.id.fragment_container, ListFragment.createInstance(screen, bundle))
            }
            transaction.commit()
            currentScreen = screen

            setupFabButton(screen)
            setupToolbar(screen, bundle)
        }
    }

    private fun setupFabButton(screen : Screen) {
        when (screen) {
            Screen.TRADES -> {
                fab.setVisibility(View.VISIBLE)
                fab.setOnClickListener { MainApplication.fragmentRequestedSubject.onNext(Pair<Screen, Bundle?>(Screen.TRADE, null)) }
            }
            else -> fab.setVisibility(View.GONE)
        }
    }

    private fun setupToolbar(screen : Screen, bundle : Bundle?) {
        toolbar.setTitle(screen.getName())
        when (screen) {
            Screen.TRADE -> {
                toolbar.setSubtitle(R.string.trade_subtitle_default)
                if (bundle != null) {
                    val tradeId = bundle.getLong(BundleConstants.TRADE_ID, BundleConstants.NEW_OBJECT)
                    if (tradeId != BundleConstants.NEW_OBJECT) {
                        val trade = Select.from(javaClass<Trade>()).where(Condition.prop("id").eq(tradeId)).first()
                        toolbar.setSubtitle(getString(R.string.trade_with, trade.person.name))
                    }
                }

            }
            else -> toolbar.setSubtitle("")
        }
    }

    public override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentRequestSubscriber = FragmentRequestSubscriber()
        MainApplication.fragmentRequestedSubject.subscribe(fragmentRequestSubscriber)
        navRecyclerView.setLayoutManager(LinearLayoutManager(this))
        navRecyclerView.setAdapter(DrawerNavAdapter(Array(2, { i ->
            if (i == 0) Screen.TRADES else Screen.CARD_SETS
        }), fragmentRequestSubscriber));

    }

    public override fun onDestroy() {
        super.onDestroy()
        fragmentRequestSubscriber?.unsubscribe()
    }
}
