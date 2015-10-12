package com.blsoft.rareacare.client.requestfactory;

import com.blsoft.rareacare.model.Fact;
import com.blsoft.rareacare.server.EntityLocator;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = Fact.class, locator = EntityLocator.class)
public interface FactProxy extends BaseEntityProxy {

	String getPath();

	void setPath(String param);

	String getContent();

	void setContent(String param);

	DocumentProxy getDocument();

	void setDocument(DocumentProxy param);
	
	Integer getLp();

	void setLp(Integer param);

}