package com.blsoft.rareacare.client.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.place.GoodbyePlace;
import com.blsoft.rareacare.client.ui.interfaces.IGoodbyeView;

public class GoodbyeActivityImpl extends CustomActivity {

	private String name;

	public GoodbyeActivityImpl(GoodbyePlace place, IClientFactory iClientFactory) {
		this.name = place.getGoodbyeName();
		this.iClientFactory = iClientFactory;
	}

	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		IGoodbyeView iGoodbyeView = iClientFactory.getGoodbyeView();
		iGoodbyeView.setName(name);
		FadeAnimation.changeWidget(iGoodbyeView.asWidget());		//containerWidget.setWidget(iGoodbyeView.asWidget());
	}
}