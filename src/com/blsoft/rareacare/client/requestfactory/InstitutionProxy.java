package com.blsoft.rareacare.client.requestfactory;

import java.util.List;

import com.blsoft.rareacare.model.Institution;
import com.blsoft.rareacare.server.EntityLocator;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = Institution.class, locator = EntityLocator.class)
public interface InstitutionProxy extends BaseEntityProxy {

	String getName();

	void setName(String param);

	AddressProxy getAddress();

	void setAddress(AddressProxy param);

	String getTelephone();

	void setTelephone(String param);

	String getEmail();

	void setEmail(String param);

	List<UserProxy> getUsers();

	void setUsers(List<UserProxy> param);

}