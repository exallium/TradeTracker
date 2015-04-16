package org.exallium.tradetracker.app.view.models;

public class LineItemViewModel extends ViewModel {

    private final String description;

    public LineItemViewModel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
