package com.blsoft.rareacare.client.ui;

import java.util.LinkedList;
import java.util.List;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IUserActivity;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.InstitutionDataRequest;
import com.blsoft.rareacare.client.requestfactory.InstitutionProxy;
import com.blsoft.rareacare.client.requestfactory.UserDataRequest;
import com.blsoft.rareacare.client.requestfactory.UserProxy;
import com.blsoft.rareacare.client.ui.interfaces.IUserEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PasswordTextBox;

public class UserEditor extends DialogBox implements Editor<UserProxy>, IUserEditor, ClickHandler {

	private UserEditor.Driver usersEditorDriver;
	private IUserActivity presenter;
	private UserProxy currentUser = null;
	private List<InstitutionProxy> instList = null;

	private FlexTable flexTable;
	private Label lblImi;
	private Label lblNazwisko;
	private Label lblLogin;
	private Label lblEmail;
	private Label lblNewLabel;
	private Label lblOrodek;
	private Label lblKomentarz;
	private PushButton btnOk;
	private PushButton btnCancel;
	TextBox firstNameEditor;
	TextBox nameEditor;
	TextBox loginEditor;
	TextBox emailEditor;
	TextBox telephoneEditor;
	// @Path("institution.name")
	@Ignore SuggestBox institutionNameEditor;
	TextArea commentEditor;
	protected InstitutionProxy selectedInst = null;
	private PushButton btnInstEdit;
	private Label lblZmianaHasa;
	private PasswordTextBox passwordTextBox;
	private PushButton btnNewPass;

	public interface Driver extends RequestFactoryEditorDriver<UserProxy, UserEditor> {
	}

	private class CustomSuggestion implements SuggestOracle.Suggestion {

		InstitutionProxy fInst;

		public CustomSuggestion(InstitutionProxy inst) {
			fInst = inst;
		}

		@Override
		public String getDisplayString() {
			return fInst.getName();
		}

		@Override
		public String getReplacementString() {
			return fInst.getName();
		}

	}

	private List<CustomSuggestion> filterResponse(String patern) {
		List<CustomSuggestion> lst = new LinkedList<CustomSuggestion>();
		String s = patern.toUpperCase();
		for (InstitutionProxy p : instList) {
			if (p.getName().toUpperCase().contains(s)) {
				lst.add(new CustomSuggestion(p));
			}
		}
		return lst;
	}

	private class CustomOracle extends SuggestOracle {
		private String lastQuery = null;

		@Override
		public boolean isDisplayStringHTML() {
			return true;
		}

		@Override
		public void requestSuggestions(final Request request, final Callback callback) {
			final String query = request.getQuery().toUpperCase();
			if (instList == null || !query.startsWith(lastQuery)) {
				InstitutionDataRequest req = presenter.getClientFactory().getRequestFactory().institutionDataRequest();
				req.getInstitutions(query, 0, 300).fire(new CommonReceiver<List<InstitutionProxy>>() {
					@Override
					public void onSuccess(List<InstitutionProxy> response) {
						instList = response;
						lastQuery = query.toUpperCase();
						callback.onSuggestionsReady(request, new Response(filterResponse(query)));
					}
				});
			} else
				callback.onSuggestionsReady(request, new Response(filterResponse(query)));
		}
	}

	public UserEditor() {
		setGlassEnabled(true);
		setAutoHideOnHistoryEventsEnabled(true);

		flexTable = new FlexTable();
		flexTable.setCellSpacing(8);
		flexTable.setCellPadding(8);
		setWidget(flexTable);

		lblImi = new Label("Imię");
		flexTable.setWidget(0, 0, lblImi);
		flexTable.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);

		firstNameEditor = new TextBox();
		flexTable.setWidget(0, 1, firstNameEditor);
		firstNameEditor.setWidth("30EM");
		flexTable.getCellFormatter().setWidth(0, 1, "");

		lblNazwisko = new Label("Nazwisko");
		flexTable.setWidget(1, 0, lblNazwisko);

		nameEditor = new TextBox();
		flexTable.setWidget(1, 1, nameEditor);
		nameEditor.setWidth("30EM");

		lblLogin = new Label("Login");
		flexTable.setWidget(2, 0, lblLogin);

		loginEditor = new TextBox();
		flexTable.setWidget(2, 1, loginEditor);
		loginEditor.setWidth("30EM");

		lblEmail = new Label("e-mail");
		flexTable.setWidget(3, 0, lblEmail);

		emailEditor = new TextBox();
		flexTable.setWidget(3, 1, emailEditor);
		emailEditor.setWidth("30EM");

		lblNewLabel = new Label("Telefon");
		flexTable.setWidget(4, 0, lblNewLabel);

		telephoneEditor = new TextBox();
		flexTable.setWidget(4, 1, telephoneEditor);
		telephoneEditor.setWidth("30EM");

		lblOrodek = new Label("Ośrodek");
		flexTable.setWidget(5, 0, lblOrodek);
		lblOrodek.setWidth("8EM");
		flexTable.getCellFormatter().setWidth(5, 0, "");

