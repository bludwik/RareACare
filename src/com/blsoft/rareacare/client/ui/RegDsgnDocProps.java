package com.blsoft.rareacare.client.ui;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.TextArea;

public class RegDsgnDocProps extends DialogBox {
	
	private static RegDsgnDocProps editor;
	
	private Doc currentElem;
	private RareACareDef reg;

	private static final Binder binder = GWT.create(Binder.class); 
	@UiField
	TextBox nameEditor;
	@UiField
	PushButton btnCancel;
	@UiField
	PushButton btnOk;
	@UiField TextArea descrEditor;
	@UiField TextBox idEditor;

	interface Binder extends UiBinder<Widget, RegDsgnDocProps> {
	}

	public RegDsgnDocProps() {
		setText("Właściwości dokumentu");
		setWidget(binder.createAndBindUi(this));
	}
	

	static public RegDsgnDocProps get() {
		if (editor == null)
			editor = new RegDsgnDocProps();
		return editor;
	}
	
	static public void editDocProps(Doc e, RareACareDef r) {
		get();
		assert e!=null;
		editor.nameEditor.setValue(e.getName());
		editor.descrEditor.setValue(e.getDescr());
		editor.idEditor.setValue(e.getId());
		editor.currentElem = e;
		editor.reg = r;
		editor.center();		
	}

	@UiHandler("btnOk")
	void onBtnOkClick(ClickEvent event) {
		if (nameEditor.getValue() == null)
			Utils.getCF().showError("Musisz wypenić nazwę dokumentu");
		else if (idEditor.getValue() == null)
			Utils.getCF().showError("Musisz wypenić unikatow ID dokumentu. Musi on być unikatowy w ramach rejestru");
		else {
			
			// Czy ID jest unikatowe
			
			JsArray<Doc> docs = reg.getDocs();
			for (int i=0; i<docs.length(); i++)
				if (docs.get(i) != currentElem && docs.get(i).getId().equals(idEditor.getValue())) {
					Utils.getCF().showError("Wskazany identyfikator dokumentu już istnieje w rejestrze. Wpisz unikatowy identyfikator");
					return;
				}
			if (!Utils.chkIdRules(idEditor.getValue(), true)) 
				return;
				
			currentElem.setName(nameEditor.getValue());
			currentElem.setDescr(descrEditor.getValue());
			currentElem.setId(idEditor.getValue());
			Utils.getCF().getRegDesigner().addOrSetDoc(currentElem);
			hide();
		}

	}
	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		hide();
	}

}
