package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.place.RegPtsListPlace;
import com.blsoft.rareacare.client.ui.interfaces.IRegPtsList;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class RegPtsListActivity extends CustomActivity implements IRegPtsListActivity {
	private RegPtsListPlace place;

	
	public RegPtsListActivity(RegPtsListPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
		this.place = place;
	}
	
	@Override
	public RegPtsListPlace getPlace() {
		return place;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		Utils.CheckLogIn();
		IRegPtsList iRegPtsList = iClientFactory.getRegPtsList();
		iRegPtsList.setPresenter(this);
		FadeAnimation.changeWidget(iRegPtsList.asWidget());
	}

}
