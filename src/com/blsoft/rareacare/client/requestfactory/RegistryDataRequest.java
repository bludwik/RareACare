package com.blsoft.rareacare.client.requestfactory;

import java.util.List;

import com.blsoft.rareacare.server.BaseServiceLocator;
import com.blsoft.rareacare.server.reqservices.RegistryService;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(value = RegistryService.class, locator = BaseServiceLocator.class)
public interface RegistryDataRequest extends RequestContext {
	
	//Registry
	public Request<RegistryProxy> findRegistry(Integer id);
	public Request<RegistryProxy> findRegistryWithUsers(Integer id);		
	public Request<List<RegistryProxy>> getRegs(Integer from, Integer count, String patern);
	public Request<Integer> getCount(String patern);
	public Request<RegistryProxy> persistAndLoad(RegistryProxy e, Boolean requery);
	public Request<Void> persist(RegistryProxy p);
	public Request<Void> remove(RegistryProxy p);
	
	//RegUserProxy
	public Request<List<RegUserProxy>> getRegUsers(Integer regId);			
	public Request<RegUserProxy> persistAndLoad(RegUserProxy e);
	public Request<Void> persist(RegUserProxy p);
	public Request<Void> remove(RegUserProxy p);
	
	//DDocElem
	public Request<DDocElemProxy> persistAndLoad(DDocElemProxy p);
	public Request<Void> remove(DDocElemProxy p);
	public Request<List<DDocElemProxy>> getDDocElems(String pattern);
	public Request<Void> dDocElemAdd(String elem);			
}
