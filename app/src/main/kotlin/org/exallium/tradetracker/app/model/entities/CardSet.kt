package org.exallium.tradetracker.app.model.entities

public class CardSet : Record<CardSet>() {
    public var code: String = ""
    public var name: String = ""
    public var count: Int = 0
}
