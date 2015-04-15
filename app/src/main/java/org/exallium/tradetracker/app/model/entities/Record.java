package org.exallium.tradetracker.app.model.entities;

import com.orm.SugarRecord;
import org.exallium.tradetracker.app.MainApplication;

public class Record<T> extends SugarRecord<T> {

    @Override
    public void save() {
        super.save();
        MainApplication.getInstance().onObjectSaved(this);
    }
}
