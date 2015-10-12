package com.blsoft.rareacare.client.ui;

import java.util.EnumSet;



import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Elem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.ElemItem;
import com.blsoft.rareacare.shared.ItemKind;
import com.blsoft.rareacare.shared.RegElemDataType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class RegDocElemDesigner extends DialogBox {

	private static final Binder binder = GWT.create(Binder.class);
	@UiField PushButton btnOK;
	@UiField PushButton btnCancel;
	@UiField TextBox edLabel;
	@UiField FlowPanel itemsPanel;
	@UiField TextBox edId;
	@UiField ScrollPanel itemsScroll;
	@UiField TextBox pedId;
	@UiField LayoutPanel ppnId;
	@UiField TextBox pedLabel;
	@UiField LayoutPanel ppnLabel;
	@UiField TextArea pedDescr;
	@UiField LayoutPanel ppnDescr;
	@UiField ListBox pedKind;
	@UiField LayoutPanel ppnKind;
	@UiField TextBox pedUnit;
	@UiField LayoutPanel ppnUnit;
	@UiField DoubleBox pedNormMin;
	@UiField LayoutPanel ppnNormMin;
	@UiField DoubleBox pedNormMax;
	@UiField LayoutPanel ppnNormMax;
	@UiField TextArea pedNormDescr;
	@UiField LayoutPanel ppnNormDescr;
	@UiField PushButton btnAddItem;
	@UiField Image imgTrash;
	@UiField ListBox pedDataType;
	@UiField LayoutPanel ppnDataType;
	@UiField FlowPanel pnProps;
	@UiField PushButton btDelItem;
	
	@UiField LayoutPanel ppnItems;
	@UiField TextArea pedItems;
	@UiField LayoutPanel ppnLimitToList;
	@UiField CheckBox pcbLimitToList;
	@UiField LayoutPanel ppnRequired;
	@UiField CheckBox pcbRequired;
	@UiField LayoutPanel ppnPlaceHolder;
	@UiField TextBox pedPlaceHolder;
	@UiField LayoutPanel ppnMinMax;
	@UiField DoubleBox pedMin;
	@UiField DoubleBox pedMax;
	@UiField LayoutPanel ppnMaxLen;
	@UiField IntegerBox pedMaxLen;
	@UiField LayoutPanel ppnRegExpr;
	@UiField TextBox pedRegExpr;
	
	final Integer itemHeight = 30;

	private DragInfo latestDraggedItem;
	private ItemHtml selectedItem;
	private Elem orgElem;
	private IDElemConsumer presenter = null;
//	private IRegDsgnActivity presenter;
	static private RegDocElemDesigner regDocElemDesigner;
	
	interface IDElemConsumer {
		void saveElem(RegistryProxy.Elem nw, RegistryProxy.Elem org);
	}

	private class ItemHtml extends HTML implements DragStartHandler, DragOverHandler, DropHandler, DragLeaveHandler, ClickHandler {
		private ElemItem elemItem;

		/** Tworzy nowy item i dodaje do panelu; Jeśli idx<0, to dodaje na końcu */
		ItemHtml(final ElemItem e, int idx) {
			elemItem = e;
			update();
			setWidth("95%");
			setStyleName("gwt-ItemPanel");
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
			if (idx < 0)
				itemsPanel.add(this);
			else
				itemsPanel.insert(this, idx);

			addDragStartHandler(this);
			addDragOverHandler(this);
			addDropHandler(this);
			addDragLeaveHandler(this);
			addClickHandler(this);
		}

		private void setIndex(int idx) {
			int currIdx = itemsPanel.getWidgetIndex(this);
			if (idx != currIdx) {
				// itemsPanel.remove(idx);
				// if (idx > currIdx)
				// idx--;
				itemsPanel.insert(this, idx);
			}
		}

		@Override
		public void onDrop(DropEvent event) {
			getElement().getStyle().clearBackgroundColor();
			latestDraggedItem.Target = this;
		}

		@Override
		public void onDragOver(DragOverEvent event) {
			Style st = getElement().getStyle();
			st.setBackgroundColor("aqua");
		}

		@Override
		public void onDragStart(DragStartEvent event) {
			latestDraggedItem = new DragInfo(elemItem.getKind(), this);
			event.getDataTransfer().setDragImage(this.getElement(), 10, 10);
		}

		@Override
		public void onDragLeave(DragLeaveEvent event) {
			getElement().getStyle().clearBackgroundColor();
		}

		@Override
		public void onClick(ClickEvent event) {
			selectItem(this);
		}

		/** Aktualizacja HTMLa wyświetlanego elementu */
		public void update() {
			switch (elemItem.getKind()) {
			case Separator:
				setHTML("<hr/>");
				break;
			default:
				String s = " <table cellspacing='1' cellpadding='1' >" + "<tr><td colspan='2'><strong><u>" + elemItem.getKind().getDescr()
						+ "</u></strong></td></tr>";
				if (elemItem.getLabel() != null && !elemItem.getLabel().isEmpty())
					s += "<tr><td style='WIDTH: 82px'><strong>Etykieta</strong></td><td>" + elemItem.getLabel() + "</td></tr>";
				if (elemItem.getId() != null && !elemItem.getId().isEmpty())
					s += "<tr><td style='WIDTH: 82px'><strong>ID</strong></td><td>" + elemItem.getId() + "</td></tr>";
				if (elemItem.getUnits() != null && !elemItem.getUnits().isEmpty())
					s += "<tr><td style='WIDTH: 82px'><strong>Jednostka</strong></td><td>" + elemItem.getUnits() + "</td></tr>";
				if (elemItem.getDescr() != null && !elemItem.getDescr().isEmpty())
					s += "<tr><td style='WIDTH: 82px'><strong>Opis</strong></td><td>" + elemItem.getDescr() + "</td></tr>";
				s += "</table>";
				setHTML(s);
				break;
			}

		}
	}

	private class DragInfo {
		ItemKind draggedItemKind = null;
		ItemHtml Source = null;
		ItemHtml Target = null;

		DragInfo(ItemKind kind, ItemHtml src) {
			draggedItemKind = kind;
			Source = src;
		}
	}

	interface Binder extends UiBinder<Widget, RegDocElemDesigner> {
	}

	public RegDocElemDesigner() {
		setText("Edytor elementów rejestru");
		setWidget(binder.createAndBindUi(this));

		// Wlaczam dragowalność narzędzi - nie umiem w template
		setAutoHideOnHistoryEventsEnabled(true);
		setGlassEnabled(true);
		setModal(true);

		for (ItemKind ik : ItemKind.values())
			pedKind.addItem(ik.getDescr());
		for (RegElemDataType tp : RegElemDataType.values())
			pedDataType.addItem(tp.getDescr());

		showItemPanels();

		itemsPanel.addDomHandler(new DragOverHandler() {
			public void onDragOver(DragOverEvent event) {
				// To ponoć trzeba zaimplementować- inaczej czasem nie dziala
				// Ustala kolor scrollera, bo on ma border
				Style st = itemsScroll.getElement().getStyle();
				st.setBorderColor("red");
			}
		}, DragOverEvent.getType());

		itemsPanel.addDomHandler(new DragLeaveHandler() {
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				itemsScroll.getElement().getStyle().clearBorderColor();
			}
		}, DragLeaveEvent.getType());

		itemsPanel.addDomHandler(new DropHandler() {
			public void onDrop(DropEvent event) {
				// Prevent the default text drop.
				event.preventDefault();

				// Un-highlight the name and notes box.
				Style st = itemsScroll.getElement().getStyle();
				st.clearBorderColor();

				// Fill in the form.
				try {
					if (latestDraggedItem != null) {
						if (latestDraggedItem.Source == null) {
							// Nowy
							ElemItem e = (ElemItem) ElemItem.createObject();
							e.setKind(latestDraggedItem.draggedItemKind);
							new ItemHtml(e, -1);
						} else {
							latestDraggedItem.Source.setIndex((latestDraggedItem.Target == null) ? itemsPanel.getWidgetCount()
									: latestDraggedItem.Target.elemItem.getLp());
						}
						latestDraggedItem = null;
						// refreshTreePanel();
					}
				} catch (NumberFormatException e) {
					// The user probably dragged something other than a
					// template.
				}
			}
		}, DropEvent.getType());
	}

	// private void reorderItems() {
	// for (int i = 0; i < itemsPanel.getWidgetCount(); i++) {
	// ItemHtml el = ((ItemHtml) (itemsPanel.getWidget(i)));
	// itemsPanel.setWidgetLeftRight(el, 10, Unit.PX, 10, Unit.PX);
	// itemsPanel.setWidgetTopHeight(el, (itemHeight + 5) * el.elemItem.getLp(),
	// Unit.PX, itemHeight, Unit.PX);
	// }
	// }

	static public RegDocElemDesigner get() {
		if (regDocElemDesigner == null)
			regDocElemDesigner = new RegDocElemDesigner();
		return regDocElemDesigner;
	}

	public void editElem(Elem ee, IDElemConsumer parent) {
		this.presenter  = parent;
		this.orgElem = ee;
		Elem e;
		if (ee == null)
			e = (Elem) Elem.createObject();
		else {
			e = Elem.createFromString(ee.toJson()); // Kopia na potrzeby edycji
													// - by można anulować
		}

		edLabel.setText(e.getLabel());
		edId.setText(e.getId());
		JsArray<ElemItem> ls = e.getItems();
		selectedItem = null;
		itemsPanel.clear();
		if (ls != null) 
			for (int i = 0; i < ls.length(); i++)
				new ItemHtml(ls.get(i), i);
		showItemPanels();
		this.center();
	}

	private void selectItem(ItemHtml itm) {
		selectedItem = itm;
		for (int i = 0; i < itemsPanel.getWidgetCount(); i++)
			if (itemsPanel.getWidget(i) != itm)
				itemsPanel.getWidget(i).removeStyleDependentName("selected");

		if (selectedItem != null) {
			final ElemItem ei = itm.elemItem;
			selectedItem.addStyleDependentName("selected"); // getElement().getStyle().setBackgroundColor("red");
			pedKind.setSelectedIndex((ei.getKind() == null) ? 0 : itm.elemItem.getKind().ordinal());
			pedDataType.setSelectedIndex((ei.getDataType() == null) ? 0 : ei.getDataType().ordinal());
			pedDescr.setText(ei.getDescr());
			pedId.setText(ei.getId());
			pedLabel.setText(ei.getLabel());
			pedNormDescr.setText(ei.getNormDescr());
			pedNormMax.setValue((ei.getNormMax() == null) ? null : Double.valueOf(itm.elemItem.getNormMax()));
			pedNormMin.setValue((ei.getNormMin() == null) ? null : Double.valueOf(itm.elemItem.getNormMin()));
			pedUnit.setText(ei.getUnits());

			pedItems.setText(ei.getItemsAsString());
			pcbLimitToList.setValue(ei.getLimitToList());
			pcbRequired.setValue(ei.getRequired());
			pedPlaceHolder.setText(ei.getPlaceHolder());
			pedMin.setValue((ei.getMin() == null) ? null : Double.valueOf(ei.getMin()));
			pedMax.setValue((ei.getMax() == null) ? null : Double.valueOf(ei.getMax()));
			pedMaxLen.setValue(ei.getMaxLen());
			pedRegExpr.setText(ei.getRegExpr());
		}
		showItemPanels();
	}

	private final EnumSet<ItemKind> EDITORS = EnumSet.of(ItemKind.Edit, ItemKind.Combo, ItemKind.Memo, ItemKind.List, ItemKind.Radios, ItemKind.CheckBox);
	private final EnumSet<ItemKind> LISTHOLDERS = EnumSet.of(ItemKind.Edit, ItemKind.Combo, ItemKind.List, ItemKind.Radios);
	
	private boolean isEditor(ElemItem it) {
		return EDITORS.contains(it.getKind());
	}

	/** Aktualizuje widoczność listy wlaściwości */
	private void showItemPanels() {
		if (selectedItem == null) {
			for (int i = 0; i < pnProps.getWidgetCount(); i++)
				if (pnProps.getWidget(i) instanceof LayoutPanel)
					pnProps.getWidget(i).setVisible(false);
		} else {
			ItemKind kd = selectedItem.elemItem.getKind();
			ppnId.setVisible(isEditor(selectedItem.elemItem) /*|| kd == ItemKind.Header*/);
			ppnKind.setVisible(true);
			ppnDescr.setVisible(isEditor(selectedItem.elemItem) || kd == ItemKind.Comment /*|| kd == ItemKind.Header*/);
			ppnLabel.setVisible(isEditor(selectedItem.elemItem) /*|| kd == ItemKind.Header */ || kd == ItemKind.Comment);
			ppnDataType.setVisible(kd == ItemKind.Edit);
			ppnNormDescr.setVisible(isEditor(selectedItem.elemItem));
			ppnNormMin.setVisible(kd == ItemKind.Edit);
			ppnNormMax.setVisible(kd == ItemKind.Edit);
			ppnUnit.setVisible(isEditor(selectedItem.elemItem));
			
			ppnItems.setVisible(LISTHOLDERS.contains(kd));
			ppnLimitToList.setVisible(kd == ItemKind.Edit);
			ppnRequired.setVisible(isEditor(selectedItem.elemItem));
			ppnPlaceHolder.setVisible(kd == ItemKind.Edit || kd == ItemKind.Memo);
			ppnMinMax.setVisible(kd == ItemKind.Edit);
			ppnMaxLen.setVisible(kd == ItemKind.Edit || kd == ItemKind.Memo);
			ppnRegExpr.setVisible(kd == ItemKind.Edit || kd == ItemKind.Memo);
		}
	}

	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		hide();
	}

	@UiHandler("btnOK")
	void onBtnOKClick(ClickEvent event) {
		
		// Zapis objektu i kontrola danych - tworzę nowy objekt
		Elem e = (Elem) Elem.createObject();
		e.setId(Utils.ChkNotEmpty(edId.getText(), "ID elementu"));
		e.setLabel(Utils.ChkNotEmpty(edLabel.getText(), "Etykieta elementu"));
		int emptyCount = 0;
		if (itemsPanel.getWidgetCount() > 0) {

			// Wypełniam elementy
			JsArray<ElemItem> ls = e.getItems();
			for (int i = 0; i < itemsPanel.getWidgetCount(); i++) {
				ElemItem it = ((ItemHtml) itemsPanel.getWidget(i)).elemItem;
				it.setLp(i);
				// Sprawdzam poprawność pozycji
				if (isEditor(it))
					Utils.ChkNotEmpty(it.getLabel(), null);
				if (isEditor(it) /*|| it.getKind() == ItemKind.Header*/) {
					if (it.getId()==null || it.getId().isEmpty())
						if (emptyCount==0)
							emptyCount++;
						else {
							Utils.getCF().showError("Pole ID elementu może być puste tylko dla jednego elementu");
						}
					ls.push(it);
				}
			}
			// Czy ID są unikatowe?
			for(int i=0;i<ls.length();i++)
				for(int j=0;j<ls.length();j++)
					if (ls.get(i).getId().equals(ls.get(j).getId()) && i!=j) {
						Utils.getCF().showError("Wartości ID w ramach elememtu muszą byc unikatowe");
						return;
					}
						
		} else {
			Utils.getCF().showError("Element musi mieć choć jedną pozycję");
			return;
		}
		presenter.saveElem(e, orgElem);
//		Utils.getCF().getEventBus().fireEvent(new DocElemEditedEvent(e, orgElem));
		hide();
	}

	@UiHandler("btnAddItem")
	void onBtnAddItemClick(ClickEvent event) {
		ElemItem e = (ElemItem) ElemItem.createObject();
		e.setKind(ItemKind.Edit);
		e.setLabel("- nowy element -");
		selectItem(new ItemHtml(e, -1));
	}

	@UiHandler("imgTrash")
	void onImgTrashDragOver(DragOverEvent event) {
		imgTrash.getElement().getStyle().setBackgroundColor("aqua");
	}

	@UiHandler("imgTrash")
	void onImgTrashDragLeave(DragLeaveEvent event) {
		imgTrash.getElement().getStyle().clearBackgroundColor();
	}

	@UiHandler("imgTrash")
	void onImgTrashDrop(DropEvent event) {
		if (latestDraggedItem != null && latestDraggedItem.Source != null) {
			itemsPanel.remove(latestDraggedItem.Source);
			if (latestDraggedItem.Source.equals(selectedItem))
				selectItem(null);
		}
	}

	private void saveDataToObject() {
		ElemItem ei = selectedItem.elemItem;
		ei.setId(pedId.getText());
		ei.setKind(ItemKind.values()[pedKind.getSelectedIndex()]);
		ei.setDataType((ppnDataType.isVisible()) ? RegElemDataType.values()[pedDataType.getSelectedIndex()] : null);
		ei.setDescr((ppnDescr.isVisible()) ? pedDescr.getText() : null);
		ei.setLabel((ppnLabel.isVisible()) ? pedLabel.getText() : null);
		ei.setNormDescr((ppnNormDescr.isVisible()) ? pedNormDescr.getText() : null);
		ei.setNormMax((ppnNormMax.isVisible()) ? Utils.ChkNumeric(pedNormMax.getText(), "Norma max") : null);
		ei.setNormMin((ppnNormMax.isVisible()) ? Utils.ChkNumeric(pedNormMin.getText(), "Norma min") : null);
		ei.setUnits((ppnUnit.isVisible()) ? pedUnit.getText() : null);

		ei.setItemsAsString(ppnItems.isVisible() ? pedItems.getText() : null);
		ei.setLimitToList(ppnLimitToList.isVisible() && pcbLimitToList.getValue());
		ei.setRequired(ppnRequired.isVisible() && pcbRequired.getValue());
		ei.setPlaceHolder((ppnPlaceHolder.isVisible()) ? pedPlaceHolder.getText() : null);
		ei.setMax((ppnMinMax.isVisible()) ? Utils.ChkNumeric(pedMax.getText(), "Maksymalna wartość") : null);
		ei.setMin((ppnMinMax.isVisible()) ? Utils.ChkNumeric(pedMin.getText(), "Minimalna wartość") : null);
		ei.setMaxLen((ppnMaxLen.isVisible()) ? Integer.valueOf(Utils.ChkInteger(pedMaxLen.getText(), "Maksymalna długość")) : 0);
		ei.setRegExpr((ppnRegExpr.isVisible()) ? pedRegExpr.getText() : "");
		
		selectedItem.update();
		showItemPanels();
	}

	@UiHandler("pedKind")
	void onPedKindChange(ChangeEvent event) {
		saveDataToObject();
	}

	@UiHandler("pedId")
	void onPedIdValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	@UiHandler("pedLabel")
	void onPedLabelValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	@UiHandler("pedDescr")
	void onPedDescrValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	@UiHandler("pedUnit")
	void onPedUnitValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	@UiHandler("pedNormMin")
	void onPedNormMinValueChange(ValueChangeEvent<Double> event) {
		saveDataToObject();
	}

	@UiHandler("pedNormMax")
	void onPedNormMaxValueChange(ValueChangeEvent<Double> event) {
		saveDataToObject();
	}

	@UiHandler("pedNormDescr")
	void onPedNormDescrValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	@UiHandler("btDelItem")
	void onBtDelItemClick(ClickEvent event) {
		if (selectedItem != null) {
			itemsPanel.remove(selectedItem);
			selectItem(null);
		}
	}
	
	@UiHandler("pedDataType")
	void onPedDataTypeChange(ChangeEvent event) {
		saveDataToObject();
	}
	@UiHandler("pedItems")
	void onPedItemsValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}
	@UiHandler("pcbLimitToList")
	void onPcbLimitToListValueChange(ValueChangeEvent<Boolean> event) {
		saveDataToObject();
	}
	
	
	@UiHandler("pcbRequired")
	void onPcbRequiredValueChange(ValueChangeEvent <Boolean> event) {
		saveDataToObject();
	}
	@UiHandler("pedPlaceHolder")
	void onPedPlaceHolderValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}
	@UiHandler("pedMin")
	void onPedMinValueChange(ValueChangeEvent<Double> event) {
		saveDataToObject();
	}
	@UiHandler("pedMax")
	void onPedMaxValueChange(ValueChangeEvent<Double> event) {
		saveDataToObject();
	}
	@UiHandler("pedMaxLen")
	void onPedMaxLenValueChange(ValueChangeEvent<Integer>event) {
		saveDataToObject();
	}
	@UiHandler("pedRegExpr")
	void onPedRegExprValueChange(ValueChangeEvent<String>event) {
		saveDataToObject();
	}
}
