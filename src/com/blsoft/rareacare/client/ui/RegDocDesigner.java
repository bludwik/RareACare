package com.blsoft.rareacare.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.blsoft.rareacare.client.FadeAnimation;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IRegDsgnActivity;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Elem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.ElemItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.blsoft.rareacare.client.ui.RegDocElemDesigner.IDElemConsumer;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.blsoft.rareacare.shared.ElemKind;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.InlineHTML;

public class RegDocDesigner extends Composite implements DragOverHandler, DropHandler, DragLeaveHandler, IDElemConsumer {

	static private RegDocDesigner editor = null;

	private Widget caller;
	private static final Binder binder = GWT.create(Binder.class);

	private class DragInfo {
		Widget draggedControl = null; // Kontrolka

		DragInfo(Widget obj) {
			draggedControl = obj;
		}

		boolean isElem() {
			return draggedControl != null && (draggedControl instanceof DraggableItem)
					&& ((DraggableItem) draggedControl).kind == ElemKind.Elem;
		}

		boolean isConrol() {
			return draggedControl != null && (draggedControl instanceof DraggableItem) && !isElem();
		}

		boolean isTreeLeaf() {
			return draggedControl != null && (draggedControl instanceof ElemDesItem);
		}

		ElemKind getKind() {
			if (draggedControl != null)
				if (draggedControl instanceof DraggableItem)
					return ((DraggableItem) draggedControl).kind;
				else if (draggedControl instanceof ElemDesItem)
					return ((ElemDesItem) draggedControl).itm.getKind();
			return null;
		}

	}

	@UiField PushButton btnCancel;
	@UiField PushButton btnOk;
	@UiField FlowPanel pnControls;
	@UiField FlowPanel elemTreePanel;
	@UiField ScrollPanel scrollDocTreePanel;
	@UiField Image imgTrash;
	@UiField LayoutPanel ppnKind;
	@UiField LayoutPanel ppnId;
	@UiField Label pedKind;
	@UiField TextBox pedId;
	@UiField LayoutPanel ppnLabel;
	@UiField TextBox pedLabel;
	@UiField TextArea pedDescr;
	@UiField LayoutPanel ppnDescr;
	@UiField LayoutPanel ppnRepeatMin;
	@UiField IntegerBox pedRepeatMin;
	@UiField LayoutPanel ppnRepeatMax;
	@UiField IntegerBox pedRepeatMax;
	@UiField ListBox pedPK;
	@UiField LayoutPanel ppnPK;
	@UiField FlowPanel ppnProps;
	@UiField InlineHTML lbCaption;
	@UiField PushButton btnProps;
	@UiField PushButton btnPropsEx;
	@UiField FlowPanel pnElems;
	@UiField PushButton btnDocPrv;
	@UiField BLButton btnRep;
	@UiField BLButton btnAdd;
	@UiField BLButton btnDeleteElem;
	@UiField BLButton btnOpenElem;
	@UiField BLButton btnExportElem;

	DraggableItem btnSeparator = new DraggableItem(ElemKind.Separator);
	DraggableItem btnComment = new DraggableItem(ElemKind.Comment);
	DraggableItem btnNewPage = new DraggableItem(ElemKind.PageBreak);
	DraggableItem btnHeader = new DraggableItem(ElemKind.Header);
	HTML dragPointer = new HTML();

	/** Klasa elementów w toolboxe */
	public class DraggableItem extends Composite implements DragStartHandler, DoubleClickHandler, ClickHandler {

		private Elem elem;
		private ElemKind kind;
		FlowPanel mainPanel;
		HTML mainHtml;

		public DraggableItem() {
			initItem();
			// mainPanel.getElement().setDraggable(Element.DRAGGABLE_TRUE);

		}

