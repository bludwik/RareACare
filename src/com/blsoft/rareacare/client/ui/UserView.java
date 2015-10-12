package com.blsoft.rareacare.client.ui;

import java.util.List;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IUserActivity;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.blsoft.rareacare.client.place.MainPlace;
import com.blsoft.rareacare.client.requestfactory.UserProxy;
import com.blsoft.rareacare.client.res.Images;
import com.blsoft.rareacare.client.ui.interfaces.IUserView;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.blsoft.rareacare.client.ui.controls.BLButton.ImgPos;
import com.blsoft.rareacare.client.ui.controls.LogoMini;
import com.blsoft.rareacare.client.ui.controls.HomeButton;

public class UserView extends Composite implements IUserView, RangeChangeEvent.Handler, ClickHandler {

	private IUserActivity presenter;
	private DockLayoutPanel dockPanel;
	private DockPanel dockSubPanel;
	private Label lblNewLabel;
	private TextBox textBox;
	private DataGrid<UserProxy> usersTable;
	private TextColumn<UserProxy> colName;
	private TextColumn<UserProxy> colFirstName;
	private SimplePager simplePager;
	private Column<UserProxy, Number> columnId;
	private TextColumn<UserProxy> colInstitution;
	private BLButton btnAdd;
	private LogoMini logoMini;
	private HomeButton btnHome;

