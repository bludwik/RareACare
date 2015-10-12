package com.blsoft.rareacare.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.blsoft.rareacare.client.ClientFactory.ClientException;
import com.blsoft.rareacare.client.mvp.AppActivityMapper;
import com.blsoft.rareacare.client.mvp.AppPlaceHistoryMapper;
import com.blsoft.rareacare.client.place.HelloPlace;


/** Entry point classes define <code>onModuleLoad()</code>. */
public class RareACare implements EntryPoint {
	private Place defaultPlace = new HelloPlace();
	private SimplePanel appWidget = new SimplePanel();

	/** This is the entry point method. */
	@SuppressWarnings("deprecation")
	public void onModuleLoad() {

		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable throwable) {

				if (throwable.getCause() instanceof ClientException)
					return;
				
				String text = "BŁĄD: ";
				while (throwable != null) {
//					StackTraceElement[] stackTraceElements = throwable.getStackTrace();
					text += throwable.toString() + "\n";
//					for (int i = 0; i < stackTraceElements.length; i++) {
//						text += "    at " + stackTraceElements[i] + "\n";
//					}
					throwable = throwable.getCause();
					if (throwable != null) {
						text += "Wywołany przez: ";
					}
				}
				DialogBox dialogBox = new DialogBox(true, false);
				DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#ABCDEF");
				System.err.print(text);
				text = text.replaceAll(" ", "&nbsp;");
				dialogBox.setHTML("<pre>" + text + "</pre>");
				dialogBox.center();
				
			}
		});

		// Create IClientFactory using deferred binding so we can replace with
		// different
		// impls in gwt.xml
		IClientFactory cf = GWT.create(IClientFactory.class);
		Utils.setCF(cf);
		EventBus eventBus = cf.getEventBus();
		PlaceController placeController = cf.getPlaceController();

		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(cf);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, defaultPlace);

		RootLayoutPanel.get().add(appWidget);
//		RootPanel.get().add(appWidget);
//		RootPanel.get().setSize(Window.getClientWidth()+"px",
//				Window.getClientHeight()+"px");

//		RootPanel.get().setSize("100%", "100%");
		cf.setMainDisplay(appWidget);
		// Goes to place represented on URL or default place
		historyHandler.handleCurrentHistory();
		
	}

}
