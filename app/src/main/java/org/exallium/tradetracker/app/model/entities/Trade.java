package org.exallium.tradetracker.app.model.entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

import java.util.Date;
import java.util.UUID;

@RealmClass
public class Trade extends RealmObject {

    @PrimaryKey
    private String uid;

    private RealmList<LineItem> lineItems;
    private Person person;
    private Date tradeDate;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
