package com.blsoft.rareacare.client.ui;

import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.Handler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IRegPtsListActivity;
import com.blsoft.rareacare.client.place.MainPlace;
import com.blsoft.rareacare.client.place.RegEditPlace;
import com.blsoft.rareacare.client.place.RegsPlace;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.DocumentProxy;
import com.blsoft.rareacare.client.requestfactory.DocumentDataRequest;
import com.blsoft.rareacare.client.requestfactory.FactProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocTreeItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.blsoft.rareacare.client.ui.controls.BackButton;
import com.blsoft.rareacare.client.ui.controls.HomeButton;
import com.blsoft.rareacare.client.ui.interfaces.IRegPtsList;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.event.dom.client.KeyPressEvent;

public class RegPtsList extends Composite implements IRegPtsList, RangeChangeEvent.Handler {

	private IRegPtsListActivity presenter;

	ProvidesKey<DocumentProxy> keyProvider = new ProvidesKey<DocumentProxy>() {
		@Override
		public Object getKey(DocumentProxy item) {
			return (item == null) ? null : item.stableId();
		}
	};

	private static final Binder binder = GWT.create(Binder.class);
	@UiField TextBox edSearch;
	@UiField BLButton btnSearch;
	@UiField BLButton btnAdd;
	@UiField BLButton btnExport;
	@UiField HomeButton btnHome;
	@UiField BackButton btnBack;
	@UiField(provided = true) DataGrid<DocumentProxy> dataGrid = new DataGrid<DocumentProxy>(keyProvider);
	@UiField(provided = true) SimplePager pager;

	private RegistryProxy reg;

	private RareACareDef regDef;

	private String sortByColumnName = "";
	private Boolean sortByAscending = true;

	interface Binder extends UiBinder<Widget, RegPtsList> {
	}

	public RegPtsList() {

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);

