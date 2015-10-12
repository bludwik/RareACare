package com.blsoft.rareacare.client.ui;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.blsoft.rareacare.client.activity.IPresenter;
import com.blsoft.rareacare.client.place.LoginPlace;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.blsoft.rareacare.client.ui.interfaces.IHelloView;

public class HelloView extends Composite implements IHelloView
{
	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);

	interface HelloViewImplUiBinder extends UiBinder<Widget, HelloView>
	{
	}

	@UiField BLButton btnLogin;

	private IPresenter iPresenter;

	public HelloView()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}



	@Override
	public void setPresenter(IPresenter listener)
	{
		this.iPresenter = listener;
	}
	
	@UiHandler("btnLogin")
	void onLoginClick(ClickEvent event) {
		iPresenter.goTo(new LoginPlace());
	}
}
