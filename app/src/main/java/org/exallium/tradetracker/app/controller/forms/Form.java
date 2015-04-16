package org.exallium.tradetracker.app.controller.forms;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import butterknife.ButterKnife;
import com.orm.query.Condition;
import com.orm.query.Select;
import org.exallium.tradetracker.app.model.entities.Record;

/**
 * A form is owned by a fragment, and thus will act directly on the fragment's behalf wrt to the
 * Model layer.  A form reads data from UI elements and validates them against requirements for
 * a particular entity.
 * @param <E> The Entity we are creating via this form.
 */
public abstract class Form<E extends Record<E>> {

    private static final String TAG = Form.class.getSimpleName();

    private final Class<E> entityClass;
    private E entity;

    public Form(Class<E> entityClass, View formView) {
        this.entityClass = entityClass;
        ButterKnife.inject(this, formView);
    }

    public boolean initialize(long id) {
        entity = Select.from(entityClass).where(Condition.prop("id").eq(id)).first();
        populateFields(entity);
        return entity != null;
    }

    public boolean initialize(E entity) {
        this.entity = entity;
        populateFields(entity);
        return true;
    }

    public final void save() {
        if (isValid()) {
            entity = populateEntity(entity);
            entity.save();
        }
    }

    public E getEntity() {
        return entity;
    }

    protected E createEntity() {
        try {
            return entityClass.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "Something went horribly Wrong", e);
        }
        return null;
    }

    public abstract boolean isValid();
    protected abstract E populateEntity(@Nullable E entity);
    protected abstract void populateFields(@Nullable E entity);

}
