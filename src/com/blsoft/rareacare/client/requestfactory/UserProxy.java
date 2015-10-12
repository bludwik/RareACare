package com.blsoft.rareacare.client.requestfactory;


import com.blsoft.rareacare.model.User;
import com.blsoft.rareacare.server.EntityLocator;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = User.class, locator = EntityLocator.class)
public interface UserProxy extends BaseEntityProxy  {

	public String getName();

	public void setName(String param);

	public String getFirstName();

	public void setFirstName(String param);

	public String getLogin();

	public void setLogin(String param);

	public String getTelephone();

	public void setTelephone(String param);

	public String getEmail();
	
	public void setEmail(String param);
	
	public Boolean getIsSuperuser();

	public void setIsSuperuser(Boolean param) ;
	
	public String getComment();
	
	public void setComment(String param);
	
	public InstitutionProxy getInstitution();

	public void setInstitution(InstitutionProxy param);
}