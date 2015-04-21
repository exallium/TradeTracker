package org.exallium.tradetracker.app.controller

import android.os.Bundle
import flow.Backstack
import flow.Flow

public object FlowController : Flow.Listener {

    override fun go(nextBackstack: Backstack?, direction: Flow.Direction?, callback: Flow.Callback?) {
        val nextFragment = nextBackstack?.current()?.getScreen() as? Pair<Screen, Bundle?>
        if (nextFragment != null) {
            MainApplication.fragmentRequestedSubject.onNext(nextFragment)
        }
        callback?.onComplete()
    }

    val backstack: Backstack = Backstack.single(Pair<Screen, Bundle?>(Screen.TRADES, null))
    val flow = Flow(backstack, this)

    public fun getAppFlow(): Flow {
        return flow
    }
}
