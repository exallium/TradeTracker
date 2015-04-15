package org.exallium.tradetracker.app.model.rest;

import android.content.Context;
import io.realm.Realm;
import org.exallium.tradetracker.app.R;
import org.exallium.tradetracker.app.model.RealmManager;
import org.exallium.tradetracker.app.model.entities.Card;
import org.exallium.tradetracker.app.model.entities.CardSet;
import retrofit.RestAdapter;
import rx.Observable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public Observable<String> getCardSetObservable() {
        return mtgApiRestService.getSets().map(m -> m.get("sets")).flatMap(list ->
            Observable.from((List) list)
        ).map(s -> {
            Map<String, Object> setInfo = (Map<String, Object>) s;
            String setCode = setInfo.get("code").toString();
            Realm realm = RealmManager.INSTANCE.getRealm();
            CardSet set = realm.allObjects(CardSet.class).where().equalTo("code", setInfo.get("code").toString()).findFirst();

            if (set == null) {
                realm.beginTransaction();
                set = realm.createObject(CardSet.class);
                set.setCode(setInfo.get("code").toString());
                set.setCount((int) Math.round(((Double) setInfo.get("cardCount"))));
                realm.commitTransaction();
            }

            return setCode;
        });
    }

    public Observable<CardSet> getCardsForSetObservable(final CardSet cardSet, final int page) {
        final String setCode = cardSet.getCode();
        return mtgApiRestService.getCardsForSet(setCode, page).map(m -> m.get("cards")).flatMap(list ->
            Observable.from((List) list)
        ).map(c -> {

            Map<String, Object> cardInfo = (Map<String, Object>) c;

            String name = cardInfo.get("name").toString();
            UUID cardId = UUID.nameUUIDFromBytes(String.format("%s[%s]", name, setCode).getBytes());
            Realm realm = RealmManager.INSTANCE.getRealm();
            Card card = realm.allObjects(Card.class).where().equalTo("id", cardId.toString()).findFirst();

            if (card == null) {
                String imageUri = ((Map<String, Object>) cardInfo.get("images")).get("mtgimage").toString();
                realm.beginTransaction();
                card = realm.createObject(Card.class);
                card.setName(name);
                card.setId(cardId.toString());
                card.setImageUri(imageUri);
                card.setCardSet(cardSet);
                realm.commitTransaction();
            }

            return cardSet;
        });
    }

    public Observable<List<String>> getPriceForCardObservable(Card card) {
        return mtgPriceRestService.getPriceInfo(card.getName(), card.getCardSet().getCode());
    }

}
