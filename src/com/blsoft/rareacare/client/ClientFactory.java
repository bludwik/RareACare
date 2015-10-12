package com.blsoft.rareacare.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.blsoft.rareacare.client.place.HelloPlace;
import com.blsoft.rareacare.client.place.LoginPlace;
import com.blsoft.rareacare.client.place.MainPlace;
import com.blsoft.rareacare.client.place.SecuredPlace;
import com.blsoft.rareacare.client.requestfactory.AppRequestFactory;
import com.blsoft.rareacare.client.ui.GoodbyeView;
import com.blsoft.rareacare.client.ui.HelloView;
import com.blsoft.rareacare.client.ui.InstEditor;
import com.blsoft.rareacare.client.ui.LoginView;
import com.blsoft.rareacare.client.ui.MainView;
import com.blsoft.rareacare.client.ui.RegDesigner;
import com.blsoft.rareacare.client.ui.RegDesignerProps;
import com.blsoft.rareacare.client.ui.RegEditor;
import com.blsoft.rareacare.client.ui.RegPtsList;
import com.blsoft.rareacare.client.ui.RegUserPropEditor;
import com.blsoft.rareacare.client.ui.RegUsersEditor;
import com.blsoft.rareacare.client.ui.RegsView;
import com.blsoft.rareacare.client.ui.UserEditor;
import com.blsoft.rareacare.client.ui.UserView;
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


public class ClientFactory implements IClientFactory {
	private static final EventBus eventBus = new SimpleEventBus();
	@SuppressWarnings("deprecation")
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final AppRequestFactory requestFactory = GWT.create(AppRequestFactory.class);

	private static IHelloView iHelloView = new HelloView();
	private static IGoodbyeView iGoodbyeView = new GoodbyeView();
	private static ILoginView iLoginView = null;
	private static IRegsView iRegsView;
	private static IMainView iMainView;
	private static IUserView iUserView;
	private static IUserEditor iUserEditor;
	private static IInstEditor iInstEditor;
	private static IRegDesigner iRegDesigner;  
	private static IRegDesignerProps iRegDesignerProps;  
	private static IRegUsersEditor iRegUsersEditor;
	private static IRegUserPropEditor iRegUserPropEditor;
	private static IRegPtsList iRegPtsList;
	private static IRegEditor iRegEditor;
	
	private Place placeToGoAfterLogin;
	private AcceptsOneWidget appWidget;

	{
		requestFactory.initialize(eventBus);
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public IHelloView getHelloView() {
		return iHelloView;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public IGoodbyeView getGoodbyeView() {
		return iGoodbyeView;
	}

	@Override
	public ILoginView getLoginView() {
		if (iLoginView == null)
			iLoginView = new LoginView();
		return iLoginView;
	}

	/**
	 * @return the requestfactory
	 */
	public AppRequestFactory getRequestFactory() {
		return requestFactory;
	}

	@Override
	public IRegsView getRegsView() {
		if (iRegsView == null)
			iRegsView = new RegsView();
		return iRegsView;
	}

	@Override
	public IMainView getMainView() {
		if (iMainView == null)
			iMainView = new MainView();
		return iMainView;
	}

	@Override
	public IUserView getUsersView() {
		if (iUserView == null)
			iUserView = new UserView();
		return iUserView;
	}

	@Override
	public IRegDesigner getRegDesigner() {
		if (iRegDesigner == null)
			iRegDesigner = new RegDesigner();
		return iRegDesigner;
	}

	@Override
	public IRegDesignerProps getRegDesignerProps() {
		if (iRegDesignerProps == null)
			iRegDesignerProps = new RegDesignerProps();
		return iRegDesignerProps;
	}
	

	/**
	 * Procedura przechodzi  do wskazanego Place; Jeśli nie zalogowano użytkownika, a Place tego wymaga, to najpierw przekierowuje do strony logowania.
	 */
	@Override
	public void goTo(Place place) {
		if (place == null)
			place = placeToGoAfterLogin;
		placeToGoAfterLogin = null;
		if (place != null) {
			if (place instanceof SecuredPlace && !Utils.isLoggedIn()) {
				placeToGoAfterLogin = place;
				placeController.goTo(new LoginPlace());
			} else
				placeController.goTo(place);
		} else if (!Utils.isLoggedIn())
			placeController.goTo(new HelloPlace());
		else
			placeController.goTo(new MainPlace());
	}

	@Override
	public IUserEditor getUserEditor() {
		if (iUserEditor == null)
			iUserEditor = new UserEditor();
		return iUserEditor;
	}
	

	@SuppressWarnings("serial")
	public class ClientException extends RuntimeException {
		ClientException(String message){
			super(message);
		}
	}
	
	@Override
	public void showError(String msg) throws ClientException {
		Window.alert(msg); // TODO - dodać lepszą obsugę będów - może lgowanie?
		throw new ClientException(msg);
	}


	@Override
	public boolean confirm(String msg) { 
		return Window.confirm(msg); 
	}

	@Override
	public void showMessage(String msg) {
		Window.alert(msg); 
	}

	@Override
	public IInstEditor getInstEditor() {
		if (iInstEditor == null)
			iInstEditor = new InstEditor();
		return iInstEditor;
	}
	
	@Override
	public IRegUsersEditor getRegUsersEditor() {
		if (iRegUsersEditor == null)
			iRegUsersEditor = new RegUsersEditor();
		return iRegUsersEditor;
	}
	
	@Override
	public IRegUserPropEditor getRegUserPropEditor() {
		if (iRegUserPropEditor == null)
			iRegUserPropEditor = new RegUserPropEditor();
		return iRegUserPropEditor;
	}

	@Override
	public IRegPtsList getRegPtsList() {
		if (iRegPtsList == null)
			iRegPtsList = new RegPtsList();
		return iRegPtsList;
	}

	@Override
	public IRegEditor getRegEditor() {
		if (iRegEditor == null)
			iRegEditor = new RegEditor();
		return iRegEditor;
	}	

	
	
	@Override
	public void setMainDisplay(AcceptsOneWidget appWidget) {
		this.appWidget = appWidget;		
	}

	@Override
	public AcceptsOneWidget getMainDisplay() {
		return this.appWidget;
	}

}
