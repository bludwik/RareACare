package com.blsoft.rareacare.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.blsoft.rareacare.client.requestfactory.AppRequestFactory;
import com.blsoft.rareacare.client.ui.interfaces.IGoodbyeView;
import com.blsoft.rareacare.client.ui.interfaces.IHelloView;
import com.blsoft.rareacare.client.ui.interfaces.IInstEditor;
import com.blsoft.rareacare.client.ui.interfaces.ILoginView;
import com.blsoft.rareacare.client.ui.interfaces.IMainView;
import com.blsoft.rareacare.client.ui.interfaces.IRegDesigner;
import com.blsoft.rareacare.client.ui.interfaces.IRegDesignerProps;
import com.blsoft.rareacare.client.ui.interfaces.IRegEditor;
import com.blsoft.rareacare.client.ui.interfaces.IRegPtsList;
import com.blsoft.rareacare.client.ui.interfaces.IRegUserPropEditor;
import com.blsoft.rareacare.client.ui.interfaces.IRegUsersEditor;
import com.blsoft.rareacare.client.ui.interfaces.IRegsView;
import com.blsoft.rareacare.client.ui.interfaces.IUserEditor;
import com.blsoft.rareacare.client.ui.interfaces.IUserView;

public interface IClientFactory {
	EventBus getEventBus();

	PlaceController getPlaceController();

	public void goTo(Place place);

	AppRequestFactory getRequestFactory();

	void showError(String msg) throws RuntimeException;

	boolean confirm(String msg);

	void showMessage(String msg);

	IHelloView getHelloView();

	IGoodbyeView getGoodbyeView();

	ILoginView getLoginView();

	IRegsView getRegsView();

	IMainView getMainView();

	IUserView getUsersView();

	IUserEditor getUserEditor();

	IInstEditor getInstEditor();

	IRegDesigner getRegDesigner();

	IRegDesignerProps getRegDesignerProps();

	IRegUsersEditor getRegUsersEditor();

	IRegUserPropEditor getRegUserPropEditor();
	
	IRegPtsList getRegPtsList(); 

	IRegEditor getRegEditor(); 

	void setMainDisplay(AcceptsOneWidget appWidget);

	AcceptsOneWidget getMainDisplay();

}
