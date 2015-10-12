package com.blsoft.rareacare.client.activity;

import com.blsoft.rareacare.client.IClientFactory;
import com.blsoft.rareacare.client.activity.UserActivity.IUserConsumer;
import com.blsoft.rareacare.client.requestfactory.UserProxy;
import com.google.gwt.view.client.Range;


public interface IUserActivity extends IPresenter {

	IClientFactory getClientFactory();

	void editUser(UserProxy u);
	void deleteUser(UserProxy user);
	
	void getUserCount(IUserConsumer consumer);
	void getUsers(Range newRange, IUserConsumer consumer);
}
