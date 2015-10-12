package com.blsoft.rareacare.client.ui;

import com.blsoft.rareacare.client.activity.ILoginActivity;
import com.blsoft.rareacare.client.place.HelloPlace;
import com.blsoft.rareacare.client.ui.interfaces.ILoginView;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class LoginView extends Composite implements ILoginView {

	private ILoginActivity presenter;
	private TextBox txtLogin;
	private PasswordTextBox txtPassword;

	public LoginView() {
		
		LayoutPanel layoutPanel = new LayoutPanel();
		initWidget(layoutPanel);
		layoutPanel.setSize("100%", "30EM");
		
		DecoratorPanel decoratorPanel = new DecoratorPanel();
		layoutPanel.add(decoratorPanel);
		decoratorPanel.setSize("", "");
		layoutPanel.setWidgetLeftRight(decoratorPanel, 30.0, Unit.PCT, 30.0, Unit.PCT);
		layoutPanel.setWidgetTopHeight(decoratorPanel, 30.0, Unit.PCT, 16.0, Unit.EM);
		
		FlexTable flexTable = new FlexTable();
		flexTable.setCellSpacing(8);
		flexTable.setStyleName("gwt-DecoratorPanel .middleCenter");
		flexTable.setCellPadding(8);
		decoratorPanel.setWidget(flexTable);
		flexTable.setSize("100%", "100%");
		
		Label lblNazwaUytkownika = new Label("Nazwa u\u017Cytkownika");
		flexTable.setWidget(0, 0, lblNazwaUytkownika);
		
		txtLogin = new TextBox();
		flexTable.setWidget(0, 2, txtLogin);
		txtLogin.setWidth("90%");
		flexTable.getCellFormatter().setWidth(0, 2, "");
		
		Label lblHaso = new Label("Has\u0142o");
		flexTable.setWidget(1, 0, lblHaso);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		flexTable.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		
		txtPassword = new PasswordTextBox();
		flexTable.setWidget(1, 2, txtPassword);
		txtPassword.setWidth("90%");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(8);
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getFlexCellFormatter().setColSpan(2, 0, 3);
		flexTable.setWidget(2, 0, horizontalPanel);
		horizontalPanel.setWidth("100%");
		
		PushButton btnCancel = new PushButton("Anuluj");
		btnCancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				presenter.goTo(new HelloPlace());
			}
		});
		btnCancel.setHTML("Anuluj");
		horizontalPanel.add(btnCancel);
		horizontalPanel.setCellHorizontalAlignment(btnCancel, HasHorizontalAlignment.ALIGN_CENTER);
		btnCancel.setWidth("10EM");
		
		PushButton btnOk = new PushButton("OK");
		btnOk.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				presenter.login(txtLogin.getText(), txtPassword.getText());
			}
		});
		btnOk.setHTML("OK");
		horizontalPanel.add(btnOk);
		btnOk.setWidth("10EM");
	}
	
	public void setPresenter(ILoginActivity presenter){
		this.presenter = presenter;
	}

	@Override
	public void initView() {
		txtPassword.setText(null);		
	}

}
