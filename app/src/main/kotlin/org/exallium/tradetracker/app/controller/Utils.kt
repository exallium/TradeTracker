package org.exallium.tradetracker.app.utils

import android.content.res.Resources
import org.exallium.tradetracker.app.R
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

private val dateTimeFormatter = DateTimeFormat.forPattern("MMMMM dd yyyy")

public fun LocalDate.printForDisplay(resources : Resources) : String {
    return when {
        this.isEqual(LocalDate.now()) -> resources.getString(R.string.today)
        this.isEqual(LocalDate.now().minusDays(1)) -> resources.getString(R.string.yesterday)
        else -> dateTimeFormatter.print(this)
    }
}

public fun LocalDate.printForField() : String {
    return dateTimeFormatter.print(this)
}

public fun String.toLocalDate(): LocalDate? {
    return dateTimeFormatter.parseLocalDate(this)
}