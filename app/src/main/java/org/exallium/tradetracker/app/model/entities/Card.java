package org.exallium.tradetracker.app.model.entities;

public class Card extends Record<Card> {
    public String uuid;
    public CardSet cardSet;
    public String name;
    public int multiverseId;
    public Card() {}
}
