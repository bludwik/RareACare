/**
 * 
 */
package com.blsoft.rareacare.client.requestfactory;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

/**
 * RequestFacotry dla ca�ej aplikacji
 * 
 * @author bartek
 * 
 */
public interface AppRequestFactory extends RequestFactory {

	UserDataRequest userDataRequest();

	InstitutionDataRequest institutionDataRequest();

	RegistryDataRequest registryDataRequest();

	DocumentDataRequest documentDataRequest();
	
	// TODO other application entity type requests go here ...

}
