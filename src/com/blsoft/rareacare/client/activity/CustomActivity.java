package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.IClientFactory;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.place.shared.Place;

public abstract class CustomActivity extends AbstractActivity implements IPresenter {

	protected IClientFactory iClientFactory;
	
		@Override
	public void goTo(Place place) {
		assert iClientFactory != null;
		iClientFactory.goTo(place);
	}

	@Override
	public IClientFactory getClientFactory() {
		return iClientFactory;
	}


}
