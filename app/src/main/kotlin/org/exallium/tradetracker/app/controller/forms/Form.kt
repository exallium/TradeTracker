package org.exallium.tradetracker.app.controller.forms

import android.os.Bundle
import android.view.View
import butterknife.ButterKnife
import butterknife.ButterKnifeViewHolder
import com.orm.SugarRecord
import com.orm.query.Condition
import com.orm.query.Select
import org.exallium.tradetracker.app.model.entities.Record

public abstract class Form<E : Record<E>?>(val entityClass : Class<E>) {

    private var entity : E = null

    public fun initialize(id : Long) : Boolean {
        entity = SugarRecord.findById(entityClass, id)
        return initialize(entity)
    }

    public fun initialize(entity : E) : Boolean {
        this.entity = entity
        populateFields(entity)
        return entity != null
    }

    public final fun forceSave() {
        forceSave(null)
    }

    public final fun forceSave(bundle: Bundle?) {
        if (entity == null)
            createEntity()
        populateEntity(entity)
        populateEntityFromBundle(entity, bundle)
        entity.save()
    }

    public final fun save() : Boolean {
        return save(null)
    }

    public final fun save(bundle: Bundle?) : Boolean {
        val isValid = isValid()
        if (isValid)
            forceSave(bundle)
        return isValid
    }

    public fun getEntity() : E { return entity }

    protected fun createEntity() {
        entity = entityClass.newInstance()
    }

    public abstract fun isValid() : Boolean
    protected abstract fun populateFields(entity : E)
    protected abstract fun populateEntity(entity : E)

    protected open fun populateEntityFromBundle(entity: E, bundle: Bundle?) {}

}
