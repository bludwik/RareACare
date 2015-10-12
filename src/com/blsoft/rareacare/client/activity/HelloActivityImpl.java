package com.blsoft.rareacare.client.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.place.HelloPlace;
import com.blsoft.rareacare.client.ui.interfaces.IHelloView;

public class HelloActivityImpl extends CustomActivity implements
		IPresenter {
	// Name that will be appended to "Hello,"

	public HelloActivityImpl(HelloPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		IHelloView iHelloView = iClientFactory.getHelloView();
		iHelloView.setPresenter(this);
		FadeAnimation.changeWidget(iHelloView.asWidget());
//		containerWidget.setWidget(iHelloView.asWidget());
	}

}
