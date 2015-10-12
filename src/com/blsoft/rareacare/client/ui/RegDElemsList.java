package com.blsoft.rareacare.client.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.DDocElemProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Elem;
import com.blsoft.rareacare.client.ui.RegDocElemDesigner.IDElemConsumer;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.blsoft.rareacare.client.ui.controls.BackButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class RegDElemsList extends DialogBox implements IDElemConsumer {

	private static final Binder binder = GWT.create(Binder.class);
	@UiField BLButton btnSearch;
	@UiField BLButton btnAdd;
	@UiField BLButton btnOpen;
	@UiField BackButton btnClose;
	@UiField TextBox edSearch;
	@UiField ScrollPanel elemsScrollPanel;

	private static RegDElemsList instance = new RegDElemsList();
	private RegDocDesigner regDes;

	interface Binder extends UiBinder<Widget, RegDElemsList> {
	}
	
	private CellList<DDocElemProxy> cellList;
	SingleSelectionModel<DDocElemProxy> selectionModel;
	ProvidesKey<DDocElemProxy> keyProvider = new ProvidesKey<DDocElemProxy>() {
		@Override
		public Object getKey(DDocElemProxy item) {
			return (item == null) ? null : item.getId();
		}
	};
	protected List<DDocElemProxy> list;
	
	
	/** The Cell used to render a Document. */
	static class DDocElemCell extends AbstractCell<DDocElemProxy> {

		@Override
		public void render(Context context, DDocElemProxy value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			}
			SafeHtml sh = Utils.TEMPLATES.regEditNewDocItem(Utils.coalesce(value.getId()), Utils.coalesce(value.getLabel()), Utils.coalesce(value.getDescription()));
			sb.append(sh);
		}
	}
	

	public RegDElemsList() {
		
		setText("Repozytorium globalne elementów rejestrów");
		
		// Create a CellList.
		DDocElemCell dDocElemCell = new DDocElemCell();

		cellList = new CellList<DDocElemProxy>(dDocElemCell, keyProvider);
		
		// Add a selection model using the same keyProvider.
		selectionModel = new SingleSelectionModel<DDocElemProxy>(keyProvider);		
		cellList.setSelectionModel(selectionModel);

		setWidget(binder.createAndBindUi(this));

		elemsScrollPanel.add(cellList);
	}
	
	static public void showDialog(RegDocDesigner regDes) {
		
		instance.regDes = regDes;
		instance.setAnimationEnabled(true);
		instance.setAutoHideOnHistoryEventsEnabled(true);
		instance.setGlassEnabled(true);
		instance.center();
		instance.edSearch.setText(null);
		instance.onBtnSearchClick(null);
		
	}
	

	@UiHandler("btnSearch")
	void onBtnSearchClick(ClickEvent event) {
		RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
		req.getDDocElems(edSearch.getText()).fire(new CommonReceiver<List<DDocElemProxy>>() {

			@Override
			public void onSuccess(List<DDocElemProxy> response) {
				list = response;
				cellList.setRowData(response);
			}

		});
	}
	
	@UiHandler("btnOpen")
	void onBtnOpenClick(ClickEvent event) {
		DDocElemProxy so = selectionModel.getSelectedObject();
		if (so != null) {
			Elem ee = (Elem)Elem.createFromString(so.getDef());
			RegDocElemDesigner.get().editElem(ee, this);
		}
	}

	@UiHandler("btnClose")
	void onBtnCloseClick(ClickEvent event) {
		hide();
	}

	@UiHandler("btnAdd")
	void onBtnAddClick(ClickEvent event) {
		DDocElemProxy so = selectionModel.getSelectedObject();
		if (so != null && Utils.getCF().confirm("Czy jesteś pewien, że chcesz wstawić do rejestru definicję objektu: \"" + so.getLabel() + "\" do słownika elementów? (może to spowodować nadpisanie ewentualnego elementu o tym samym kodzie)")) {
			Elem ee = (Elem)Elem.createFromString(so.getDef());
			regDes.saveElem(ee, null);
		}
	}

	@Override
	public void saveElem(Elem nw, Elem org) {
		String id;
		if (org == null)
			id = nw.getId();
		else
			id = org.getId();
		final String fid = id;

		DDocElemProxy e = null;
		for (DDocElemProxy l : list) {
			if (l.getId().equals(id)) {
				e = l;
				break;
			}
		}
		
		RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
		if (e==null)
			e = req.create(DDocElemProxy.class);
		else
			e = req.edit(e);
		e.setLabel(nw.getLabel());
		e.setDef(nw.toJson());
		// TODO dodać description
		
		// Zapis do bazy 
		req.persistAndLoad(e).fire(new CommonReceiver<DDocElemProxy>() {

			@Override
			public void onSuccess(DDocElemProxy response) {
				boolean found = false;
				for (int i=0;i<list.size();i++)
					if (list.get(i).getId().equals(fid)) {
						list.set(i, response);
						found = true;
						break;
					}
				if (!found)
					list.add(response);
				
				// Sortowanie po wykonaniu zapisu
				Collections.sort(list, new Comparator<DDocElemProxy>() {

					@Override
					public int compare(DDocElemProxy o0, DDocElemProxy o1) {
						return o0.getLabel().compareToIgnoreCase(o1.getLabel());
					}}); 
					cellList.setRowData(list);
			}
		});
		
	}

}
