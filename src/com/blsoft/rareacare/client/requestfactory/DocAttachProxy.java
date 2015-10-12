package com.blsoft.rareacare.client.requestfactory;

import com.blsoft.rareacare.model.DocAttach;
import com.blsoft.rareacare.server.EntityLocator;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = DocAttach.class, locator = EntityLocator.class)
public interface DocAttachProxy extends BaseEntityProxy{

	public String getName();

	public void setName(String param) ;

	public DocumentProxy getParent();

	public void setParent(DocumentProxy param);

}