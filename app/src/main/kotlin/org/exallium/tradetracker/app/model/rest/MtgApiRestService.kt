package org.exallium.tradetracker.app.model.rest

import retrofit.http.GET
import retrofit.http.Query
import rx.Observable

public trait MtgApiRestService {

    [GET("/cards")]
    public fun getCardsForSet([Query("set")] setCode: String, [Query("page")] page: Int): Observable<Map<String, Any>>

    [GET("/sets")]
    public fun getSets(): Observable<Map<String, Any>>
}