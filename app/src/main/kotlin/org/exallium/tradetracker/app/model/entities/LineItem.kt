package org.exallium.tradetracker.app.model.entities

import org.joda.time.LocalDate
import java.util.Date

public class LineItem : Record<LineItem>() {
    public var description: String? = null
    public var card: Card? = null
    public var value: Long = 0
    public var trade: Trade? = null
    public var lastUpdated: Date = LocalDate.now().toDate()
    public var direction: Boolean = false
}
