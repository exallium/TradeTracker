package org.exallium.tradetracker.app.model.rest;

import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

public interface MtgPriceRestService {
    @GET("/cfb/price.json")
    List<String> getPriceInfo(@Query("cardname") String name, @Query("cardset") String set);
}
