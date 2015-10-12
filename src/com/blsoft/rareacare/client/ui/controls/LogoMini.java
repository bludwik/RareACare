package com.blsoft.rareacare.client.ui.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LogoMini extends Composite {

	private static final Binder binder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, LogoMini> {
	}

	public LogoMini() {
		initWidget(binder.createAndBindUi(this));
	}

}
