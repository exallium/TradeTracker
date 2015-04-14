package org.exallium.tradetracker.app.view.models;

public class CardViewModel extends ViewModel {

    private final String name;
    private final String setCode;

    public CardViewModel(String name, String setCode) {
        this.setCode = setCode;
        this.name = name;
    }

    public String getSetCode() {
        return setCode;
    }

    public String getName() {
        return name;
    }
}
