package org.exallium.tradetracker.app.model.entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;
import java.util.UUID;

public class Trade extends RealmObject {

    @PrimaryKey
    private UUID id;

    private RealmList<LineItem> lineItems;
    private Person person;
    private Date tradeDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public RealmList<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(RealmList<LineItem> lineItems) {
        this.lineItems = lineItems;
    }
}
