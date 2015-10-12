package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.IClientFactory;
import com.google.gwt.place.shared.Place;

public interface IPresenter {

	void goTo(Place place);
	
	IClientFactory getClientFactory();

}
