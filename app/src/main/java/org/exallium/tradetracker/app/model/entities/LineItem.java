package org.exallium.tradetracker.app.model.entities;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

import java.util.Date;
import java.util.UUID;

public class LineItem extends RealmObject {

    @PrimaryKey
    private UUID id;

    @Index
    private String description; // Could be card[id] or other item info.

    private long value;

    // Keeps value of this line item up to date.
    // We should be updating the database with most recent pricing every few days
    private Date lastUpdated;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
