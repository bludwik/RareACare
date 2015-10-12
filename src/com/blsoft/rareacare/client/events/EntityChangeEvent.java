package com.blsoft.rareacare.client.events;

import com.blsoft.rareacare.client.requestfactory.BaseEntityProxy;
import com.google.gwt.event.shared.GwtEvent;

public class EntityChangeEvent extends GwtEvent<EntityChangeHandler> {

	final static public Type<EntityChangeHandler> TYPE = new Type<EntityChangeHandler>();
	private BaseEntityProxy entity;
	
	public EntityChangeEvent(BaseEntityProxy entity) {
		this.entity = entity;
	}
	
	public BaseEntityProxy getEntity() {
		return entity;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EntityChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EntityChangeHandler handler) {
		handler.onEntityChange(this);
	}

}
