package org.exallium.tradetracker.app.model.rest;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

import java.util.List;

public interface MtgPriceRestService {
    @GET("/cfb/price.json")
    Observable<List<String>> getPriceInfo(@Query("cardname") String name, @Query("cardset") String set);
}
