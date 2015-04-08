package org.exallium.tradetracker.app.model.rest;

import retrofit.http.GET;
import retrofit.http.Query;

import java.util.Map;

public interface MtgApiRestService {

    @GET("/cards")
    Map<String, Object> getCardsForSet(@Query("set") String setCode);

    @GET("/sets")
    Map<String, Object> getSets();
}
