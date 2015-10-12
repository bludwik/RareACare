package com.blsoft.rareacare.client.ui.controls;

import com.blsoft.rareacare.client.res.Images;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ElemToolBox extends PopupPanel {
	private HorizontalPanel horizontalPanel;
	private PushButton ptnLeft;
	private PushButton btnRight;
	private PushButton btnEdit;

	public ElemToolBox() {
		super(true);
		
		horizontalPanel = new HorizontalPanel();
		setWidget(horizontalPanel);
		horizontalPanel.setSize("100%", "100%");
		
		ptnLeft = new PushButton(new Image(Images.INSTANCE.arrowLeft24()));
		horizontalPanel.add(ptnLeft);
		
		btnRight = new PushButton(new Image(Images.INSTANCE.arrowRight24()));
		horizontalPanel.add(btnRight);
		
		btnEdit = new PushButton(new Image(Images.INSTANCE.edit24()));
		horizontalPanel.add(btnEdit);
	}

}
