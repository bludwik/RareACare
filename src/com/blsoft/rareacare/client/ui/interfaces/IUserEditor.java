package com.blsoft.rareacare.client.ui.interfaces;

import com.blsoft.rareacare.client.activity.IUserActivity;
import com.blsoft.rareacare.client.requestfactory.UserProxy;

public interface IUserEditor extends IInstConsumer{
	void setPresenter(IUserActivity presenter);
	void editUser(UserProxy u);

}
