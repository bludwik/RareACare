package com.blsoft.rareacare.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Elem;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.ScrollPanel;

public class RegDocPrv extends DialogBox {

	static private RegDocPrv regDocPrv = null;
	private static final Binder binder = GWT.create(Binder.class);
	@UiField LayoutPanel mainLayout;
	@UiField BLButton btnClose;
	@UiField ScrollPanel docPanel;

	
	interface Binder extends UiBinder<Widget, RegDocPrv> {
	}

	public RegDocPrv() {
		setWidget(binder.createAndBindUi(this));
		setAutoHideOnHistoryEventsEnabled(true);
		setGlassEnabled(true);
		setText("Podgląd dokumentu");
		setModal(true);
	}
	
	static public RegDocPrv get() {
		if (regDocPrv==null)
			regDocPrv = new RegDocPrv();
		return regDocPrv;
	}
	
	static public void showDoc(Doc doc, JsArray<Elem> elems) {
//		DocInstance prv = new DocInstance(doc, elems);
		DocInstance prv = new DocInstance();
		prv.loadDoc(null, doc, elems, true);
		get().docPanel.clear();
		regDocPrv.docPanel.add(prv);
		regDocPrv.mainLayout.setHeight(String.valueOf(Window.getClientHeight()-50) + "px");
		regDocPrv.mainLayout.setWidth(String.valueOf(Window.getClientWidth()-50) + "px");
		regDocPrv.center();
	}
	
	@UiHandler("btnClose")
	void onBtnCloseClick(ClickEvent event) {
		hide();
	}
}
