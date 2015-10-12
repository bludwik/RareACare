package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.place.RegEditPlace;
import com.blsoft.rareacare.client.ui.interfaces.IRegEditor;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class RegEditActivity extends CustomActivity implements IRegEditActivity {

	
	private RegEditPlace place;

	public RegEditActivity(RegEditPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
		this.place = place;
	}
	
	@Override
	public RegEditPlace getPlace() {
		return place;
	}
	
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Utils.CheckLogIn();
		IRegEditor iRegEditor = iClientFactory.getRegEditor();
		iRegEditor.setPresenter(this);
		FadeAnimation.changeWidget(iRegEditor.asWidget());
	}

}
