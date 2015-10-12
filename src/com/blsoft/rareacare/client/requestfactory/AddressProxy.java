package com.blsoft.rareacare.client.requestfactory;

import com.blsoft.rareacare.model.Address;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value = Address.class/*, locator = EntityLocator.class*/)
public interface AddressProxy extends ValueProxy {

	String getStreet();

	void setStreet(String param);

	String getStr_no();

	void setStr_no(String param);

	String getStr_loc();

	void setStr_loc(String param);

	String getCity();

	void setCity(String param);

	String getZipcode();

	void setZipcode(String param);

	String getRegion();

	void setRegion(String param);

	String getCountry();

	void setCountry(String param);

}