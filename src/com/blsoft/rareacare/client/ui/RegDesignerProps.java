package com.blsoft.rareacare.client.ui;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.blsoft.rareacare.client.ui.controls.UserSuggestBox;
import com.blsoft.rareacare.client.ui.interfaces.IRegDesignerProps;
import com.blsoft.rareacare.client.ui.interfaces.IRegsDesignerPropsConsumer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.gwt.user.client.ui.TextArea;

public class RegDesignerProps extends DialogBox implements Editor<RegistryProxy>, IRegDesignerProps {

	private static final Binder binder = GWT.create(Binder.class);

	@UiField TextBox nameEditor;

	@UiField UserSuggestBox ownerEditor;

	@UiField TextArea descrEditor;

	@UiField PushButton btnCancel;
	@UiField PushButton btnOk;

	private RequestFactoryEditorDriver<RegistryProxy, RegDesignerProps> regEditorDriver;
	private RegistryProxy currentReg;

	interface Binder extends UiBinder<Widget, RegDesignerProps> {
	}

	public interface Driver extends RequestFactoryEditorDriver<RegistryProxy, RegDesignerProps> {
	}

	public RegDesignerProps() {
		setText("Właściwości rejestru");
		setWidget(binder.createAndBindUi(this));
	}

	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		close();
	}

	private void close() {
		regEditorDriver = null;
		hide();
	}

	@UiHandler("btnOk")
	void onBtnOkClick(ClickEvent event) {
		RequestContext edited = regEditorDriver.flush();
		if (regEditorDriver.hasErrors()) {
			String msg = "Stwierdzono następujące będy:\n";
			for (EditorError v : regEditorDriver.getErrors())
				msg += "Pole: " + v.getAbsolutePath() + "; BŁĄD: " + v.getMessage() + "\n";
			Utils.getCF().showError(msg); // TODO dodać obsugę bedów
											// walidacji i walidację w
											// ogóle
			return;
		}

		RegistryProxy.RareACareDef d = (RareACareDef) RegistryProxy.RareACareDef.createFromString(currentReg.getDef());
		if (d.getName() != currentReg.getName()) {
			d.setName(currentReg.getName());
			currentReg.setDef(d.toJson());
		}
		edited.fire();
	}

	@Override
	public void edit(RegistryProxy reg, final IRegsDesignerPropsConsumer presenter) {

		if (regEditorDriver == null)
			regEditorDriver = GWT.create(Driver.class);
		// Initialize the driver with the top-level editor
		regEditorDriver.initialize(Utils.getCF().getRequestFactory(), this);

		// Copy the data in the object into the UI
		RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
		if (reg == null) {
			currentReg = req.create(RegistryProxy.class);
			if (currentReg.getDef() == null) {
				currentReg.setDef("{}");
			}
		} else
			currentReg = req.edit(reg);

		// Poniższe będzie wykonane w moemencie wykonania Fire po btnOK
		req.persistAndLoad(currentReg, false).to(new CommonReceiver<RegistryProxy>() {
			@Override
			public void onSuccess(RegistryProxy response) {
				if (presenter != null) {
					presenter.setRegistryPropsEdited(response);
				}
				close();
			}
		}); // Oznaczam do zapisania w bazie

		regEditorDriver.edit(currentReg, req);
		this.setAnimationEnabled(true);
		this.setAutoHideOnHistoryEventsEnabled(true);
		this.setGlassEnabled(true);
		this.center();
	}
}
