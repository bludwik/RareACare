package com.blsoft.rareacare.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IRegDsgnActivity;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.RegUserProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.ui.controls.UserSuggestBox;
import com.blsoft.rareacare.client.ui.interfaces.IRegUserPropEditor;
import com.blsoft.rareacare.shared.RegRights;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class RegUserPropEditor extends DialogBox implements IRegUserPropEditor {

	private RegUserProxy currentUser;
	private IRegDsgnActivity presenter;

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	UserSuggestBox userEditor;
	@UiField
	SimpleCheckBox canDesignEditor;
	@UiField
	ListBox canReadEditor;
	@UiField
	ListBox canEditEditor;
	@UiField
	ListBox canCorrectEditor;
	@UiField
	PushButton btnCancel;
	@UiField
	PushButton btnOk;

	private RegistryDataRequest req;

	interface Binder extends UiBinder<Widget, RegUserPropEditor> {
	}

	public RegUserPropEditor() {
		setWidget(binder.createAndBindUi(this));
		initCombo(canReadEditor);
		initCombo(canEditEditor);
		initCombo(canCorrectEditor);
	}

	private void initCombo(ListBox lb) {
		lb.addItem(RegRights.NONE.toString());
		lb.addItem(RegRights.OWN.toString());
		lb.addItem(RegRights.ALL.toString());
	}

	public void editRegUser(RegUserProxy u, IRegDsgnActivity presenter) {
		this.presenter = presenter;

		req = Utils.getCF().getRequestFactory().registryDataRequest();
		if (u == null) {
			currentUser = req.create(RegUserProxy.class);
		} else
			currentUser = req.edit(u);

		userEditor.setValue(currentUser.getUser());
		canReadEditor.setItemSelected(RegRights.getIdx(currentUser.getCanRead()), true);
		canEditEditor.setItemSelected(RegRights.getIdx(currentUser.getCanEdit()), true);
		canCorrectEditor.setItemSelected(RegRights.getIdx(currentUser.getCanCorrect()), true);
		canDesignEditor.setValue(currentUser.getCanDefine());

		// Nie wolno edytować użytkownika już przypisanego
		userEditor.getSuggestBox().setEnabled(currentUser.getId() == null);
//		this.setAnimationEnabled(true);
		this.setAutoHideOnHistoryEventsEnabled(true);
		this.setGlassEnabled(true);
		this.center();
	}

	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		req = null;
		hide();
	}

	@UiHandler("btnOk")
	void onBtnOkClick(ClickEvent event) {
		if (userEditor.getValue() == null)
			Utils.getCF().showMessage("Nie przypisano użytkownika");
		else {
			currentUser.setRegistry(presenter.getRegistry());
			currentUser.setUser(userEditor.getValue());
			currentUser.setCanRead(RegRights.getRightFromIndex(canReadEditor.getSelectedIndex()));
			currentUser.setCanEdit(RegRights.getRightFromIndex(canEditEditor.getSelectedIndex()));
			currentUser.setCanCorrect(RegRights.getRightFromIndex(canCorrectEditor.getSelectedIndex()));
			currentUser.setCanDefine(canDesignEditor.getValue());

			// Zapis do serwera
			req.persistAndLoad(currentUser).with("user").fire(new CommonReceiver<RegUserProxy>() {

				@Override
				public void onSuccess(RegUserProxy response) {
					hide();
					presenter.saveRegUser(response);
				}
			});

		}
		// Utils.getCF().getEventBus().fireEvent(new
		// EntityChangeEvent<RegUserProxy>(currentUser));
	}
}
