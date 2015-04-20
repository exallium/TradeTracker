package org.exallium.tradetracker.app.model.rest

import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

public trait MtgPriceRestService {
    GET("/cfb/price.json")
    public fun getPriceInfo(Query("cardname") name: String, Query("cardset") set: String): Observable<List<String>>
}