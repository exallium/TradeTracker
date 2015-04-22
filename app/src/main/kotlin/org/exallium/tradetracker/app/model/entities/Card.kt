package org.exallium.tradetracker.app.model.entities

import org.joda.time.LocalDate
import java.util.Date

public class Card : Record<Card>() {
    public var uuid: String = ""
    public var cardSet: CardSet? = null
    public var name: String = ""
    public var multiverseId: Int = -1
}
