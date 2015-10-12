package com.blsoft.rareacare.client.events;

import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public class RegistryChangeEvent extends GwtEvent<RegistryChangeEvent.RegistryChangeHandler> {

	static public interface RegistryChangeHandler extends EventHandler {
		void onRegistryChange(RegistryChangeEvent event);
	}
	
	final static public Type<RegistryChangeHandler> TYPE = new Type<RegistryChangeHandler>();
	private RegistryProxy entity;

	public RegistryChangeEvent(RegistryProxy entity) {
		this.entity = entity;
	}

	public RegistryProxy getEntity() {
		return entity;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RegistryChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RegistryChangeHandler handler) {
		handler.onRegistryChange(this);
	}

}
