package com.blsoft.rareacare.client.ui;

import java.util.List;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IRegDsgnActivity;
import com.blsoft.rareacare.client.requestfactory.RegUserProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.ui.interfaces.IRegUsersEditor;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class RegUsersEditor extends DialogBox implements IRegUsersEditor {

	private IRegDsgnActivity presenter;
	ProvidesKey<RegUserProxy> keyProvider = new ProvidesKey<RegUserProxy>() {
		@Override
			public Object getKey(RegUserProxy item) {
				return (item == null) ? null : item.getId();
		}
	};		
	ListDataProvider<RegUserProxy> dataProvider = new ListDataProvider<RegUserProxy>();
	private static final Binder binder = GWT.create(Binder.class);
	@UiField(provided=true) DataGrid<RegUserProxy> dataGrid = new DataGrid<RegUserProxy>(keyProvider);
	@UiField PushButton btnAddUser;
	@UiField PushButton btnClose;

	RegUserProxy currentUser;

	interface Binder extends UiBinder<Widget, RegUsersEditor> {
	}

	public RegUsersEditor() {		
		
		// Dodawanie kolumn do tabeli
		TextColumn<RegUserProxy> colName = new TextColumn<RegUserProxy>() {
			@Override
			public String getValue(RegUserProxy object) {
				return object.getUser() !=null ? object.getUser().getName() + " " + object.getUser().getFirstName() + " (" + object.getUser().getLogin() + ")" : null; 
			}
		};
		colName.setSortable(true);
		colName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(colName, "Użytkownik");
		dataGrid.setColumnWidth(colName, "10EM");

		TextColumn<RegUserProxy> colRight = new TextColumn<RegUserProxy>() {
			@Override
			public String getValue(RegUserProxy object) {
				return object.getCanRead().toString(); 
			}
		};
		colRight.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dataGrid.addColumn(colRight, "Odczyt");
		dataGrid.setColumnWidth(1, "5EM");

		colRight = new TextColumn<RegUserProxy>() {
			@Override
			public String getValue(RegUserProxy object) {
				return object.getCanEdit().toString(); 
			}
		};
		colRight.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dataGrid.addColumn(colRight, "Edycja");
		dataGrid.setColumnWidth(2, "5EM");

		colRight = new TextColumn<RegUserProxy>() {
			@Override
			public String getValue(RegUserProxy object) {
				return object.getCanCorrect().toString(); 
			}
		};
		colRight.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dataGrid.addColumn(colRight, "Korekta");
		dataGrid.setColumnWidth(3, "5EM");


		Column<RegUserProxy, Boolean> colCb = new Column<RegUserProxy, Boolean>(new CheckboxCell(false, false)) {
			@Override
			public Boolean getValue(RegUserProxy object) {
				return object.getCanDefine(); 
			}
		};
		colCb.setSortable(true);
		colCb.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(colCb, "Definiowanie");
		dataGrid.setColumnWidth(4, "5EM");

		
		// Akcja edycji użytkowników
		ActionCell<RegUserProxy> colEdit = new ActionCell<RegUserProxy>("Edytuj", new ActionCell.Delegate<RegUserProxy>() {
			@Override
			public void execute(RegUserProxy rg) {
				Utils.getCF().getRegUserPropEditor().editRegUser(rg, presenter); 
			}
		});
		Column<RegUserProxy, RegUserProxy> col = new Column<RegUserProxy, RegUserProxy>(colEdit) {
			@Override
			public RegUserProxy getValue(RegUserProxy object) {
				return object;
			}

		};
		dataGrid.addColumn(col);
		dataGrid.setColumnWidth(col, "4EM");
		
		// Akcja usuwania użytkownika
		ActionCell<RegUserProxy> colDes = new ActionCell<RegUserProxy>("Usuń", new ActionCell.Delegate<RegUserProxy>() {
			@Override
			public void execute(RegUserProxy rg) {
				if (Utils.getCF().confirm("Czy jesteś pewien, że chcesz usunąć wskazanego użytkownika?"))
					presenter.deleteRegUser(rg);
			}
		});
		Column<RegUserProxy, RegUserProxy> colDsg = new Column<RegUserProxy, RegUserProxy>(colDes) {
			@Override
			public RegUserProxy getValue(RegUserProxy object) {
				return object;
			}

		};
		dataGrid.addColumn(colDsg);
		dataGrid.setColumnWidth(colDsg, "4EM");
		dataProvider.addDataDisplay(dataGrid);

//		// Dodaję handler do zmian objektu
//		Utils.getCF().getEventBus().addHandler(new Type<EntityChangeHandler<RegUserProxy>>(), new EntityChangeHandler<RegUserProxy>() {
//
//			@Override
//			public void onEvent(EntityChangeEvent<RegUserProxy> event) {
//				Utils.replaceOrAddEntity(dataProvider.getList(), event.getEntity());
//				dataProvider.refresh();				
//			}
//		});
//		
		setWidget(binder.createAndBindUi(this));
		
	}
	
	@Override
	public void showRegUsersEditor(RegistryProxy u, IRegDsgnActivity presenter) {
		this.presenter= presenter; 
//		dataProvider.removeDataDisplay(dataGrid);
		// Put the UI on the screen.
//		this.setAnimationEnabled(true);
		this.setAutoHideOnHistoryEventsEnabled(true);
		this.setGlassEnabled(true);
		this.center();
	}
	
	@Override
	public void consumeRegUsers(List<RegUserProxy> ls) {
		dataProvider.setList(ls);
	}	
	

	@UiHandler("btnClose")
	void onBtnCloseClick(ClickEvent event) {
		hide();
	}
	@UiHandler("btnAddUser")
	void onBtnAddUserClick(ClickEvent event) {
		presenter.editRegUser(null);
	}
}
