package com.blsoft.rareacare.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;

public class PatientEditor extends DialogBox {

	private static final Binder binder = GWT.create(Binder.class);
	@UiField TextBox surnameEditor;
	@UiField TextBox firstnameEditor;
	@UiField DateBox dayOfBirthEditor;
	@UiField TextBox peselEditor;
	@UiField ListBox genderEditor;
	@UiField PushButton btnOK;
	@UiField PushButton btnCancel;
	@UiField AddressEditor addressEditor;
	@UiField TextBox telephoneEditor;
	@UiField TextBox mobileEditor;
	@UiField TextBox emailEditor;


	interface Binder extends UiBinder<Widget, PatientEditor> {
	}

	public PatientEditor() {
		setWidget(binder.createAndBindUi(this));
	}

}
