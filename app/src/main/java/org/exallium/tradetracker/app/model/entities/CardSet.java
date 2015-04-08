package org.exallium.tradetracker.app.model.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CardSet extends RealmObject {

    @PrimaryKey
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
