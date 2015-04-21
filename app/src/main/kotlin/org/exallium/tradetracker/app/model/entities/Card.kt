package org.exallium.tradetracker.app.model.entities

public class Card : Record<Card>() {
    public var uuid: String = ""
    public var cardSet: CardSet? = null
    public var name: String = ""
    public var multiverseId: Int = -1
}
