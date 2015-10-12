package com.blsoft.rareacare.client.ui.controls;

import java.util.ArrayList;
import java.util.List;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.UserDataRequest;
import com.blsoft.rareacare.client.requestfactory.UserProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

public class UserSuggestBox extends Composite implements LeafValueEditor<UserProxy> {

	private UserProxy selectedUser;
	private static final Binder binder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, UserSuggestBox> {
	}

	@UiField(provided = true)
	SuggestBox suggestBox = new SuggestBox(new CustomOracle());

	private String getUserCaption(UserProxy us) {
		return us.getName() + " " + us.getFirstName() + " (" + us.getLogin() + ")";
	}

	private class CustomSuggestion implements SuggestOracle.Suggestion {

		UserProxy fUser;

		public CustomSuggestion(UserProxy inst) {
			fUser = inst;
		}
		
		@Override
		public String getDisplayString() {
			return getUserCaption(fUser);
		}

		@Override
		public String getReplacementString() {
			return getUserCaption(fUser);
		}

	}

	private List<UserProxy> userList;

	private List<CustomSuggestion> filterResponse(String patern) {
		List<CustomSuggestion> lst = new ArrayList<CustomSuggestion>();
		String s = patern.toUpperCase();
		for (UserProxy p : userList) {
			if (getUserCaption(p).toUpperCase().contains(s)) {
				lst.add(new CustomSuggestion(p));
			}
		}
		return lst;
	}

	private class CustomOracle extends SuggestOracle {
		private String lastQuery = null;

		@Override
		public boolean isDisplayStringHTML() {
			return false;
		}

		@Override
		public void requestSuggestions(final Request request, final Callback callback) {
			final String query = request.getQuery().toUpperCase();
			if (userList == null || !query.startsWith(lastQuery)) {
				UserDataRequest req = Utils.getCF().getRequestFactory().userDataRequest();
				req.getUsers(query, 0, 300).fire(new CommonReceiver<List<UserProxy>>() {
					@Override
					public void onSuccess(List<UserProxy> response) {
						userList = response;
						lastQuery = query;
						callback.onSuggestionsReady(request, new Response(filterResponse(query)));
					}
				});
			} else
				callback.onSuggestionsReady(request, new Response(filterResponse(query)));
		}
	}

	public UserSuggestBox() {

		suggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				selectedUser = ((CustomSuggestion) event.getSelectedItem()).fUser;
			}
		});

		initWidget(binder.createAndBindUi(this));
	}

	public SuggestBox getSuggestBox() {
		return suggestBox;
	}
	
	@Override
	public void setValue(UserProxy value) {
		String display = (value == null) ? "" : getUserCaption(value);
		suggestBox.setText(display);
		selectedUser = value;
	}

	@Override
	public UserProxy getValue() {
		String s = suggestBox.getText();
		if (userList != null) {
			for (UserProxy u : userList)
				if (s.equals(getUserCaption(u)))
					return u;
		}
		if (selectedUser != null && s.equals(getUserCaption(selectedUser)))
			return selectedUser;
		else {
			suggestBox.setText(null);
			return null;
		}
	}

}
