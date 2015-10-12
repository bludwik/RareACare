package com.blsoft.rareacare.client;

import com.blsoft.rareacare.client.events.MainWidgetChangeEvent;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;

public class FadeAnimation extends Animation {

//	static private FadeAnimation fadeAnimation = null;
	static private Widget lastWidget = null;
	private Widget currentWidget = null;
	private Widget nextWidget = null;
	private boolean reverted;
	

//	static public FadeAnimation get() {
//		if (fadeAnimation == null)
//			fadeAnimation = new FadeAnimation();
//		return fadeAnimation;
//	}

	public void changeWdg(Widget newWidget) {
		if (lastWidget != null && lastWidget != newWidget) {
			currentWidget =  lastWidget;
			nextWidget = newWidget;
			reverted = true;
			run(300);
		} else {
			nextWidget = null;
			reverted = false;
			currentWidget = newWidget;
			newWidget.getElement().getStyle().setOpacity(0);
			Utils.getCF().getMainDisplay().setWidget(newWidget);
			Utils.getCF().getEventBus().fireEvent(new MainWidgetChangeEvent(lastWidget, newWidget));
			run(300);
		}
	}
	
	
	static public void changeWidget(Widget newWidget) {
		FadeAnimation fa = new FadeAnimation();
		fa.changeWdg(newWidget);
	}

	@Override
	protected void onComplete() {
		super.onComplete();// onUpdate(interpolate(1.0));
		if (nextWidget != null) {
			reverted = false;
			currentWidget = nextWidget;
			nextWidget.getElement().getStyle().setOpacity(0);
			Utils.getCF().getMainDisplay().setWidget(nextWidget);
			Utils.getCF().getEventBus().fireEvent(new MainWidgetChangeEvent(lastWidget, nextWidget));
			nextWidget = null;
			run(300);
		} else
			lastWidget = currentWidget;
	}

	@Override
	protected void onUpdate(double progress) {
		if (reverted)
			currentWidget.getElement().getStyle().setOpacity(1.0 - progress);
		else
			currentWidget.getElement().getStyle().setOpacity(progress);
	}
}