		void initItem() {
			mainPanel = new FlowPanel();
			mainPanel.setStyleName("gwt-ItemPanel");
			initWidget(mainPanel);
//			mainPanel.setWidth("100%");
			// mainPanel.getElement().getStyle().setMarginLeft(50, Unit.PX);

			mainHtml = new HTML("New HTML", true);
			mainPanel.add(mainHtml);
//			mainHtml.setWidth("100%");
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
			addDomHandler(this, DragStartEvent.getType());
			addDomHandler(this, DoubleClickEvent.getType());
			addDomHandler(this, ClickEvent.getType());
		}

		public DraggableItem(ElemKind kind) {
			initItem();
			setKind(kind);
		}

		@Override
		public void onDragStart(DragStartEvent event) {
			latestDraggedItem = new DragInfo(this); // getIndex();
			// event.getDataTransfer().setDragImage(this.getElement(), 10, 10);
			event.setData("text", "test");
			event.stopPropagation();
			// event.preventDefault();
		}

		public ElemKind getKind() {
			return kind;
		}

		public void setKind(ElemKind kind) {
			this.kind = kind;
			if (this.kind == ElemKind.Elem && elem != null)
				mainHtml.setHTML("<div class='gwt-ItemPanel-intern'>" + Utils.escapeHTML(elem.getLabel()) + "</div>");
			else
				mainHtml.setHTML("<div class='gwt-ItemPanel-intern'>" + Utils.escapeHTML(kind.getDescr()) + "</div>");
		}

		public Elem getElem() {
			return elem;
		}

		public void setElem(Elem elem) {
			this.elem = elem;
			setKind(ElemKind.Elem);
		}

		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			if (getKind() == ElemKind.Elem)
				RegDocElemDesigner.get().editElem(elem, RegDocDesigner.this);
		}

