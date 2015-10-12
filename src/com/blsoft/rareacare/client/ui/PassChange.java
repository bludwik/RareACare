package com.blsoft.rareacare.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.UserDataRequest;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class PassChange extends DialogBox {

	private static final Binder binder = GWT.create(Binder.class);
	private static PassChange editor;
	@UiField Label lbUser;
	@UiField PasswordTextBox edOldPass;
	@UiField PasswordTextBox edNewPass;
	@UiField PasswordTextBox edConfPass;
	@UiField BLButton btnCancel;
	@UiField BLButton btnOK;

	interface Binder extends UiBinder<Widget, PassChange> {
	}

	public PassChange() {
		setWidget(binder.createAndBindUi(this));
	}

	static public PassChange get() {
		if (editor == null)
			editor = new PassChange();
		return editor;
	}

	public static void openDialog() {
		Utils.CheckLogIn();
		get();
		if (Utils.getUserId() <= 0)
			Utils.showError("Nie zalogowano użytkownika, lub jeszcze nie zaczytano danych");
		else {
			editor.lbUser.setText(Utils.getUser().getName());
			editor.edOldPass.setText("");
			editor.edNewPass.setText("");
			editor.edConfPass.setText("");
		}
		editor.setAnimationEnabled(true);
		editor.setAutoHideOnHistoryEventsEnabled(true);
		editor.setGlassEnabled(true);
		editor.center();
	}

	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		hide();
	}

	@UiHandler("btnOK")
	void onBtnOKClick(ClickEvent event) {
		/** Funkcja sprawdza, czy user jest zalogowany. jeśli nie, to próbuje
		 * poprbrać usera z sesji; Jeśli sie to nie powiedzie, to przekierowuje
		 * aplikację do strony logowania */
		if (Utils.isLoggedIn()) {
			if (edOldPass.getText().equals(edNewPass.getText()))
				Utils.showError("Stare i nowe hasła nie mogą być jednakowe");
			else if (!edNewPass.getText().equals(edConfPass.getText()))
				Utils.showError("Nowe hasło i potwierdzenie muszą być zgodne");
			else {
				UserDataRequest req = Utils.getCF().getRequestFactory().userDataRequest();
				req.changePass(Utils.getUserId(), Utils.getSHA1for(edOldPass.getText()), Utils.getSHA1for(edNewPass.getText())).fire(new CommonReceiver<String>() {

					@Override
					public void onSuccess(String response) {
						if (response.isEmpty()) {
							Utils.showMessage("Hasło zostało zmienione");
							hide();
						}
						Utils.showMessage(response);
					}
				});
			}
		}

	}
}
