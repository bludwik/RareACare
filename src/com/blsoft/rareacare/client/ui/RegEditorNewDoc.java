package com.blsoft.rareacare.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocTreeItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.event.dom.client.ClickEvent;

public class RegEditorNewDoc extends DialogBox {

	private static final Binder binder = GWT.create(Binder.class);
	@UiField BLButton btnAdd;
	@UiField BLButton btnClose;
	@UiField ScrollPanel pnScroll;
	

	private CellList<DocTreeItem> cellList;
	SingleSelectionModel<DocTreeItem> selectionModel;
	ProvidesKey<DocTreeItem> keyProvider = new ProvidesKey<DocTreeItem>() {
		@Override
		public Object getKey(DocTreeItem item) {
			return (item == null) ? null : item.getUniqueId();
		}
	};

	static final RegEditorNewDoc instance = new RegEditorNewDoc();
	private RegEditor editor;

	
	/** The Cell used to render a Document. */
	static class DocumentCell extends AbstractCell<DocTreeItem> {

		static private RareACareDef regDef;
		
		@Override
		public void render(Context context, DocTreeItem value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			}
			
			Doc dc = regDef.getDoc(value.getId());
			SafeHtml sh = Utils.TEMPLATES.regEditNewDocItem(dc.getId(), dc.getName(), dc.getDescr());

			sb.append(sh);
		}
	}


	interface Binder extends UiBinder<Widget, RegEditorNewDoc> {
	}

	public RegEditorNewDoc() {
		
		// Create a CellList.
		DocumentCell documentCell = new DocumentCell();

		cellList = new CellList<DocTreeItem>(documentCell, keyProvider);

		// Add a selection model using the same keyProvider.
		selectionModel = new SingleSelectionModel<DocTreeItem>(keyProvider);
		cellList.setSelectionModel(selectionModel);

		setWidget(binder.createAndBindUi(this));

		pnScroll.add(cellList);
		
	}
	
	static public void show(RegEditor ed) {
		
		instance.editor = ed;
		DocumentCell.regDef = ed.getRegDef();
		instance.cellList.setRowData(ed.getDocsToAdd());
		
		instance.setAnimationEnabled(true);
		instance.setAutoHideOnHistoryEventsEnabled(true);
		instance.setGlassEnabled(true);
		instance.center();		
	}

	@UiHandler("btnAdd")
	void onBtnAddClick(ClickEvent event) {
		editor.addNewDoc(selectionModel.getSelectedObject());
		hide();
	}
	
	@UiHandler("btnClose")
	void onBtnCloseClick(ClickEvent event) {
		hide();
	}
}
