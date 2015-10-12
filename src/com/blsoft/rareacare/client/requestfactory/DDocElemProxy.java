package com.blsoft.rareacare.client.requestfactory;


import com.blsoft.rareacare.model.DDocElem;
import com.blsoft.rareacare.server.DDocElemLocator;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = DDocElem.class, locator = DDocElemLocator.class)
public interface DDocElemProxy extends EntityProxy {

	String getId();

	void setId(String param);

	String getLabel();

	void setLabel(String param);

	String getDescription();

	void setDescription(String param);
	
	public String getDef();

	public void setDef(String param);

}