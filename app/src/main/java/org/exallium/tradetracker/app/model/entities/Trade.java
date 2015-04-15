package org.exallium.tradetracker.app.model.entities;

import java.util.Date;

public class Trade extends Record<Trade> {
    public Person person;
    public Date tradeDate;

}
