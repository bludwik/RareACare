package com.blsoft.rareacare.client.ui.interfaces;


import com.blsoft.rareacare.client.activity.IUserActivity;
import com.blsoft.rareacare.client.activity.UserActivity.IUserConsumer;
import com.google.gwt.user.client.ui.IsWidget;

public interface IUserView  extends IsWidget, IUserConsumer {
	
	void setPresenter(IUserActivity presenter);

}
