package org.exallium.tradetracker.app.view.models;

import android.net.Uri;
import org.joda.time.LocalDate;

import java.util.UUID;

public class TradeViewModel extends ViewModel {
    private UUID id;
    private String formattedValue;
    private String with;
    private Uri imagePath;
    private int lineItemCount;
    private String cardsTraded;
    private LocalDate tradeDate;

    public TradeViewModel(UUID id, String formattedValue, String with, Uri imagePath, int lineItemCount, String cardsTraded, LocalDate tradeDate) {
        this.id = id;
        this.formattedValue = formattedValue;
        this.with = with;
        this.imagePath = imagePath;
        this.lineItemCount = lineItemCount;
        this.cardsTraded = cardsTraded;
        this.tradeDate = tradeDate;
    }

    public UUID getId() {
        return id;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public String getWith() {
        return with;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public int getLineItemCount() {
        return lineItemCount;
    }

    public String getCardsTraded() {
        return cardsTraded;
    }

    public LocalDate getDate() {
        return tradeDate;
    }
}
