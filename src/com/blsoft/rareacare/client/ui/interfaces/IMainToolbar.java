package com.blsoft.rareacare.client.ui.interfaces;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.Command;

public class IMainToolbar extends Composite {

	public IMainToolbar() {
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		initWidget(horizontalPanel);
		horizontalPanel.setWidth("100%");
		
		MenuBar menuBar = new MenuBar(false);
		horizontalPanel.add(menuBar);
		
		MenuItem mntmAdmin = new MenuItem("Administracja", false, (Command) null);
		menuBar.addItem(mntmAdmin);
		
		MenuItem mntmDsgn = new MenuItem("New item", false, (Command) null);
		mntmDsgn.setHTML("Projektowanie");
		menuBar.addItem(mntmDsgn);
		MenuBar menuBarUsers = new MenuBar(true);
		menuBarUsers.setAutoOpen(true);
		menuBarUsers.setAnimationEnabled(true);
		
		MenuItem mntmUsers = new MenuItem("New menu", false, menuBarUsers);
		
		MenuItem mntmChangePass = new MenuItem("Zmiana hasła", false, (Command) null);
		menuBarUsers.addItem(mntmChangePass);
		mntmUsers.setHTML("Użytkownicy");
		menuBar.addItem(mntmUsers);
		
		InlineHTML lbUser = new InlineHTML("Użytkownik: ");
		horizontalPanel.add(lbUser);
		horizontalPanel.setCellVerticalAlignment(lbUser, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(lbUser, HasHorizontalAlignment.ALIGN_CENTER);
		
		InlineHTML lbLogo = new InlineHTML("Rare-A-Care<br>Registry");
		lbLogo.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(lbLogo);
		horizontalPanel.setCellHorizontalAlignment(lbLogo, HasHorizontalAlignment.ALIGN_RIGHT);
	}

}
