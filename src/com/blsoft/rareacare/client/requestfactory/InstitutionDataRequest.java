package com.blsoft.rareacare.client.requestfactory;

import java.util.List;

import com.blsoft.rareacare.server.BaseServiceLocator;
import com.blsoft.rareacare.server.reqservices.InstitutionService;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(value = InstitutionService.class, locator=BaseServiceLocator.class)
public interface InstitutionDataRequest extends RequestContext {
	public Request<Integer> getCount();
	public Request<InstitutionProxy> findInstitution(Integer id);
	public Request<List<InstitutionProxy>> getInstitutions(String patern, Integer start, Integer count);
	
	Request<InstitutionProxy> persistAndLoad(InstitutionProxy p, Boolean requery);
	
	Request<Void> persist(InstitutionProxy p);
	Request<Void> remove(InstitutionProxy p);	

}
