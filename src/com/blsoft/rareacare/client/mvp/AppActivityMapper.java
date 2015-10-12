package com.blsoft.rareacare.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.activity.GoodbyeActivityImpl;
import com.blsoft.rareacare.client.activity.HelloActivityImpl;
import com.blsoft.rareacare.client.activity.LoginActivity;
import com.blsoft.rareacare.client.activity.MainActivity;
import com.blsoft.rareacare.client.activity.RegDsgnActivity;
import com.blsoft.rareacare.client.activity.RegEditActivity;
import com.blsoft.rareacare.client.activity.RegPtsListActivity;
import com.blsoft.rareacare.client.activity.RegsActivity;
import com.blsoft.rareacare.client.activity.UserActivity;
import com.blsoft.rareacare.client.place.GoodbyePlace;
import com.blsoft.rareacare.client.place.HelloPlace;
import com.blsoft.rareacare.client.place.LoginPlace;
import com.blsoft.rareacare.client.place.MainPlace;
import com.blsoft.rareacare.client.place.RegDsgnPlace;
import com.blsoft.rareacare.client.place.RegEditPlace;
import com.blsoft.rareacare.client.place.RegPtsListPlace;
import com.blsoft.rareacare.client.place.RegsPlace;
import com.blsoft.rareacare.client.place.UsersPlace;

public class AppActivityMapper implements ActivityMapper {

	private IClientFactory iClientFactory;

	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 * 
	 * @param iClientFactory
	 *            Factory to be passed to activities
	 */
	public AppActivityMapper(IClientFactory iClientFactory) {
		super();
		this.iClientFactory = iClientFactory;
	}

	/**
	 * Map each Place to its corresponding Activity. This would be a great use
	 * for GIN.
	 */
	@Override
	public Activity getActivity(Place place) {
		// This is begging for GIN
		if (place instanceof HelloPlace)
			return new HelloActivityImpl((HelloPlace) place, iClientFactory);
		else if (place instanceof MainPlace)
			return new MainActivity((MainPlace) place, iClientFactory);
		else if (place instanceof UsersPlace)
			return new UserActivity((UsersPlace) place, iClientFactory);
		else if (place instanceof LoginPlace)
			return new LoginActivity((LoginPlace) place, iClientFactory);
		else if (place instanceof GoodbyePlace)
			return new GoodbyeActivityImpl((GoodbyePlace) place, iClientFactory);
		else if (place instanceof RegsPlace)
			return new RegsActivity((RegsPlace) place, iClientFactory);
		else if (place instanceof RegDsgnPlace)
			return new RegDsgnActivity((RegDsgnPlace) place, iClientFactory);
		else if (place instanceof RegEditPlace)
			return new RegEditActivity((RegEditPlace) place, iClientFactory);
		else if (place instanceof RegPtsListPlace)
			return new RegPtsListActivity((RegPtsListPlace) place, iClientFactory);

		return null;
	}

}
