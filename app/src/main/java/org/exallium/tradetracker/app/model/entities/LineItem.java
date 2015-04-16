package org.exallium.tradetracker.app.model.entities;

import java.util.Date;

public class LineItem extends Record<LineItem> {
    public String description;
    public Card card;
    public long value;
    public Trade trade;
    public Date lastUpdated;
    public boolean direction;
}
