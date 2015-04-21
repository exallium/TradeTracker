package org.exallium.tradetracker.app.model.entities

import org.joda.time.LocalDate
import java.util.Date

public class Trade : Record<Trade>() {
    public var person: Person? = null
    public var tradeDate: Date = LocalDate.now().toDate()
    public var isTemporary: Boolean = false
}
