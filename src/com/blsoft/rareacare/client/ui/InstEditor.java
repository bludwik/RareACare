package com.blsoft.rareacare.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.cellview.client.CellList;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.AddressProxy;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.InstitutionDataRequest;
import com.blsoft.rareacare.client.requestfactory.InstitutionProxy;
import com.blsoft.rareacare.client.ui.interfaces.IInstConsumer;
import com.blsoft.rareacare.client.ui.interfaces.IInstEditor;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.web.bindery.requestfactory.gwt.client.RequestFactoryEditorDriver;

public class InstEditor extends DialogBox implements Editor<InstitutionProxy>, SelectionChangeEvent.Handler, ClickHandler, IInstEditor {
	SingleSelectionModel<InstitutionProxy> selectionModel;
	ListDataProvider<InstitutionProxy> dataProvider = new ListDataProvider<InstitutionProxy>();

	private HorizontalPanel horizontalPanel;
	private DecoratorPanel decoratorPanel;
	private FlexTable flexTable;
	private Label label;
	TextBox nameEditor;
	private Label label_1;
	TextBox telephoneEditor;
	private Label label_2;
	TextBox emailEditor;
	@Path("address")
	AddressEditor addressEditor;
	private VerticalPanel verticalPanel;
	private CellList<InstitutionProxy> cellList;
	private HorizontalPanel horizontalPanel_1;
	private PushButton btnAdd;
	private PushButton btnDelete;
	private PushButton btnOK;
	private PushButton btnSave;
	private Driver instEditorDriver;
	private boolean driverInitiated = false;
	private IInstConsumer consumer;
	private PushButton btnClose;
	private List<InstitutionProxy> instLst;

	public interface Driver extends RequestFactoryEditorDriver<InstitutionProxy, InstEditor> {
	}

	static class InstCell extends AbstractCell<InstitutionProxy> {

		@Override
		public void render(Context context, InstitutionProxy value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the name and address.
			sb.appendHtmlConstant("<td style='font-size:95%;'>");
			sb.appendEscaped(value.getName());
			sb.appendHtmlConstant("</td><tr><td style='font-size:80%;'>");
			AddressProxy adr = value.getAddress();
			if (adr != null) {
				sb.appendEscaped(adr.getCity() + ", " + adr.getStreet() + " " + adr.getStr_no());
			}
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}

	public InstEditor() {
		setGlassEnabled(true);
		setAutoHideOnHistoryEventsEnabled(true);

		ProvidesKey<InstitutionProxy> keyProvider = new ProvidesKey<InstitutionProxy>() {
			@Override
			public Object getKey(InstitutionProxy item) {
				return item == null ? null : item.stableId();
			}
		};

		horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		setWidget(horizontalPanel);
		horizontalPanel.setSize("100%", "29EM");

		verticalPanel = new VerticalPanel();
		verticalPanel.setSpacing(5);
		horizontalPanel.add(verticalPanel);
		verticalPanel.setHeight("100%");

		InstCell instCell = new InstCell();
		cellList = new CellList<InstitutionProxy>(instCell, keyProvider);
		cellList.setPageSize(30);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		selectionModel = new SingleSelectionModel<InstitutionProxy>(keyProvider);
		cellList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(this);
		
		ScrollPanel scrlPanel = new ScrollPanel();
		scrlPanel.setStyleName("scrollPanel");
		verticalPanel.add(scrlPanel);
		scrlPanel.setSize("15EM", "26EM");
		verticalPanel.setCellHeight(cellList, "24EM");
		scrlPanel.add(cellList);
		cellList.setWidth("13EM");
		// Connect the list to the data provider.
		dataProvider.addDataDisplay(cellList);

		horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setSpacing(5);
		verticalPanel.add(horizontalPanel_1);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_1, HasVerticalAlignment.ALIGN_BOTTOM);
		horizontalPanel_1.setWidth("100%");

		btnAdd = new PushButton("Dodaj");
		btnAdd.addClickHandler(this);
		horizontalPanel_1.add(btnAdd);
		horizontalPanel_1.setCellHorizontalAlignment(btnAdd, HasHorizontalAlignment.ALIGN_CENTER);

		btnDelete = new PushButton("Usuń");
		btnDelete.addClickHandler(this);
		horizontalPanel_1.add(btnDelete);
		horizontalPanel_1.setCellHorizontalAlignment(btnDelete, HasHorizontalAlignment.ALIGN_CENTER);

		decoratorPanel = new DecoratorPanel();
		horizontalPanel.add(decoratorPanel);
		decoratorPanel.setSize("100%", "");

		flexTable = new FlexTable();
		flexTable.setCellSpacing(5);
		flexTable.setCellPadding(5);
		decoratorPanel.setWidget(flexTable);

		label = new Label("Nazwa");
		flexTable.setWidget(0, 0, label);

		nameEditor = new TextBox();
		flexTable.setWidget(0, 1, nameEditor);
		nameEditor.setWidth("30EM");

		btnOK = new PushButton("OK");
		btnOK.setTabIndex(20);
		btnOK.addClickHandler(this);
		flexTable.setWidget(0, 2, btnOK);
		btnOK.setSize("6EM", "");