		initWidget(binder.createAndBindUi(this));
		dataGrid.addRangeChangeHandler(this);
		dataGrid.addColumnSortHandler(new Handler() {

			@Override
			public void onColumnSort(ColumnSortEvent event) {
				Column<?, ?> c = event.getColumn();
				if (c instanceof RootTextColumn)
					sortBy(((RootTextColumn) c).colName, event.isSortAscending());

			}
		});
		pager.setDisplay(dataGrid);
	}

	protected void sortBy(String colName, Boolean ascending) {
		sortByColumnName = colName;
		sortByAscending = ascending;
		refreshList(false);
	}

	@Override
	public void setPresenter(final IRegPtsListActivity presenter) {
		this.presenter = presenter;
		edSearch.setText(null);

		// Inicjowanie operacji dodawania do cache definicji rejestru
		setReg(Utils.getRegDefFromCache(presenter.getPlace().getRegId()));
		if (reg == null) {

			RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
			req.findRegistry(presenter.getPlace().getRegId()).with("currRegUser", "currRegUser.user", "owner").fire(
					new CommonReceiver<RegistryProxy>() {
						public void onSuccess(RegistryProxy u) {
							Utils.setRegDefToCache(presenter.getPlace().getRegId(), u);
							setReg(u);
							initGridCols();
							enableControls();
						}
					});
		}
		else {
			initGridCols();
			enableControls();
		}
	}

	private void setReg(RegistryProxy reg) {
		this.reg = reg;
		if (reg != null)
			regDef = ((RareACareDef) RareACareDef.createFromString(reg.getDef()));
		else
			regDef = null;
	}

	public class RootTextColumn extends TextColumn<DocumentProxy> {

		private String colName;
		private String path;

		public RootTextColumn(String name) {
			String[] f = Utils.parseFieldPath(name);
			colName = f[1];
			path = f[0];
		}

		@Override
		public String getValue(DocumentProxy object) {
			for (FactProxy f : object.getFacts()) {
				String pth = Utils.coalesce(f.getPath());
				if (pth.equals(path)) {
					JSONObject js = JSONParser.parseStrict(f.getContent()).isObject();
					if (js != null && js.containsKey(colName))
						return js.get(colName).isString().stringValue(); // Prawidłowe
																			// wyjście
																			// z
																			// funkcji
				}
			}
			return null;
		}
	}

	/** Inicjuje kolumny na podstawie definicji rejestru i dokumentu */
	protected void initGridCols() {

		sortByAscending = true;
		sortByColumnName = "";

		// Usuwam istniejące kolumny
		for (int i = dataGrid.getColumnCount() - 1; i >= 0; i--)
			dataGrid.removeColumn(i);

		// Budowanie kolumn
		if (regDef != null) {
			DocTreeItem root = regDef.getDocTree().get(0);
			if (root != null) {
				Doc rootDoc = regDef.getDoc(root.getId());
				if (rootDoc != null) {
					JsArrayString flds = root.getInfoFields(rootDoc);
					for (int i = 0; i < flds.length(); i++) {
						String fn = flds.get(i);
						if (i == 0)
							sortByColumnName = fn;
						RootTextColumn col = new RootTextColumn(fn);
						col.setSortable(true);
						col.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
						dataGrid.addColumn(col, fn);

					}
					// Akcja projektowania rejestru
					ActionCell<DocumentProxy> acProp = new ActionCell<DocumentProxy>("Otwórz", new ActionCell.Delegate<DocumentProxy>() {
						@Override
						public void execute(DocumentProxy rg) {
							Utils.getCF().goTo(new RegEditPlace(presenter.getPlace().getRegPtsListName() + "_" + rg.getId().toString()));
						}
					});
					Column<DocumentProxy, DocumentProxy> colProp = new Column<DocumentProxy, DocumentProxy>(acProp) {
						@Override
						public DocumentProxy getValue(DocumentProxy object) {
							return object;
						}
					};
					dataGrid.addColumn(colProp);
					dataGrid.setColumnWidth(colProp, "7EM");
				}
			}
		}

		// Odczyt danych
		refreshList(true);
	}

	protected void enableControls() {
		btnAdd.setEnabled((Utils.getRegDefFromCache(presenter.getPlace().getRegId()) != null));
	}

	@Override
	public void onRangeChange(final RangeChangeEvent event) {
		final Range newRange = event.getNewRange();

		if (regDef != null) {
			DocTreeItem dt = regDef.getDocTree().get(0);
			String flds = dt.getInfoFieldsAsString(regDef.getDoc(dt.getId()));

			DocumentDataRequest req = Utils.getCF().getRequestFactory().documentDataRequest();
			req.getRootDocs(presenter.getPlace().getRegId(),
					newRange.getStart(),
					newRange.getLength(),
					edSearch.getText(),
					sortByColumnName,
					sortByAscending,
					flds
					).with("facts").fire(
							new CommonReceiver<List<DocumentProxy>>() {
								public void onSuccess(List<DocumentProxy> u) {
									dataGrid.setRowData(newRange.getStart(), u);
								}
							});
		}
	}

	@UiHandler("btnSearch")
	void onBtnSearchClick(ClickEvent event) {
		refreshList(true);
	}

	private void refreshList(Boolean withRowCount) {
		final Range r = dataGrid.getVisibleRange();
		if (!withRowCount) {
			dataGrid.setVisibleRangeAndClearData(r, true);
		} else if (regDef != null) {

			DocTreeItem dt = regDef.getDocTree().get(0);
			String flds = dt.getInfoFieldsAsString(regDef.getDoc(dt.getId()));
			DocumentDataRequest req = Utils.getCF().getRequestFactory().documentDataRequest();
			req.getRootDocCount(presenter.getPlace().getRegId(), edSearch.getText(), flds).fire(new CommonReceiver<Integer>() {
				@Override
				public void onSuccess(Integer response) {
					dataGrid.setRowCount(response);
					if (response < r.getStart())
						dataGrid.setVisibleRangeAndClearData(new Range(0, 25), true);
					else
						dataGrid.setVisibleRangeAndClearData(r, true); // TODO
					// - poprawić range - może na TRUE, ale wówczas wiele razy
					// odpytywalo
					// serwer
				}
			});
		}
	}

	@UiHandler("btnHome")
	void onBtnHomeClick(ClickEvent event) {
		Utils.getCF().goTo(new MainPlace());
	}

	@UiHandler("btnBack")
	void onBtnBackClick(ClickEvent event) {
		Utils.getCF().goTo(new RegsPlace());
	}

	@UiHandler("btnAdd")
	void onBtnAddClick(ClickEvent event) {
		Utils.getCF().goTo(new RegEditPlace(presenter.getPlace().getRegPtsListName() + "_0"));
	}

	@UiHandler("btnExport")
	void onBtnExportClick(ClickEvent event) {
		String link = GWT.getModuleBaseURL() + "exportfiledownload?regid=" + reg.getId();
		Window.open(link, "", "");
	}

	@UiHandler("edSearch")
	void onEdSearchKeyPress(KeyPressEvent event) {
		char c = event.getCharCode();
		if (c == 13) // Enter
			refreshList(true);
	}
}
