package com.blsoft.rareacare.client.requestfactory;

import com.blsoft.rareacare.model.RegUser;
import com.blsoft.rareacare.server.EntityLocator;
import com.blsoft.rareacare.shared.RegRights;
import com.google.web.bindery.requestfactory.shared.ProxyFor;


@ProxyFor(value = RegUser.class, locator = EntityLocator.class)
public interface RegUserProxy extends BaseEntityProxy{

	UserProxy getUser();

	void setUser(UserProxy param);

	RegistryProxy getRegistry();

	void setRegistry(RegistryProxy param);

	RegRights getCanRead();

	void setCanRead(RegRights param);

	boolean getCanDefine();

	void setCanDefine(boolean param);

	RegRights getCanEdit();

	void setCanEdit(RegRights param);

	RegRights getCanCorrect();

	void setCanCorrect(RegRights param);

}