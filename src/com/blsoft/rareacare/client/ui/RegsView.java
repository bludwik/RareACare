package com.blsoft.rareacare.client.ui;

import java.util.List;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IRegsActivity;
import com.blsoft.rareacare.client.place.MainPlace;
import com.blsoft.rareacare.client.place.RegDsgnPlace;
import com.blsoft.rareacare.client.place.RegPtsListPlace;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.ui.interfaces.IRegsView;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.user.cellview.client.PageSizePager;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.blsoft.rareacare.client.ui.controls.HomeButton;

public class RegsView extends Composite implements IRegsView, RangeChangeEvent.Handler {
	private IRegsActivity presenter;

	ProvidesKey<RegistryProxy> keyProvider = new ProvidesKey<RegistryProxy>() {
		@Override
		public Object getKey(RegistryProxy item) {
			return (item == null) ? null : item.stableId();
		}
	};

	private static final Binder binder = GWT.create(Binder.class);
	@UiField
	PushButton btnSearch;
	@UiField
	TextBox edSearch;
	@UiField(provided = true)
	DataGrid<RegistryProxy> dataGrid = new DataGrid<RegistryProxy>(keyProvider);
	@UiField PageSizePager pager;
	@UiField HomeButton btnHome;

	interface Binder extends UiBinder<Widget, RegsView> {
	}

	@Override
	public void setPresenter(IRegsActivity presenter) {
		this.presenter = presenter;
		edSearch.setText(null);
		dataGrid.addRangeChangeHandler(this);
		onBtnSearchClick(null);
		return;
	}

	public RegsView() {
		// Dodawanie kolumn do tabeli
		TextColumn<RegistryProxy> colName = new TextColumn<RegistryProxy>() {
			@Override
			public String getValue(RegistryProxy object) {
				return object.getName();
			}
		};
		colName.setSortable(true);
		colName.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(colName, "Nazwa rejestru");

		TextColumn<RegistryProxy> colDescr = new TextColumn<RegistryProxy>() {
			@Override
			public String getValue(RegistryProxy object) {
				return object.getDescr();
			}
		};
		colDescr.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(colDescr, "Opis rejestru");

		TextColumn<RegistryProxy> colOwner = new TextColumn<RegistryProxy>() {
			@Override
			public String getValue(RegistryProxy object) {
				return object.getOwner() !=null ? object.getOwner().getName() + " " + object.getOwner().getFirstName() + " (" + object.getOwner().getLogin() + ")" : null; 
			}
		};
		colOwner.setSortable(true);
		colOwner.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dataGrid.addColumn(colOwner, "Właściciel");

		// Akcja edycji rejestr
		ActionCell<RegistryProxy> colEdit = new ActionCell<RegistryProxy>("Edytuj", new ActionCell.Delegate<RegistryProxy>() {
			@Override
			public void execute(RegistryProxy rg) {
				presenter.goTo(new RegPtsListPlace(rg.getId().toString()));
			}
		});
		Column<RegistryProxy, RegistryProxy> col = new Column<RegistryProxy, RegistryProxy>(colEdit) {
			@Override
			public RegistryProxy getValue(RegistryProxy object) {
				return object;
			}

		};
		dataGrid.addColumn(col);
		dataGrid.setColumnWidth(col, "7EM"); 
		
		// Akcja projektowania rejestru
		ActionCell<RegistryProxy> colDes = new ActionCell<RegistryProxy>("Projektuj", new ActionCell.Delegate<RegistryProxy>() {
			@Override
			public void execute(RegistryProxy rg) {
				presenter.goTo(new RegDsgnPlace(rg.getId().toString()));
			}
		});
		Column<RegistryProxy, RegistryProxy> colDsg = new Column<RegistryProxy, RegistryProxy>(colDes) {
			@Override
			public RegistryProxy getValue(RegistryProxy object) {
				return object;
			}
		};
		dataGrid.addColumn(colDsg);
		dataGrid.setColumnWidth(colDsg, "8EM");
		
		
		// Akcja projektowania rejestru
		ActionCell<RegistryProxy> acProp = new ActionCell<RegistryProxy>("Właściwości", new ActionCell.Delegate<RegistryProxy>() {
			@Override
			public void execute(RegistryProxy rg) {
				presenter.editRegProps(rg);
			}
		});
		Column<RegistryProxy, RegistryProxy> colProp = new Column<RegistryProxy, RegistryProxy>(acProp) {
			@Override
			public RegistryProxy getValue(RegistryProxy object) {
				return object;
			}
		};
		dataGrid.addColumn(colProp);
		dataGrid.setColumnWidth(colProp, "9EM");
		
		initWidget(binder.createAndBindUi(this));
		
		pager.setDisplay(dataGrid);
	}

	@Override
	public void consumeRegList(int startRange, List<RegistryProxy> list) {
		dataGrid.setRowData(startRange, list);
	}

	@Override
	public void consumeRegCount(int count) {
		dataGrid.setRowCount(count);
	}

	@Override
	public void onRangeChange(RangeChangeEvent event) {
		presenter.getRegs(event.getNewRange(), this, edSearch.getText());
	}

	@UiHandler("btnSearch")
	void onBtnSearchClick(ClickEvent event) {
		presenter.getRegCount(this, edSearch.getText());
		dataGrid.setVisibleRangeAndClearData(new Range(0, 25), true);		
	}
	@UiHandler("btnAdd")
	void onBtnAddClick(ClickEvent event) {
		presenter.editRegProps(null);
	}
	@UiHandler("btnHome")
	void onBtnHomeClick(ClickEvent event) {
		Utils.getCF().goTo(new MainPlace());
	}
}
