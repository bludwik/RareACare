package com.blsoft.rareacare.client.ui;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IMainActivity;
import com.blsoft.rareacare.client.place.RegsPlace;
import com.blsoft.rareacare.client.place.UsersPlace;
import com.blsoft.rareacare.client.res.Images;
import com.blsoft.rareacare.client.ui.interfaces.IMainView;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Grid;

public class MainView extends Composite implements IMainView, ClickHandler {

	private IMainActivity presenter;

	private PushButton btnRegs;
	private PushButton btnUsers;
	private PushButton btnClose;
	private PushButton btnChgPass;

	public MainView() {

		DockPanel dockPanel = new DockPanel();
		dockPanel.setSpacing(3);
		initWidget(dockPanel);
		dockPanel.setSize("100%", "100%");

//		HTML htmlHtml = new HTML("<b>Teraz jakis text w HTML</b><br>I sam juz ni wiem, co jeszcze", true);
//		dockPanel.add(htmlHtml, DockPanel.WEST);
//		dockPanel.setCellWidth(htmlHtml, "20%");
//
//		HTML html = new HTML("<b>Teraz jakis text w HTML</b><br>I sam juz ni wiem, co jeszcze", true);
//		dockPanel.add(html, DockPanel.EAST);
//		dockPanel.setCellWidth(html, "20%");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel.setSpacing(20);
		dockPanel.add(verticalPanel, DockPanel.CENTER);
		verticalPanel.setSize("100%", "100%");

		Grid grid = new Grid(4 , 2);
		grid.setCellSpacing(10);
		grid.setCellPadding(10);
		verticalPanel.add(grid);
		grid.setSize("100%", "100%");

		int iRow = 0;

		Image image = new Image(Images.INSTANCE.registry());
		image.addClickHandler(this);
		btnRegs = new PushButton(image);
		btnRegs.addClickHandler(this);
		grid.setWidget(iRow, 0, btnRegs);
		grid.getCellFormatter().setWidth(0, 0, "160");
		grid.getCellFormatter().setHeight(0, 0, "140");
		btnRegs.setSize("", "");

		HTML htmlNewHtml = new HTML(
				"Edycja i zarządzanie rejestrami.<br>\r\nWciśnij ten klawisz, jeśli chcesz edytowac zawartość któregoś z rejestrów, do których posiadasz uprawnnienia.<br>\r\nJeśli jesteś administratorem rejestru, wówczas możesz tu dokonywać jego edycji.",
				true);
		grid.setWidget(iRow, 1, htmlNewHtml);

		Image image_1 = new Image(Images.INSTANCE.users());
		btnUsers = new PushButton(image_1);
		btnUsers.addClickHandler(this);
		grid.setWidget(++iRow, 0, btnUsers);

		HTML htmlEdycjaIZarzdzanie = new HTML(
				"Edycja i zarządzanie użytkownikami.<br>\r\nJeśli jesteś administratorem, wówczas możesz tu zarządzać użytkownikami.", true);
		grid.setWidget(iRow, 1, htmlEdycjaIZarzdzanie);

		btnChgPass = new PushButton(new Image(Images.INSTANCE.passChg128()));
		btnChgPass.addClickHandler(this);
		grid.setWidget(++iRow, 0, btnChgPass);

		HTML htmlPassChange = new HTML("Zmiana hasła zalogowanego użytkownika", true);
		grid.setWidget(iRow, 1, htmlPassChange);

		Image image_2 = new Image(Images.INSTANCE.close());
		btnClose = new PushButton(image_2);
		btnClose.addClickHandler(this);
		grid.setWidget(++iRow, 0, btnClose);
		grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		grid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

		HTML htmlOpuszczenieTrefyChronionej = new HTML(
				"Opuszczenie strefy chronionej rejestru.<br>\r\nZostaniesz przeniesiony do części ogólnodostępnej", true);
		grid.setWidget(iRow, 1, htmlOpuszczenieTrefyChronionej);
	}

	@Override
	public void setPresenter(IMainActivity presenter) {
		this.presenter = presenter;
		btnUsers.setEnabled(Utils.getUser().getIsSuperuser());
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == btnClose) {
			Utils.logout(true);
		} else if (event.getSource() == btnRegs) {
			presenter.goTo(new RegsPlace());
		} else if (event.getSource() == btnUsers) {
			presenter.goTo(new UsersPlace(null));
		} else if (event.getSource() == btnChgPass) {
			PassChange.openDialog();
		}
	}
}