		@Override
		public void onClick(ClickEvent event) {
			selectItem(this);
		}
	}

	public DragInfo latestDraggedItem = null;

	private IRegDsgnActivity presenter;

	public ElemDesItem selectedElem;

	private Doc currentDoc;

	private DraggableItem selectedItem = null;

	/** Klasa elementów będących w okienku edycyjnym bydujących drzewo */
	class ElemDesItem extends Composite implements DragStartHandler, DragOverHandler, DropHandler, DragLeaveHandler, ClickHandler {
		private DocItem itm;
		private FlowPanel mainPanel;
		private HTML lbInfo;

		// Budowanie widgeta
		public void initElem(FlowPanel panel, DocItem it) {
			mainPanel = new FlowPanel();
			mainPanel.setStyleName("gwt-ItemPanel");
			initWidget(mainPanel);
//			mainPanel.setWidth("100%");
			// mainPanel.getElement().getStyle().setMarginLeft(50, Unit.PX);

			lbInfo = new HTML("New HTML", true);
			mainPanel.add(lbInfo);
//			lbInfo.setWidth("100%");

			// Logika
			itm = it;

			// Inicjacja danych
			if (itm.getKind() == ElemKind.Elem) {
				Elem elem = getElemById(itm.getId());
				assert elem != null;
				itm.setId(elem.getId());
				if (itm.getLabel().isEmpty())
					itm.setLabel(elem.getLabel());
			}

			getElement().setDraggable(Element.DRAGGABLE_TRUE);

			if (panel == null)
				putElemOnPos(this);
			else
				panel.add(this);

			mainPanel.addDomHandler(this, DragStartEvent.getType());
			mainPanel.addDomHandler(this, DragOverEvent.getType());
			mainPanel.addDomHandler(this, DropEvent.getType());
			mainPanel.addDomHandler(this, DragLeaveEvent.getType());
			mainPanel.addDomHandler(this, ClickEvent.getType());

			// Inicjacja kontrolek w ramach elementu
			switch (itm.getKind()) {
			case Separator:
				setStyleName("gwt-Separator");
				lbInfo.setText(null);
				break;
			default:
				break;
			}

			update();
		};

		public ElemDesItem(FlowPanel panel, DocItem it) {
			initElem(panel, it);
		}

		public ElemDesItem(DragInfo di) {

			// Logika
			DocItem it = (DocItem) DocItem.createObject();

			assert di.isConrol() || di.isElem();

			if (di.isElem()) {
				Elem e = ((DraggableItem) di.draggedControl).getElem();
				it.setKind(ElemKind.Elem);
				it.setId(e.getId());
				it.setLabel(e.getLabel());

			} else if (di.isConrol()) {
				if (di.draggedControl == btnComment)
					it.setKind(ElemKind.Comment);
				else if (di.draggedControl == btnHeader)
					it.setKind(ElemKind.Header);
				else if (di.draggedControl == btnNewPage)
					it.setKind(ElemKind.PageBreak);
				else if (di.draggedControl == btnSeparator)
					it.setKind(ElemKind.Separator);

			}

			initElem(null, it);
		}

		@Override
		public void onClick(ClickEvent event) {
			selectItem();
			event.stopPropagation();
		}

		private void selectItem() {
			if (selectedElem != null)
				selectedElem.removeStyleDependentName("selected");
			selectedElem = this;
			addStyleDependentName("selected");

			if (selectedElem != null) {
				pedKind.setText(itm.getKind().getDescr());
				pedId.setText(itm.getId());
				pedDescr.setText(itm.getDescr());
				pedLabel.setText(itm.getLabel());
				pedRepeatMin.setValue(Integer.valueOf(itm.getRepeatMin()));
				pedRepeatMax.setValue(Integer.valueOf(itm.getRepeatMax()));

				// Budowanie listy pól dla klucza głównego; Dodaję jedynie pola z zerowego zagłębienia.
				pedPK.clear();
				pedPK.addItem(" - brak -", "");
				List<String> ls = presenter.getFieldListOfItems(itm.getItems());
				for (int i = 0; i < ls.size(); i++) {
					String s = ls.get(i);
					if (!s.isEmpty() && s.charAt(0) == '|' && s.contains("/")) {
						s = s.substring(1);
						pedPK.addItem(s);
					}
				}
				

				pedPK.setSelectedIndex(0);
				String pk = itm.getPrimaryKey();
				for (int i = 0; i < pedPK.getItemCount(); i++)
					if (pedPK.getValue(i).equals(pk)) {
						pedPK.setSelectedIndex(i);
						break;
					}
				if (pk != null && !pk.isEmpty() && pedPK.getSelectedIndex() == 0) 
					Utils.showMessage("Zapamiętana wartość klucza głównego: " + pk + " nie jest prawidłowa. Wybierz nową, prawidłową wartość z listy dostępnych pól");

			}
			showItemPanels();

		}

		@Override
		public void onDragLeave(DragLeaveEvent event) {
			event.stopPropagation();
			event.preventDefault();
			removeStyleName("gwt-ItemDropTarget");
		}

		@Override
		public void onDrop(DropEvent event) {
			removeStyleName("gwt-ItemDropTarget");
			event.preventDefault();
			event.stopPropagation();
			doDrop();
		}

		@Override
		public void onDragOver(DragOverEvent event) {
			event.stopPropagation();
			showDragPointer(this, event.getNativeEvent());
			// if (itm.getKind() == ElemKind.Header) {
			// addStyleName("gwt-ItemDropTarget");
			// hideDragPointer();
			// } else {
			// showDragPointer(this);
			// }
		}

		@Override
		public void onDragStart(DragStartEvent event) {
			latestDraggedItem = new DragInfo(this);
			event.stopPropagation();
			// event.preventDefault();
			// event.getDataTransfer().setDragImage(this.getElement(), 10, 10);
			event.setData("text", "test");
		}

		/** Aktualizacja wyglądu elementu na podstawie definicji */
		public void update() {
			if (itm.getKind() != ElemKind.Separator) {
				String s = " <table cellspacing='1' cellpadding='1' >" + "<tr><td colspan='2'><strong><u>" + Utils.escapeHTML(itm.getKind().getDescr())
						+ "</u></strong></td></tr>";
				if (itm.getLabel() != null && !itm.getLabel().isEmpty())
					s += "<tr><td style='WIDTH: 82px'><strong>Etykieta</strong></td><td>" + Utils.escapeHTML(itm.getLabel()) + "</td></tr>";
				if (itm.getId() != null && !itm.getId().isEmpty())
					s += "<tr><td style='WIDTH: 82px'><strong>ID</strong></td><td>" + Utils.escapeHTML(itm.getId()) + "</td></tr>";
				if (itm.getDescr() != null && !itm.getDescr().isEmpty())
					s += "<tr><td style='WIDTH: 82px'><strong>Opis</strong></td><td>" + Utils.escapeHTML(itm.getDescr()) + "</td></tr>";
				s += "</table>";
				lbInfo.setHTML("<div class='gwt-ItemPanel-intern'>" + s + "</div>");
			}
			// if (itm.getKind() == ElemKind.Elem) {
			// lbInfo.setHTML(itm.getLabel());
			// } else {
			// lbInfo.setHTML(itm.getKind().getDescr());
			// }
		}

	}

	/** Sprawdza, czy item nie jest rodzicem aktualnej kontrolki */
	public boolean isChildOf(Widget child, ElemDesItem item) {
		for (int i = 0; i < item.mainPanel.getWidgetCount(); i++) {
			if (item.mainPanel.getWidget(i) instanceof ElemDesItem) {
				ElemDesItem w = (ElemDesItem) item.mainPanel.getWidget(i);
				if (w == child)
					return true;
				else
					return isChildOf(child, w);
			}
		}
		return false;
	}

	/** Zaznacza wskazany element. Wskazać mozna tylko elementy typu ELEM */
	public void selectItem(DraggableItem item) {
		if (selectedItem == item || item.getKind() != ElemKind.Elem)
			return;

		if (selectedItem != null) {
			selectedItem.removeStyleDependentName("selected");
		}
		selectedItem = item;
		selectedItem.addStyleDependentName("selected");
	}

	private void showItemPanels() {
		if (selectedElem == null) {
			for (int i = 0; i < ppnProps.getWidgetCount(); i++)
				if (ppnProps.getWidget(i) instanceof LayoutPanel)
					ppnProps.getWidget(i).setVisible(false);
		} else {
			ElemKind kd = selectedElem.itm.getKind();
			ppnId.setVisible(kd == ElemKind.Elem || kd == ElemKind.Header);
			pedId.setReadOnly(kd != ElemKind.Header);
			ppnKind.setVisible(true);
			ppnDescr.setVisible(kd == ElemKind.Elem || kd == ElemKind.Comment || kd == ElemKind.Header);
			ppnLabel.setVisible(kd == ElemKind.Elem || kd == ElemKind.Header || kd == ElemKind.Comment);
			ppnPK.setVisible(kd == ElemKind.Header);
			ppnRepeatMin.setVisible(kd == ElemKind.Header);
			ppnRepeatMax.setVisible(kd == ElemKind.Header);
		}
	}

	public void showDragPointer(ElemDesItem elemDesItem, NativeEvent event) {
		if (elemDesItem != null) {
			if (elemDesItem.itm.getKind() == ElemKind.Header) {
				elemDesItem.mainPanel.add(dragPointer);
			} else {
				FlowPanel pn = (FlowPanel) elemDesItem.getParent();
				int idx = pn.getWidgetIndex(elemDesItem);
				pn.insert(dragPointer, idx);
			}
		} else {
			if (elemTreePanel.getWidgetCount() == 0)
				elemTreePanel.add(dragPointer);
			else {
				Element lastElem = elemTreePanel.getWidget(elemTreePanel.getWidgetCount() - 1).getElement();
				int ey = event.getClientY();
				int wy = lastElem.getAbsoluteBottom();
				if (ey > wy)
					elemTreePanel.add(dragPointer);
			}
		}
	}

	public void hideDragPointer() {
		dragPointer.removeFromParent();
	}

	private Elem getElemById(String id) {
		for (int i = 0; i < pnElems.getWidgetCount(); i++)
			if (((DraggableItem) pnElems.getWidget(i)).getElem().getId().equals(id))
				return ((DraggableItem) pnElems.getWidget(i)).getElem();
		return null;
	}

	interface Binder extends UiBinder<Widget, RegDocDesigner> {
	}

	public RegDocDesigner() {
		initWidget(binder.createAndBindUi(this));

		// Ustawiam controlki jako draggable
		for (int i = 0; i < pnControls.getWidgetCount(); i++)
			pnControls.getWidget(i).getElement().setDraggable(Element.DRAGGABLE_TRUE);

		// Dodaję ogslugę eventów przez gówną klasę, by nie budować za dużo
		// drobnych klasek
		elemTreePanel.addDomHandler(this, DragOverEvent.getType());
		elemTreePanel.addDomHandler(this, DropEvent.getType());
		elemTreePanel.addDomHandler(this, DragLeaveEvent.getType());
		// Będzie wspólny handler dla wszystkich....
		pnControls.add(btnHeader);
		pnControls.add(btnComment);
		pnControls.add(btnNewPage);
		pnControls.add(btnSeparator);

		dragPointer.setStyleName("dragPointer");
		dragPointer.addDragOverHandler(new DragOverHandler() {
			@Override
			public void onDragOver(DragOverEvent event) {
				event.stopPropagation();
				event.preventDefault();
			}
		});

	}

	/** Otwiera wskazaną stronę do edycji.
	 * 
	 * @param caller
	 *            - widget, do którego nalezy wrócić po zakończeniu
	 * @param d */
	static public void edit(Widget caller, IRegDsgnActivity presenter, Doc d) {
		assert d != null;
		get().caller = caller;
		editor.presenter = presenter;
		editor.lbCaption.setText(d.getName());
		JsArray<Elem> ls = presenter.getDoc().getElems();
		editor.pnElems.clear();
		for (int i = 0; i < ls.length(); i++)
			editor.addNewDraggableElem(ls.get(i));

		// Odczyt drzewa elementów z definicji w dokumencie
		editor.buildElemTree(d.getItems());
		editor.currentDoc = d;
		editor.selectedElem = null;
		editor.latestDraggedItem = null;
		editor.showItemPanels();

		FadeAnimation.changeWidget(editor);
		// Utils.getCF().getMainDisplay().setWidget(editor);
	}

	/** Dodaję element do listy dostępnych elementów */
	private void addNewDraggableElem(final Elem elem) {
		DraggableItem it = new DraggableItem();
		it.setElem(elem);
		pnElems.add(it);
	}

	private void buildElemTree(JsArray<DocItem> items) {
		elemTreePanel.clear();
		_buildElemTree(items, elemTreePanel);
	}

	private void _buildElemTree(JsArray<DocItem> items, FlowPanel panel) {
		List<DocItem> ls = new ArrayList<DocItem>();
		// Tworzę kopię elementów i sortuje liste zgodnie z LP
		for (int i = 0; i < items.length(); i++)
			ls.add((DocItem) ElemItem.createFromString(items.get(i).toJson()));

		Collections.sort(ls, new Comparator<DocItem>() {
			@Override
			public int compare(DocItem o1, DocItem o2) {
				return o1.getLp() - o2.getLp();
			}
		});

		for (DocItem itm : ls) {
			ElemDesItem el = new ElemDesItem(panel, itm);
			if (itm.getKind() == ElemKind.Header)
				_buildElemTree(itm.getItems(), el.mainPanel);
		}
	}

	public static RegDocDesigner get() {
		if (editor == null)
			editor = new RegDocDesigner();
		return editor;
	}

	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		FadeAnimation.changeWidget(caller);
		// Utils.getCF().getMainDisplay().setWidget(caller);
	}

	@UiHandler("btnOk")
	void onBtnOkClick(ClickEvent event) {
		// Zapis definicji elementów do dokumentu
		JsArray<Elem> ls = presenter.getDoc().getElems();
		ls.setLength(0);
		for (int i = 0; i < pnElems.getWidgetCount(); i++) {
			DraggableItem w = (DraggableItem) pnElems.getWidget(i);
			ls.push(w.getElem());
		}
		// zapis drezewa elementów
		currentDoc.setItems(serializeItems(elemTreePanel));

		FadeAnimation.changeWidget(caller);
		// Utils.getCF().getMainDisplay().setWidget(caller);
	}

	private JsArray<DocItem> serializeItems(FlowPanel elems) {
		@SuppressWarnings("unchecked")
		JsArray<DocItem> res = (JsArray<DocItem>) RareACareDef.createArray();
		for (int i = 0; i < elems.getWidgetCount(); i++) {
			if (elems.getWidget(i) instanceof ElemDesItem) {
				ElemDesItem w = (ElemDesItem) elems.getWidget(i);
				w.itm.setLp(i);
				res.push(w.itm);
				if (w.itm.getKind() == ElemKind.Header)
					w.itm.setItems(serializeItems(w.mainPanel));
			}
		}
		return res;
	}

	private void putElemOnPos(final ElemDesItem ct) {
		// int currIdx = panel.getWidgetIndex(ct);

		if (dragPointer.getParent() == null)
			return;

		// Nie można poożyc elementu na wasnym dziecku
		if (dragPointer.isAttached() && isChildOf(dragPointer, ct)) {
			return;
		}

		final FlowPanel panel = (FlowPanel) dragPointer.getParent();
		int i = panel.getWidgetIndex(dragPointer);
		if (panel == ct.getParent() && panel.getWidgetIndex(ct) < i)
			i--;
		final int idx = i;

		hideDragPointer();

		// Sprawdzam, czy nie dodaje drugiego identycznego elementu
		for (i = 0; i < panel.getWidgetCount(); i++) {
			if (panel.getWidget(i) instanceof ElemDesItem) {
				ElemDesItem w = (ElemDesItem) panel.getWidget(i);
				if (w.itm.getKind() == ElemKind.Elem && w != ct && w.itm.getId().equals(ct.itm.getId()))
					Utils.getCF().showError("Nie można dodać do jednej grupy dwukrotnie tego samego elementu");
			}
		}

		// if (ct.getParent() != null) {
		// GQuery w = GQuery.$(ct);
		// final String style = "height:'" + ct.getElement().getClientHeight() +
		// "px', opacity:'" + w.css("opacity") + "', margin-top:'" +
		// w.css("margin-top") + "', margin-bottom:'" + w.css("margin-bottom") +
		// "', padding:'" + w.css("padding") + "'";
		// GQuery.$(ct).animate("height:'0px', opacity:'0.0', margin-top:'0', margin-bottom:'0', padding:'0'",
		// 500, new Function() {
		final String style = "opacity:'1.0'";
		GQuery.$(ct).animate("opacity:'0.0'", 500, new Function() {
			public void f(Element e) {
				ct.removeFromParent();
				if (idx < 0)
					panel.add(ct);
				else
					panel.insert(ct, idx);
				GQuery.$(ct).animate(style, 300, new Function() {
					public void f(Element e) {
						e.getStyle().clearHeight();
						e.getStyle().clearOpacity();
						e.getStyle().clearMarginBottom();
						e.getStyle().clearMarginTop();
						e.getStyle().clearPadding();
					}
				});
			}
		});
		// } else {
		// ct.removeFromParent();
		// int idx = panel.getWidgetIndex(target);
		// if (idx < 0)
		// panel.add(ct);
		// else
		// panel.insert(ct, idx);
		// }
	}

	/** On drop na panelu gównym */
	@Override
	public void onDrop(DropEvent event) {
		scrollDocTreePanel.removeStyleName("gwt-PanelDropTarget");
		event.preventDefault();
		event.stopPropagation();

		doDrop();
	}

	private void doDrop() {

		if (latestDraggedItem != null) {
			if (latestDraggedItem.getKind() == ElemKind.PageBreak && dragPointer.getParent() != elemTreePanel) {
				hideDragPointer();
				Utils.getCF().showError("Znacznik końca strony może być lokowany jedynie bezpośrednio na stronie");
			}

			try {
				if (latestDraggedItem.isConrol() || (latestDraggedItem.isElem())) {
					new ElemDesItem(latestDraggedItem);
				}
				else if (latestDraggedItem.isTreeLeaf()) { // Już element w
															// drzewie
					putElemOnPos((ElemDesItem) latestDraggedItem.draggedControl);
				}
			} catch (Exception e) {
				// The user probably dragged something other than a
				// template.
			}
		}
	}

	/** On DragOver na panelu gównym */
	@Override
	public void onDragOver(DragOverEvent event) {
		// To ponoć trzeba zaimplementować- inaczej czasem nie dziala
		// Style st = docTreePanel.getElement().getStyle();
		// st.setBorderColor("red"); }
		scrollDocTreePanel.addStyleName("gwt-PanelDropTarget");
		showDragPointer(null, event.getNativeEvent());

	}

	@Override
	public void onDragLeave(DragLeaveEvent event) {
		scrollDocTreePanel.removeStyleName("gwt-PanelDropTarget");
		// hideDragPointer();
	}

	@UiHandler("imgTrash")
	void onImgTrashDrop(DropEvent event) {
		event.preventDefault();
		event.stopPropagation();
		imgTrash.removeStyleName("gwt-PanelDropTarget");
		if (latestDraggedItem != null && latestDraggedItem.isTreeLeaf()) {
			latestDraggedItem.draggedControl.removeFromParent();
		}
	}

	@UiHandler("imgTrash")
	void onImgTrashDragOver(DragOverEvent event) {
		imgTrash.addStyleName("gwt-PanelDropTarget");
	}

	@UiHandler("imgTrash")
	void onImgTrashDragLeave(DragLeaveEvent event) {
		imgTrash.removeStyleName("gwt-PanelDropTarget");
	}

	@UiHandler("pedId")
	void onPedIdValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	private void saveDataToObject() {

		selectedElem.itm.setId(pedId.getText());
		selectedElem.itm.setDescr((ppnDescr.isVisible()) ? pedDescr.getText() : null);
		selectedElem.itm.setLabel((ppnLabel.isVisible()) ? pedLabel.getText() : null);
		selectedElem.itm.setRepeatMax((ppnRepeatMax.isVisible()) ? Integer.valueOf(Utils.ChkNumeric(pedRepeatMax.getText(), "Powtórz max"))
				: 1);
		selectedElem.itm
				.setRepeatMin((ppnRepeatMin.isVisible()) ? Integer.valueOf(Utils.ChkNumeric(pedRepeatMin.getText(), "Powtórz mmin")) : 0);
		selectedElem.itm.setPrimaryKey((ppnPK.isVisible()) ? pedPK.getValue(pedPK.getSelectedIndex()) : null);

		selectedElem.update();
		showItemPanels();
	}

	@UiHandler("pedLabel")
	void onPedLabelValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	@UiHandler("pedDescr")
	void onPedDescrValueChange(ValueChangeEvent<String> event) {
		saveDataToObject();
	}

	@UiHandler("pedRepeatMin")
	void onPedRepeatMinValueChange(ValueChangeEvent<Integer> event) {
		saveDataToObject();
	}

	@UiHandler("pedRepeatMax")
	void onPedRepeatMaxValueChange(ValueChangeEvent<Integer> event) {
		saveDataToObject();
	}

	@UiHandler("pedPK")
	void onPedPKValueChange(ChangeEvent event) {
		saveDataToObject();
	}

	@UiHandler("btnProps")
	void onBtnPropsClick(ClickEvent event) {
		RegDsgnDocProps.editDocProps(currentDoc, presenter.getDoc());
	}

	@UiHandler("btnPropsEx")
	void onBtnPropsExClick(ClickEvent event) {
		RegDsgnDocPropsEx.edit(presenter.getFieldList(currentDoc), currentDoc);
	}

	@UiHandler("btnDocPrv")
	void onBtnDocPrvClick(ClickEvent event) {
		// Generuję nową listę dokuentów
		@SuppressWarnings("unchecked")
		JsArray<Elem> ls = (JsArray<Elem>) Doc.createArray();
		for (int i = 0; i < pnElems.getWidgetCount(); i++) {
			ls.push(((DraggableItem) pnElems.getWidget(i)).getElem());
		}
		// zapis drezewa elementów
		Doc doc = Doc.createFromString(currentDoc.toJson());
		doc.setItems(serializeItems(elemTreePanel));

		RegDocPrv.showDoc(doc, ls);
	}

	/** Dodawanie elementu z repozytorium */
	@UiHandler("btnRep")
	void onBtnRepClick(ClickEvent event) {
		RegDElemsList.showDialog(this);
	}

	/** Tworzenie nowego elementu */
	@UiHandler("btnAdd")
	void onBtnAddClick(ClickEvent event) {
		RegDocElemDesigner.get().editElem(null, this);
	}

	@UiHandler("btnOpenElem")
	void onBtnOpenElemClick(ClickEvent event) {
		if (selectedItem != null && selectedItem.getKind() == ElemKind.Elem && selectedItem.elem != null)
			RegDocElemDesigner.get().editElem(selectedItem.elem, this);
	}

	@UiHandler("btnExportElem")
	void onBtnExportElemClick(ClickEvent event) {
		if (selectedItem != null && selectedItem.getKind() == ElemKind.Elem && selectedItem.elem != null
				&& Utils.getCF().confirm("Czy jesteś pewien, że chcesz dodać wskazany element do globalnego repozytorium elementów?")) {
			RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
			final String lb = selectedItem.elem.getLabel();
			req.dDocElemAdd(selectedItem.elem.toJson()).fire(new CommonReceiver<Void>() {
				@Override
				public void onSuccess(Void response) {
					Utils.getCF().showMessage("Element: \"" + lb + "\" zapisano do repozytorium");
				}
			});
		}
	}

	@Override
	public void saveElem(Elem nw, Elem org) {
		String id;
		if (org == null)
			id = nw.getId();
		else
			id = org.getId();

		for (int i = 0; i < pnElems.getWidgetCount(); i++) {
			DraggableItem w = (DraggableItem) pnElems.getWidget(i);
			if (w.getElem().getId().equals(id)) {
				w.setElem(nw);
				return;
			}
		}
		// Nie ma tego elementu - dodaję nowy
		addNewDraggableElem(nw);
	}

	@UiHandler("btnDeleteElem")
	void onBtnDeleteElemClick(ClickEvent event) {
		if (selectedItem != null && selectedItem.getKind() == ElemKind.Elem && selectedItem.elem != null) {
			JsArray<Doc> docs = presenter.getDoc().getDocs();
			for (int i = 0; i < docs.length(); i++) {
				if (findElem(docs.get(i).getItems(), selectedItem.elem.getId())) {
					Utils.getCF().showError(
							"Wskazany element jest wykorzystywany w dokumencie: \"" + docs.get(i).getName() + "\" i nie może być usunięty ze słownika");
					return;
				}
			}
		}

		// Usuwanie elementu
		selectedItem.removeFromParent();
		selectedItem = null;
	}

	private boolean findElem(JsArray<DocItem> items, String id) {
		for (int i = 0; i < items.length(); i++) {
			if (items.get(i).getId().equals(id))
				return true;
			if (findElem(items.get(i).getItems(), id))
				return true;
		}
		return false;
	}
}
