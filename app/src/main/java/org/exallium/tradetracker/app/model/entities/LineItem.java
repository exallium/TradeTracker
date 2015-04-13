package org.exallium.tradetracker.app.model.entities;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

import java.util.Date;
import java.util.UUID;

@RealmClass
public class LineItem extends RealmObject {

    @PrimaryKey
    private String uid;

    private String description; // Could be null or other info

    private Card card;

    private long value;

    // Keeps value of this line item up to date.
    // We should be updating the database with most recent pricing every few days
    private Date lastUpdated;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
