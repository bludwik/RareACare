package com.blsoft.rareacare.client.ui;

import com.blsoft.rareacare.client.ui.interfaces.IGoodbyeView;
import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class GoodbyeView extends Composite implements IGoodbyeView
{

	SimplePanel viewPanel = new SimplePanel(); 
	Element nameSpan = DOM.createSpan();

	public GoodbyeView()
	{
		viewPanel.getElement().appendChild(nameSpan);
		initWidget(viewPanel);
	}

	@Override
	public void setName(String name)
	{
		nameSpan.setInnerText("Good-bye, " + name);
	}
	
}
