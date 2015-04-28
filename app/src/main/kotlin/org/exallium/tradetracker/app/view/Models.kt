package org.exallium.tradetracker.app.view.models

import android.net.Uri
import org.exallium.tradetracker.app.view.models.ViewModel
import org.joda.time.LocalDate

public abstract class ViewModel()
data class PersonViewModel(val name: String) : ViewModel()
data class CardSetViewModel(val code: String, val name: String) : ViewModel()
data class LineItemViewModel(val description: String?, val quantity: Long) : ViewModel()
data class CardViewModel(val name: String, val code: String) : ViewModel()
data class TradeViewModel(val id: Long,
                          val formattedValue: String,
                          val with: String?,
                          val imagePath: Uri?,
                          val lineItemCount: Int,
                          val cardsTraded: String,
                          val date: LocalDate) : ViewModel()