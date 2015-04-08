package org.exallium.tradetracker.app.model.rest;

import android.content.Context;
import io.realm.Realm;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.CardSet;
import retrofit.RestAdapter;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class RestServiceManager {

    private Context context;

    private MtgApiRestService mtgApiRestService;
    private MtgPriceRestService mtgPriceRestService;

    public RestServiceManager(Context context) {
        this.context = context;

        RestAdapter r1 = new RestAdapter.Builder()
                .setEndpoint("http://api.mtgapi.com/v2/")
                .build();
        mtgApiRestService = r1.create(MtgApiRestService.class);

        RestAdapter r2 = new RestAdapter.Builder()
                .setEndpoint("http://magictcgprices.appspot.com/api/")
                .build();
        mtgPriceRestService = r2.create(MtgPriceRestService.class);
    }

    public Observable<CardSet> getCardSetObservable() {
        return mtgApiRestService.getSets().map(m -> m.get("sets")).map(s -> {

            Map<String, String> setInfo = (Map<String, String>) s;
            Realm realm = Realm.getInstance(context);
            CardSet set = realm.allObjects(CardSet.class).where().equalTo("code", setInfo.get("code")).findFirst();

            if (set == null) {
                set = realm.createObject(CardSet.class);
                set.setCode(setInfo.get("code"));
            }

            return set;
        });
    }

    public Observable<List<Card>> getCardsForSetObservable() {
        return null;
    }

}
