package com.blsoft.rareacare.client.requestfactory;

import com.google.web.bindery.requestfactory.shared.EntityProxy;

public interface BaseEntityProxy extends EntityProxy {
	Integer getId();
	UserProxy getCreator();
	UserProxy getUpdater();
	java.util.Date getCreateTime();
	java.util.Date getUpdateTime();
}
