package org.exallium.tradetracker.app.model.entities;

import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Card {

    @PrimaryKey
    private int id;

    @Index
    private String cardSignature;
}