		institutionNameEditor = new SuggestBox(new CustomOracle());
		institutionNameEditor.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				selectedInst = ((CustomSuggestion) event.getSelectedItem()).fInst;
			}
		});
		flexTable.setWidget(5, 1, institutionNameEditor);
		institutionNameEditor.setWidth("40EM");
		flexTable.getCellFormatter().setWidth(5, 1, "");

		btnInstEdit = new PushButton("...");
		btnInstEdit.addClickHandler(this);
		btnInstEdit.setHTML("...");
		flexTable.setWidget(5, 2, btnInstEdit);
		btnInstEdit.setHeight("100%");

		lblKomentarz = new Label("Komentarz");
		flexTable.setWidget(6, 0, lblKomentarz);

		commentEditor = new TextArea();
		flexTable.setWidget(6, 1, commentEditor);
		commentEditor.setSize("40EM", "4EM");

		btnCancel = new PushButton("New button");
		btnCancel.addClickHandler(this);

		lblZmianaHasa = new Label("Zmiana hasła");
		flexTable.setWidget(7, 0, lblZmianaHasa);

		passwordTextBox = new PasswordTextBox();
		flexTable.setWidget(7, 1, passwordTextBox);

		btnNewPass = new PushButton("Zmień");
		btnNewPass.addClickHandler(this);
		flexTable.setWidget(7, 2, btnNewPass);
		btnCancel.setHTML("Anuluj");
		btnCancel.setText("Anuluj");
		flexTable.setWidget(8, 0, btnCancel);
		flexTable.getCellFormatter().setHeight(8, 0, "");
		btnCancel.setHeight("150%");

		btnOk = new PushButton("OK");
		btnOk.addClickHandler(this);
		flexTable.setWidget(8, 1, btnOk);
		flexTable.getCellFormatter().setHeight(8, 1, "");
		btnOk.setSize("20EM", "150%");
		flexTable.getCellFormatter().setHorizontalAlignment(5, 2, HasHorizontalAlignment.ALIGN_LEFT);
		flexTable.getCellFormatter().setHorizontalAlignment(8, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter().setHorizontalAlignment(8, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter().setVerticalAlignment(8, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTable.getCellFormatter().setVerticalAlignment(8, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTable.getCellFormatter().setVerticalAlignment(7, 2, HasVerticalAlignment.ALIGN_MIDDLE);
	}

	@Override
	public void editUser(UserProxy u) {
		assert presenter != null;
		if (usersEditorDriver == null)
			usersEditorDriver = GWT.create(UserEditor.Driver.class);
		
		passwordTextBox.setText("");

		// Initialize the driver with the top-level editor
		usersEditorDriver.initialize(presenter.getClientFactory().getRequestFactory(), this);

		// Copy the data in the object into the UI
		UserDataRequest req = presenter.getClientFactory().getRequestFactory().userDataRequest();
		if (u == null) {
			currentUser = req.create(UserProxy.class);
			req.persist(currentUser); // Oznaczam do zapisania w bazie
		} else
			currentUser = req.edit(u); // Dzięki temu mogę edytować po swojemu
		usersEditorDriver.edit(currentUser, req);
		if (currentUser.getInstitution() != null) // ten edit obsługuję
													// cakowicie ręcznie
			institutionNameEditor.setText(currentUser.getInstitution().getName());
		else
			institutionNameEditor.setText(null);
		// Put the UI on the screen.
		this.setAnimationEnabled(true);
		this.setAutoHideOnHistoryEventsEnabled(true);
		this.setGlassEnabled(true);
		this.selectedInst = currentUser.getInstitution();
		this.center();
	}

	@Override
	public void setPresenter(IUserActivity presenter) {
		this.presenter = presenter;
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == btnOk) {

			// Taka naciągana metoda przypisywania objektu, bo nie umiem tego
			// zrobic automatycznie, a pewnie się da...
			if (institutionNameEditor.getText().isEmpty())
				currentUser.setInstitution(null);
			else if (selectedInst == null) {
				Window.alert("Musisz wybrać instytucję z listy");
				return;
			} else
				currentUser.setInstitution(selectedInst);

			RequestContext edited = usersEditorDriver.flush();
			if (usersEditorDriver.hasErrors()) {
				String msg = "Stwierdzono następujące będy:\n";
				for (EditorError v : usersEditorDriver.getErrors())
					msg += "Pole: " + v.getAbsolutePath() + "; BŁĄD: " + v.getMessage() + "\n";
				Utils.getCF().showError(msg); // TODO dodać obsugę bedów
												// walidacji i walidację w
												// ogóle
				return;
			}
			edited.fire(new CommonReceiver<Void>() {
				@Override
				public void onSuccess(Void response) {
					usersEditorDriver = null;
					hide();
					presenter.getUsers(null, Utils.getCF().getUsersView()); // Ładuje
																			// poprzedni
																			// zakres
																			// z
					// serwera
				}
			});

		} else if (event.getSource() == btnCancel) {
			usersEditorDriver = null;
			hide();
		} else if (event.getSource() == btnInstEdit) {
			Utils.getCF().getInstEditor().editInst(this);

		} else if (event.getSource() == btnNewPass) {
			UserDataRequest req = Utils.getCF().getRequestFactory().userDataRequest();
			if (currentUser == null || currentUser.getId() <= 0)
				Utils.showError("Hasło można zmienic jedynie dla zapisanych użytkowników");
			else {
				req.setNewPass(currentUser.getId(), Utils.getSHA1for(passwordTextBox.getText())).fire(new CommonReceiver<String>() {

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

	@Override
	public void setInst(InstitutionProxy inst) {
		selectedInst = inst;
		if (inst == null)
			institutionNameEditor.setText(null);
		else
			institutionNameEditor.setText(inst.getName());
	}

}
