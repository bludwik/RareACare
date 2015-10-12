package com.blsoft.rareacare.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IRegDsgnActivity;
import com.blsoft.rareacare.client.events.MainWidgetChangeEvent;
import com.blsoft.rareacare.client.events.RegistryChangeEvent;
import com.blsoft.rareacare.client.events.MainWidgetChangeEvent.MainWidgetChangeHandler;
import com.blsoft.rareacare.client.events.RegistryChangeEvent.RegistryChangeHandler;
import com.blsoft.rareacare.client.place.RegsPlace;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocTreeItem;
import com.blsoft.rareacare.client.res.Images;
import com.blsoft.rareacare.client.ui.interfaces.IRegDesigner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.user.client.ui.Label;

public class RegDesigner extends Composite implements IRegDesigner, RegistryChangeHandler, MainWidgetChangeHandler, DropHandler,
		DragOverHandler {

	final private RegDesigner regDesigner;
	final private ToolBox toolBox = new ToolBox();

	private IRegDsgnActivity presenter;
	private static final Binder binder = GWT.create(Binder.class);

	HTML dragPointer = new HTML();

	public class ToolBox extends PopupPanel {
		private HorizontalPanel horizontalPanel;
		private PushButton btnLeft;
		private PushButton btnRight;
		private PushButton btnEdit;

		public ToolBox() {
			super(true);

			horizontalPanel = new HorizontalPanel();
			setWidget(horizontalPanel);
			horizontalPanel.setSize("100%", "100%");

			btnLeft = new PushButton(new Image(Images.INSTANCE.arrowLeft24()), new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (selectedItem != null) {
						Integer i = selectedItem.item.getIdent();
						if (i > 0) {
							selectedItem.setIdent(i - 1);
						}
					}
				}
			});
			horizontalPanel.add(btnLeft);

			btnRight = new PushButton(new Image(Images.INSTANCE.arrowRight24()), new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (selectedItem != null) {
						selectedItem.setIdent(selectedItem.item.getIdent() + 1);
					}
				}
			});
			horizontalPanel.add(btnRight);

			btnEdit = new PushButton(new Image(Images.INSTANCE.edit24()), new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Doc d = getDocById(selectedItem.item.getId());
					if (d != null) {
						RegDocDesigner.edit(regDesigner, presenter, d);
						// toolBox.hide();
					}
				}
			});
			horizontalPanel.add(btnEdit);
		}

	}

	/** ID pozycji ciągniętej. Te pola robięjako zmienne globalne, bo niestety
	 * sypalo się zapisywanie w event.setData dla CellList */
	private static DraggableItem latestDraggedItem;
	private static DraggableItem selectedItem;

	@UiField PushButton btnUsers;
	@UiField PushButton btnAddDoc;
	@UiField FlowPanel docTreePanel;
	@UiField(provided = true) Image imgTrash = new Image(Images.INSTANCE.trash());
	@UiField PushButton btnSaveAndClose;
	@UiField PushButton btnReOrderTree;
	@UiField PushButton btnCancel;
	@UiField Label lbCaption;
	@UiField FlowPanel docPanel;
	@UiField ScrollPanel docTreeScrollPanel;

	public class DraggableItem extends Composite implements MouseOverHandler, DragStartHandler, DragOverHandler, DropHandler,
			DragLeaveHandler {

		private DocTreeItem item;
		private Doc doc;
		FlowPanel mainPanel;
		HTML mainHtml;

		void initItem() {
			mainPanel = new FlowPanel();
			mainPanel.setStyleName("gwt-ItemPanel");
			initWidget(mainPanel);
			// mainPanel.setWidth("100%");
			// mainPanel.getElement().getStyle().setMarginLeft(50, Unit.PX);

			mainHtml = new HTML("New HTML", true);
			mainPanel.add(mainHtml);
			// mainHtml.setWidth("100%");
			getElement().setDraggable(Element.DRAGGABLE_TRUE);
			addDomHandler(this, DragStartEvent.getType());
			addDomHandler(this, DragOverEvent.getType());
			addDomHandler(this, MouseOverEvent.getType());
			addDomHandler(this, DropEvent.getType());
			addDomHandler(this, DragLeaveEvent.getType());
			// addDomHandler(this, ClickEvent.getType());

		}

		private void update() {
			SafeHtmlBuilder builder = new SafeHtmlBuilder();
			builder.appendEscaped(doc.getName()).appendHtmlConstant("<p style='font-size: 80%;'>").appendEscaped(doc.getDescr())
					.appendHtmlConstant("</p>");
			mainHtml.setHTML("<div class='gwt-ItemPanel-intern'>" + builder.toSafeHtml().asString() + "</div>");
		}

		public DraggableItem(Doc doc) {
			initItem();
			this.doc = doc;
			update();
		}

		public DraggableItem(DocTreeItem itm) {
			initItem();
			this.item = itm;
			doc = getDocById(itm.getId());
			setIdent(item.getIdent());
			update();
		}

		public boolean isTreeItem() {
			return (item != null);
		}

		@Override
		public void onDragStart(DragStartEvent event) {
			latestDraggedItem = this; // getIndex();
			// event.getDataTransfer().setDragImage(this.getElement(), 10, 10);
			event.setData("text", "test");
			event.stopPropagation();
			// event.preventDefault();
		}

		public void setIdent(int i) {
			item.setIdent(i);
			getElement().getStyle().setMarginLeft(0.5 + i * 3, Unit.EM);
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
		}

		@Override
		public void onDragOver(DragOverEvent event) {
			event.stopPropagation();
			if (isTreeItem())
				showDragPointer(this, event.getNativeEvent());
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			if (isTreeItem())
				showArrows(this);
		}

	}

	@Override
	public void setPresenter(IRegDsgnActivity presenter) {
		this.presenter = presenter;
		lbCaption.setText(presenter.getRegistry().getName());
		JsArray<Doc> ls = presenter.getDoc().getDocs();

		// Budowa listy dokumentów
		docPanel.clear();
		for (int i = 0; i < ls.length(); i++)
			docPanel.add(new DraggableItem(ls.get(i)));

		// Budowa drzewa dokumentów
		JsArray<DocTreeItem> dtr = presenter.getDoc().getDocTree();
		ArrayList<DocTreeItem> docTree = new ArrayList<DocTreeItem>();
		for (int i = 0; i < dtr.length(); i++)
			docTree.add(dtr.get(i));
		Collections.sort(docTree, new Comparator<DocTreeItem>() {
			@Override
			public int compare(DocTreeItem arg0, DocTreeItem arg1) {
				return arg0.getLp() - arg1.getLp();
			}
		});
		docTreePanel.clear();
		for (DocTreeItem e : docTree) {
			DraggableItem tre = new DraggableItem(e);
			docTreePanel.add(tre);
			tre.getElement().setDraggable(Element.DRAGGABLE_TRUE);
			tre.addDomHandler(tre, DragStartEvent.getType());
		}
		return;
	}

	private void showArrows(DraggableItem itm) {
		// if (selectedItem != itm)
		{
			selectedItem = itm;
			toolBox.setPopupPosition(selectedItem.getElement().getAbsoluteRight() - 140, selectedItem.getAbsoluteTop());
			toolBox.show();
		}
	}

	public void showDragPointer(DraggableItem draggableItem, NativeEvent event) {
		if (draggableItem != null) {
			FlowPanel pn = (FlowPanel) draggableItem.getParent();
			int idx = pn.getWidgetIndex(draggableItem);
			pn.insert(dragPointer, idx);
		} else if (docTreePanel.getWidgetCount() == 0)
			docTreePanel.add(dragPointer);
		else {
			Element lastElem = docTreePanel.getWidget(docTreePanel.getWidgetCount() - 1).getElement();
			int ey = event.getClientY();
			int wy = lastElem.getAbsoluteBottom();
			if (ey > wy)
				docTreePanel.add(dragPointer);
		}

	}

	public void hideDragPointer() {
		dragPointer.removeFromParent();
	}

	public Doc getDocById(String s) {
		for (int i = 0; i < docPanel.getWidgetCount(); i++) {
			DraggableItem d = (DraggableItem) docPanel.getWidget(i);
			if (d.doc.getId().equals(s))
				return d.doc;
		}
		return null;
	}

	interface Binder extends UiBinder<Widget, RegDesigner> {
	}

	public RegDesigner() {
		regDesigner = this;
		initWidget(binder.createAndBindUi(this));

		docTreePanel.addDomHandler(this, DragOverEvent.getType());

		docTreePanel.addDomHandler(this, DropEvent.getType());

		Utils.getCF().getEventBus().addHandler(RegistryChangeEvent.TYPE, this);
		Utils.getCF().getEventBus().addHandler(MainWidgetChangeEvent.TYPE, this);

		dragPointer.setStyleName("dragPointer");

	}

	@Override
	public void addOrSetDoc(RegistryProxy.Doc e) {
		for (int i = 0; i < docPanel.getWidgetCount(); i++) {
			DraggableItem d = (DraggableItem) docPanel.getWidget(i);
			if (d.doc == e)
				return;
		}
		docPanel.add(new DraggableItem(e));
	}

	/** Porządkuje hierarhię elementów w drzewie tak, by kolejny element mie
	 * ident nie większy od wczesniejszego, niż o 1. Ponadto wymusza, by był
	 * tylko jeden nadrzędny dokument */
	private boolean reOrderTree() {
		int lastIdent = -1;
		boolean result = false;
		String[] pth = new String[50];
		boolean addOffset = false;
		
		// Zwiększam wcięcie kolejnych elementów, gdy też próbuja byc głownymi		
		for (int i = 0; i < docTreePanel.getWidgetCount(); i++) {
			Widget w = docTreePanel.getWidget(i);
			if (w instanceof DraggableItem) {
				DraggableItem d = (DraggableItem) w;
				int j = d.item.getIdent();
				if (i > 0 && j == 0)
					addOffset = true;
				if (addOffset)
					d.setIdent(j + 1); 
			}
		}
		
		// ZMniejszam odpowiednie wcięcia
		for (int i = 0; i < docTreePanel.getWidgetCount(); i++) {
			Widget w = docTreePanel.getWidget(i);
			if (w instanceof DraggableItem) {
				DraggableItem d = (DraggableItem) w;

				if (d.item.getIdent() > lastIdent + 1) {
					d.setIdent(lastIdent + 1);
					result = true;
				}
				lastIdent = d.item.getIdent();

				// Budowanie ścieżki
				pth[lastIdent] = d.doc.getId();
				String sp = "";
				for (int j = 0; j <= lastIdent; j++)
					sp += '/' + pth[j];
				d.item.setPath(sp);

				// Ustalanie unikatowego ID dokumentu w drzewie
				if (d.item.getUniqueId() == null)
					d.item.setUniqueId(Utils.uuid());

			}
		}
		return result;
	}

	@UiHandler("btnUsers")
	void onBtnUsersClick(ClickEvent event) {
		presenter.editRegUsers();
	}

	@UiHandler("btnAddDoc")
	void onBtnAddDocClick(ClickEvent event) {
		RegistryProxy.Doc d = (RegistryProxy.Doc) RegistryProxy.Doc.createObject();
		RegDsgnDocProps.editDocProps(d, presenter.getDoc());
	}

	@UiHandler("imgTrash")
	void onImgTrashDragOver(DragOverEvent event) {
		// Pusty handler musi być, by browser wiedzia, że coś tu rzucam
	}

	@UiHandler("imgTrash")
	void onImgTrashDrop(DropEvent event) {
		event.stopPropagation();
		event.preventDefault();
		if (latestDraggedItem != null)
			if (latestDraggedItem.isTreeItem()) {
				// BL: NIe daję pytania, bo zgaszao debilny bad o EntryLevel pod
				// FireFox....
				// if
				// (Utils.getCF().confirm("Czy jesteś pewien, że chcesz usunąć wybrany dokument z edytora struktury dokumentów?"))
				// {
				docTreePanel.remove(latestDraggedItem);
				// }
			} else {
				for (int i = docTreePanel.getWidgetCount() - 1; i >= 0; i--) {
					if (docTreePanel.getWidget(i) instanceof DraggableItem)
						if (((DraggableItem) docTreePanel.getWidget(i)).item.getId().equals(latestDraggedItem.doc.getId()))
							docTreePanel.remove(i);
				}
				docPanel.remove(latestDraggedItem);
			}
		hideDragPointer();
		latestDraggedItem = null;
	}

	@UiHandler("btnSaveAndClose")
	void onBtnSaveAndCloseClick(ClickEvent event) {

		if (Utils.getCF().confirm("Czy jesteś pewien, że chcesz zapisać zmiany w definicji rejestru?")) {
			if (reOrderTree()) {
				Utils.getCF().showMessage("Zmodyfikowano drzewo dokumentów. Przed zapisem zobacz, czy ją akceptujesz i zapisz ponownie");
				return;
			}
			// Wypeniam na nowo listę dokumentów
			JsArray<Doc> ls = presenter.getDoc().getDocs();
			ls.setLength(0);
			for (int i = 0; i < docPanel.getWidgetCount(); i++) {
				DraggableItem d = (DraggableItem) docPanel.getWidget(i);
				ls.push(d.doc);
			}

			// Wypeniam hierarchię dokumentów

			JsArray<DocTreeItem> lh = presenter.getDoc().getDocTree();
			lh.setLength(0);
			int ct = 0;
			for (int i = 0; i < docTreePanel.getWidgetCount(); i++) {
				if (docTreePanel.getWidget(i) instanceof DraggableItem) {
					DocTreeItem d = ((DraggableItem) docTreePanel.getWidget(i)).item;
					d.setLp(ct++);
					lh.push(d);
				}
			}

			// Zapis definicji do bazy
			presenter.saveReg(true);
			// toolBox.hide();
		}

	}

	@UiHandler("btnReOrderTree")
	void onBtnReOrderTreeClick(ClickEvent event) {
		reOrderTree();
	}

	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		if (Utils.confirm("Czy jesteś pewien, że chesz zamknąć edytor. Ewentualne zmiany zostaną utracone?"))
			presenter.goTo(new RegsPlace());
	}

	@Override
	public void onRegistryChange(RegistryChangeEvent event) {
		lbCaption.setText(event.getEntity().getName());
	}

	@Override
	public void onMainWidgetChange(MainWidgetChangeEvent event) {
		// Ukrywam w evencie zmiany strony, bo inaczej pokazywa ponownie
		toolBox.hide();
	}

	@Override
	public void onDrop(DropEvent event) {
		// Prevent the default text drop.
		event.preventDefault();

		// Un-highlight the name and notes box.
		Style st = docTreePanel.getElement().getStyle();
		st.clearBorderColor();

		// Fill in the form.
		try {
			if (latestDraggedItem != null) {
				DraggableItem dgi;
				if (!latestDraggedItem.isTreeItem() && dragPointer.getParent() == docTreePanel) {
					DocTreeItem itm = (DocTreeItem) DocTreeItem.createObject();
					itm.setId(latestDraggedItem.doc.getId());
					itm.setIdent(0);
					dgi = new DraggableItem(itm);
				} else
					dgi = latestDraggedItem;

				latestDraggedItem = null;
				putElemOnPos(dgi);
			}
		} catch (NumberFormatException e) {
			// The user probably dragged something other than a
			// template.
		}
	}

	private void putElemOnPos(final DraggableItem ct) {

		if (dragPointer.getParent() == null)
			return;

		final FlowPanel panel = (FlowPanel) dragPointer.getParent();
		int i = panel.getWidgetIndex(dragPointer);
		if (panel == ct.getParent() && panel.getWidgetIndex(ct) < i)
			i--;
		final int idx = i;

		hideDragPointer();

		GQuery w = GQuery.$(ct);
		final String style = "height:'" + ct.getElement().getClientHeight() + "px', opacity:'" + w.css("opacity") + "', margin-top:'"
				+ w.css("margin-top") + "', margin-bottom:'" + w.css("margin-bottom") + "', padding:'" + w.css("padding") + "'";

		GQuery.$(ct).animate("height:'0px', opacity:'0.0', margin-top:'0', margin-bottom:'0', padding:'0'", 400, new Function() {
			public void f(Element e) {
				ct.removeFromParent();
				if (idx < 0)
					panel.add(ct);
				else
					panel.insert(ct, idx);
				GQuery.$(ct).animate(style, 200, new Function() {
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
	}

	@Override
	public void onDragOver(DragOverEvent event) {
		if (event.getSource() == docTreePanel) {
			docTreeScrollPanel.addStyleName("gwt-PanelDropTarget");
			showDragPointer(null, event.getNativeEvent());
		}
	}

}