	public UserView() {

		dockPanel = new DockLayoutPanel(Unit.EM);
		initWidget(dockPanel);
		dockPanel.setSize("100%", "100%");

		dockSubPanel = new DockPanel();
		dockSubPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dockSubPanel.setSpacing(5);
		dockPanel.addNorth(dockSubPanel, 4.0);
		dockSubPanel.setWidth("100%");

		lblNewLabel = new Label("Szukaj użytkownika");
		dockSubPanel.add(lblNewLabel, DockPanel.WEST);
		dockSubPanel.setCellHorizontalAlignment(lblNewLabel, HasHorizontalAlignment.ALIGN_RIGHT);
		lblNewLabel.setWordWrap(false);

		textBox = new TextBox();
		dockSubPanel.add(textBox, DockPanel.CENTER);
		textBox.setWidth("95%");

		btnAdd = new BLButton("Dodaj",Images.INSTANCE.add32(), ImgPos.Left);
		btnAdd.addClickHandler(this);

		logoMini = new LogoMini();
		dockSubPanel.add(logoMini, DockPanel.EAST);
		dockSubPanel.setCellWidth(logoMini, "100px");
		dockSubPanel.setCellVerticalAlignment(logoMini, HasVerticalAlignment.ALIGN_MIDDLE);
		dockSubPanel.setCellHorizontalAlignment(logoMini, HasHorizontalAlignment.ALIGN_CENTER);

		btnHome = new HomeButton();
		btnHome.addClickHandler(this);
		dockSubPanel.add(btnHome, DockPanel.EAST);
		dockSubPanel.setCellWidth(btnHome, "50px");

		dockSubPanel.add(btnAdd, DockPanel.EAST);
		dockSubPanel.setCellWidth(btnAdd, "8EM");
		btnAdd.setWidth("7EM");
		dockSubPanel.setCellHorizontalAlignment(btnAdd, HasHorizontalAlignment.ALIGN_CENTER);

		ProvidesKey<UserProxy> keyProvider = new ProvidesKey<UserProxy>() {
			@Override
			public Object getKey(UserProxy item) {
				return (item == null) ? null : item.getId();
			}
		};

		usersTable = new DataGrid<UserProxy>(keyProvider);
		usersTable.setVisible(true);

		// ActionCell<String> colEdit = new ActionCell<String>("Edytuj", null);
		// usersTable.addColumn(colEdit, "Edit");

		// Add a selection model to handle user selection.
		// final SingleSelectionModel<UserProxy> selectionModel = new
		// SingleSelectionModel<UserProxy>(keyProvider);
		// usersTable.setSelectionModel(selectionModel);
		// selectionModel.addSelectionChangeHandler(new
		// SelectionChangeEvent.Handler() {
		// public void onSelectionChange(SelectionChangeEvent event) {
		// UserProxy selected = selectionModel.getSelectedObject();
		// if (selected != null) {
		// // Window.alert("You selected: " + selected.toString());
		// }
		// }
		// });

		// Define a key provider for a Contact. We use the unique ID as the key,
		// which allows to maintain selection even if the name changes.

		simplePager = new SimplePager();
		simplePager.setDisplay(usersTable);
		dockPanel.addSouth(simplePager, 2);
		usersTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		usersTable.setPageSize(25);
		dockPanel.add(usersTable);
		usersTable.setWidth("100%");

		columnId = new Column<UserProxy, Number>(new NumberCell()) {
			@Override
			public Number getValue(UserProxy object) {
				return object.getId();
			}
		};
		usersTable.addColumn(columnId, "Id");

		colName = new TextColumn<UserProxy>() {
			@Override
			public String getValue(UserProxy object) {
				return object.getName();
			}
		};
		colName.setSortable(true);
		colName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		usersTable.addColumn(colName, "Nazwisko");

		colFirstName = new TextColumn<UserProxy>() {
			@Override
			public String getValue(UserProxy object) {
				return object.getFirstName();
			}
		};
		colFirstName.setSortable(true);
		usersTable.addColumn(colFirstName, "Imię");

		colInstitution = new TextColumn<UserProxy>() {
			@Override
			public String getValue(UserProxy object) {
				if (object.getInstitution() != null)
					return object.getInstitution().getName();
				else
					return null;
			}
		};
		usersTable.addColumn(colInstitution, "Ośrodek");

		// Dodawanie klawiszy (ręczne - designer nie daje rady....)

		ActionCell<UserProxy> colEdit = new ActionCell<UserProxy>("Edytuj", new ActionCell.Delegate<UserProxy>() {
			@Override
			public void execute(UserProxy user) {
				presenter.editUser(user);
			}
		});
		Column<UserProxy, UserProxy> col = new Column<UserProxy, UserProxy>(colEdit) {
			@Override
			public UserProxy getValue(UserProxy object) {
				return object;
			}

		};
		usersTable.addColumn(col);
		usersTable.setColumnWidth(col, "7EM");

		ActionCell<UserProxy> colDelete = new ActionCell<UserProxy>("Usuń", new ActionCell.Delegate<UserProxy>() {
			@Override
			public void execute(UserProxy user) {
				if (Window.confirm("Czy jesteś pewien, że chcesz usunąć użytkownika: " + user.getFirstName() + " " + user.getName()))
					presenter.deleteUser(user);
			}
		});

		col = new Column<UserProxy, UserProxy>(colDelete) {
			@Override
			public UserProxy getValue(UserProxy object) {
				return object;
			}
		};
		usersTable.addColumn(col);
		usersTable.setColumnWidth(col, "7EM");

	}

	@Override
	public void setPresenter(IUserActivity presenter) {
		this.presenter = presenter;
		presenter.getUserCount(this);
		usersTable.addRangeChangeHandler(this);
		// Force the cellList to fire an initial range change event.
		usersTable.setVisibleRangeAndClearData(new Range(0, 25), true);
	}

	@Override
	public void onRangeChange(RangeChangeEvent event) {
		presenter.getUsers(event.getNewRange(), this);
	}

	public void onClick(ClickEvent event) {
		if (event.getSource().equals(btnAdd))
			presenter.editUser(null);
		else if (event.getSource().equals(btnHome))
			Utils.getCF().goTo(new MainPlace());
	}

	@Override
	public void consumeUserList(int startRange, List<UserProxy> list) {
		usersTable.setRowData(startRange, list);
	}

	@Override
	public void consumeUserCount(int count) {
		usersTable.setRowCount(count);
	}

}
