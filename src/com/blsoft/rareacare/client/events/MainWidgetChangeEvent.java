package com.blsoft.rareacare.client.events;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class MainWidgetChangeEvent extends GwtEvent<MainWidgetChangeEvent.MainWidgetChangeHandler> {
	static public interface MainWidgetChangeHandler extends EventHandler {
		void onMainWidgetChange(MainWidgetChangeEvent event);
	}
	
	final static public Type<MainWidgetChangeHandler> TYPE = new Type<MainWidgetChangeHandler>();
	private Widget oldWidget;
	private Widget newWidget;

	public MainWidgetChangeEvent(Widget oldWidget, Widget newWidget) {
		this.oldWidget = oldWidget;
		this.newWidget = newWidget;
	}

	public Widget getOldWidget() {
		return oldWidget;
	}
	public Widget getNewWidget() {
		return newWidget;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<MainWidgetChangeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MainWidgetChangeHandler handler) {
		handler.onMainWidgetChange(this);
	}

}
