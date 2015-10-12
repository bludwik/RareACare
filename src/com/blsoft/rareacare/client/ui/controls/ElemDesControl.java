package com.blsoft.rareacare.client.ui.controls;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.FlowPanel;

public class ElemDesControl extends Composite {
	private FlowPanel mainPanel;
	private HTML lbInfo;

	public ElemDesControl() {
		setStyleName("gwt-ItemPanel");
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("gwt-ItemPanel");
		initWidget(mainPanel);
		mainPanel.setSize("100%", "30px");
		
		lbInfo = new HTML("New HTML", true);
		mainPanel.add(lbInfo);
		lbInfo.setWidth("100%");
	}

}
