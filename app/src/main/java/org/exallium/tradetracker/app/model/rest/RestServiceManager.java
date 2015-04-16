package org.exallium.tradetracker.app.model.rest;

import android.content.Context;
import com.orm.query.Condition;
import com.orm.query.Select;
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
            CardSet set = Select.from(CardSet.class).where(Condition.prop("code").eq(setInfo.get("code").toString())).first();

            if (set == null) {
                set = new CardSet();
                set.code = setInfo.get("code").toString();
                set.count = (int) Math.round(((Double) setInfo.get("cardCount")));
                set.name = setInfo.get("name").toString();
            }

            return setCode;
        });
    }

    public Observable<CardSet> getCardsForSetObservable(final CardSet cardSet, final int page) {
        final String setCode = cardSet.code;
        return mtgApiRestService.getCardsForSet(setCode, page).map(m -> m.get("cards")).flatMap(list ->
            Observable.from((List) list)
        ).map(c -> {

            Map<String, Object> cardInfo = (Map<String, Object>) c;

            String name = cardInfo.get("name").toString();
            UUID cardId = UUID.nameUUIDFromBytes(String.format("%s[%s]", name, setCode).getBytes());
            Card card = Select.from(Card.class).where(Condition.prop("uuid").eq(cardId.toString())).first();

            if (card == null) {
                card = new Card();
                card.name = name;
                card.uuid = cardId.toString();
                card.cardSet = cardSet;
            }

            return cardSet;
        });
    }

    public Observable<List<String>> getPriceForCardObservable(Card card) {
        return mtgPriceRestService.getPriceInfo(card.name, card.cardSet.code);
    }

}
