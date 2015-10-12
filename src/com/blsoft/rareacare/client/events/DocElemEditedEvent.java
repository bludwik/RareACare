package com.blsoft.rareacare.client.events;

import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.google.gwt.event.shared.GwtEvent;

public class DocElemEditedEvent  extends GwtEvent<DocElemEditedHandler> {

	final static public Type<DocElemEditedHandler> TYPE = new Type<DocElemEditedHandler>();
	private RegistryProxy.Elem newObj;
	private RegistryProxy.Elem orgObj;
	
	public DocElemEditedEvent(RegistryProxy.Elem newObj, RegistryProxy.Elem orgObj) {
		this.newObj= newObj;
		this.orgObj= orgObj;
	}
	
	public RegistryProxy.Elem getNewObj() {
		return newObj;
	}
	public RegistryProxy.Elem getOrgObj() {
		return orgObj;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DocElemEditedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DocElemEditedHandler handler) {
		handler.onDocElemEdited(this);
	}

}
