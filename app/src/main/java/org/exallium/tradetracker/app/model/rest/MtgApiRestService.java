package org.exallium.tradetracker.app.model.rest;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

import java.util.Map;

public interface MtgApiRestService {

    @GET("/cards")
    Observable<Map<String, Object>> getCardsForSet(@Query("set") String setCode);

    @GET("/sets")
    Observable<Map<String, Object>> getSets();
}
