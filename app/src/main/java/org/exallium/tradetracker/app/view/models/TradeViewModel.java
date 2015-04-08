package org.exallium.tradetracker.app.view.models;

import android.net.Uri;

import java.util.UUID;

public class TradeViewModel {
    private UUID id;
    private String formattedValue;
    private String with;
    private Uri imagePath;
    private int lineItemCount;
    private String cardsTraded;

    public TradeViewModel(UUID id, String formattedValue, String with, Uri imagePath, int lineItemCount, String cardsTraded) {
        this.id = id;
        this.formattedValue = formattedValue;
        this.with = with;
        this.imagePath = imagePath;
        this.lineItemCount = lineItemCount;
        this.cardsTraded = cardsTraded;
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
}
