package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.place.UsersPlace;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.UserDataRequest;
import com.blsoft.rareacare.client.requestfactory.UserProxy;
import com.blsoft.rareacare.client.ui.UserEditor;
import com.blsoft.rareacare.client.ui.interfaces.IUserEditor;
import com.blsoft.rareacare.client.ui.interfaces.IUserView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.Range;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;

import java.util.List;

public class UserActivity extends CustomActivity implements IUserActivity {

	public interface Driver extends RequestFactoryEditorDriver<UserProxy, UserEditor> {
	}
	
	public interface IUserConsumer {
		void consumeUserList(int startRange, List<UserProxy> list);
		void consumeUserCount(int count);
	}

	private IClientFactory iClientFactory;
	private Range oldRange = null;

	// Name that will be appended to "Hello,"

	public UserActivity(UsersPlace place, IClientFactory iClientFactory) {
		this.iClientFactory = iClientFactory;
	}

	@Override
	public void goTo(Place place) {
		iClientFactory.goTo(place);
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		Utils.CheckLogIn();
		IUserView iUserView = iClientFactory.getUsersView();
		iUserView.setPresenter(this);
		FadeAnimation.changeWidget(iUserView.asWidget());
//		panel.setWidget(iUserView.asWidget());
	}

	@Override
	public void getUsers(final Range newRange, final IUserConsumer consumer) {
		if (newRange == null || !newRange.equals(oldRange)) {
			UserDataRequest req = iClientFactory.getRequestFactory().userDataRequest();
			final Range rg = (newRange == null) ? (oldRange == null) ? new Range(0, 25) : oldRange : newRange;
			req.getUsers("", rg.getStart(), rg.getLength()).with("institution").fire(new CommonReceiver<List<UserProxy>>() {
				public void onSuccess(List<UserProxy> u) {
					consumer.consumeUserList(rg.getStart(), u);
				}
			});
		}
	}

	@Override
	public void getUserCount(final IUserConsumer consumer) {
		UserDataRequest req = iClientFactory.getRequestFactory().userDataRequest();
		req.getCount().fire(new CommonReceiver<Integer>() {
			@Override
			public void onSuccess(Integer response) {
				consumer.consumeUserCount(response);
			}
		});
	}

	@Override
	public void editUser(UserProxy u) {
		IUserEditor ue = iClientFactory.getUserEditor();
		ue.setPresenter(this);
		ue.editUser(u);
	}

	@Override
	public void deleteUser(UserProxy user) {
		UserDataRequest req = iClientFactory.getRequestFactory().userDataRequest();
		req.remove(user).fire(new CommonReceiver<Void>() {
			@Override
			public void onSuccess(Void response) {
				getUserCount(iClientFactory.getUsersView());
				getUsers(null, iClientFactory.getUsersView());
			}
		});
	}

	@Override
	public IClientFactory getClientFactory() {
		return iClientFactory;
	}

}