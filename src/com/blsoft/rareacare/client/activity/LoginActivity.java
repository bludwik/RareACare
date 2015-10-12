package com.blsoft.rareacare.client.activity;


import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.place.LoginPlace;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.UserDataRequest;
import com.blsoft.rareacare.client.requestfactory.UserProxy;
import com.blsoft.rareacare.client.ui.interfaces.ILoginView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class LoginActivity extends CustomActivity implements ILoginActivity {

	public LoginActivity(LoginPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		ILoginView iLoginView = iClientFactory.getLoginView();
		iLoginView.setPresenter(this);
		iLoginView.initView();
		Utils.setUser(null);
		FadeAnimation.changeWidget(iLoginView.asWidget());
//		containerWidget.setWidget(iLoginView.asWidget());
	}


	@Override
	public void login(String userTxt, String passwordTxt) {
		UserDataRequest req = iClientFactory.getRequestFactory().userDataRequest();		
		req.login(userTxt, Utils.getSHA1for(passwordTxt)).fire(new CommonReceiver<UserProxy>() {
//		req.login(userTxt, passwordTxt).fire(new CommonReceiver<UserProxy>() {
			public void onSuccess(UserProxy u) {
				Utils.setUser(u);
				Window.alert("Zalogowano użytkownka: " + u.getFirstName() + " " + u.getName());
				goTo(null);  // Przekieruje do strony wymaganej przed logoaniem, lub glownej
			}

			public void onFailure(ServerFailure error) {
				Window.alert(error.getMessage());
			}
		});
		return;
	}

	public void logout() {
		Utils.logout(true);
	}
}
