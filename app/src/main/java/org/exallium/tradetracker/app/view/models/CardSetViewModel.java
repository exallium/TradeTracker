package org.exallium.tradetracker.app.view.models;

public class CardSetViewModel extends ViewModel {

    private String code;

    public CardSetViewModel(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
