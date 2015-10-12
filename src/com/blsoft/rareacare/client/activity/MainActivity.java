/**
 * 
 */
package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.place.MainPlace;
import com.blsoft.rareacare.client.ui.interfaces.IMainView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * @author bartek
 *
 */
public class MainActivity extends CustomActivity implements IMainActivity {


	public MainActivity(MainPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
	}
	

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Utils.CheckLogIn();
		IMainView iMainView = iClientFactory.getMainView();
		iMainView.setPresenter(this);
		FadeAnimation.changeWidget(iMainView.asWidget());
//		panel.setWidget(iMainView.asWidget());
	}

}