		label_1 = new Label("Telefon");
		flexTable.setWidget(1, 0, label_1);

		telephoneEditor = new TextBox();
		flexTable.setWidget(1, 1, telephoneEditor);
		telephoneEditor.setWidth("30EM");

		btnClose = new PushButton("Zamknij");
		btnClose.setTabIndex(21);
		btnClose.addClickHandler(this);
		flexTable.setWidget(1, 2, btnClose);
		btnClose.setSize("6EM", "");

		label_2 = new Label("e-mail");
		flexTable.setWidget(2, 0, label_2);

		emailEditor = new TextBox();
		flexTable.setWidget(2, 1, emailEditor);
		emailEditor.setWidth("30EM");

		btnSave = new PushButton("Zapisz");
		btnSave.setTabIndex(22);
		btnSave.addClickHandler(this);
		btnSave.setHTML("Zapisz");
		flexTable.setWidget(2, 2, btnSave);
		btnSave.setSize("6EM", "");

		addressEditor = new AddressEditor();
		flexTable.setWidget(3, 0, addressEditor);
		flexTable.getFlexCellFormatter().setColSpan(3, 0, 3);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter().setHorizontalAlignment(2, 2, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getCellFormatter().setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_CENTER);
		FlexTableHelper.fixRowSpan(flexTable);
	}

	public void editInst(IInstConsumer consumer) {
		this.consumer = consumer;
		if (consumer != null)
			btnOK.setText("Wstaw");
		else
			btnOK.setText("OK");
		driverInitiated = false;
		if (instEditorDriver == null)
			instEditorDriver = GWT.create(Driver.class);
		// Initialize the driver with the top-level editor
		instEditorDriver.initialize(Utils.getCF().getRequestFactory(), this);

		InstitutionDataRequest req = Utils.getCF().getRequestFactory().institutionDataRequest();
		req.getInstitutions("", 0, -1).with(instEditorDriver.getPaths()).fire(new CommonReceiver<List<InstitutionProxy>>() {
			@Override
			public void onSuccess(List<InstitutionProxy> response) {
				instLst = response;
				if (instLst.size() > 0)
					selectionModel.setSelected(instLst.get(0), true);
				else
					selectionModel.setSelected(null, false);
				dataProvider.setList(instLst);
			}
		});

		this.center();
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent event) {
		save(true);
		setEditors(selectionModel.getSelectedObject());
	}

	private void setEditors(InstitutionProxy px) {
		btnAdd.setEnabled(px != null);
		InstitutionDataRequest req = Utils.getCF().getRequestFactory().institutionDataRequest();
		if (px == null)
			px = req.create(InstitutionProxy.class);
		if (px.getAddress() == null) {
			px = req.edit(px);
			px.setAddress(req.create(AddressProxy.class)); // Muszę utworzyć, bo
		}													// inaczej nie
															// zapisywal danych
															// z adresu
		instEditorDriver.edit(px, req);
		driverInitiated = true;
		// Określam wywoanie do serwera, ale jeszcze nie wywouję - to się
		// odbędzie w save()
		req.persistAndLoad(px, false).with("address").to(new CommonReceiver<InstitutionProxy>() {
			@Override
			public void onSuccess(InstitutionProxy response) {
				driverInitiated = false;
				if (Utils.replaceOrAddEntity(dataProvider.getList(), response) == null)
					selectionModel.setSelected(response, true);
			}
		});
	}

	private void addInst() {
		//setEditors(null);
		selectionModel.setSelected(selectionModel.getSelectedObject(), false);
		// dataProvider.refresh();
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == btnAdd)
			addInst();
		else if (event.getSource() == btnOK) {
			save(true);
			if (consumer != null)
				consumer.setInst(selectionModel.getSelectedObject());
			hide();
		} else if (event.getSource() == btnClose) {
			save(true);
			hide();
		} else if (event.getSource() == btnSave) {
			save(false);
		} else if (event.getSource() == btnDelete) {
			final InstitutionDataRequest req = Utils.getCF().getRequestFactory().institutionDataRequest();
			final InstitutionProxy p = selectionModel.getSelectedObject();
			if (p != null && Utils.getCF().confirm("Czy jesteś pewien, że chcesz usunąć wskazany ośrodek?")) {
				req.remove(p).fire(new CommonReceiver<Void>() {
					@Override
					public void onSuccess(Void response) {
						dataProvider.getList().remove(p);
						dataProvider.refresh();
						selectionModel.setSelected(null, false); 
					}
				});
			}
		}
	}

	/**
	 * Zapisuje dane, jeśli jest coś do zapisania
	 * 
	 * @param ask
	 *            - gdy TRUE, to po sprawdzeniu, że jest coś do zapisania, pyta
	 *            czy zapisać.
	 */
	private void save(boolean ask) {
		if (driverInitiated) {
			if (instEditorDriver.isDirty() && (!ask || Utils.getCF().confirm("Nie zapisano danych. Czy chcesz je zpisać?"))) {
				InstitutionDataRequest r = (InstitutionDataRequest) instEditorDriver.flush();
				r.fire();
			}
		}
	}
}
