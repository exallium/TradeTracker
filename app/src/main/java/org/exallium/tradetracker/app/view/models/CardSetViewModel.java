package org.exallium.tradetracker.app.view.models;

public class CardSetViewModel extends ViewModel {

    private String code;
    private String name;

    public CardSetViewModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
