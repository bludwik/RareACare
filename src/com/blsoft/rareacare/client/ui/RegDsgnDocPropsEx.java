package com.blsoft.rareacare.client.ui;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class RegDsgnDocPropsEx extends DialogBox {

	private static final Binder binder = GWT.create(Binder.class);
	private static RegDsgnDocPropsEx editor;

	@UiField ListBox lbAll;
	@UiField ListBox lbInfo;
	@UiField ListBox lbUnique;
	@UiField BLButton btnAddToInfo;
	@UiField BLButton btnDelFromInfo;
	@UiField BLButton btnAddToUnique;
	@UiField BLButton btnDelFromUnique;
	@UiField BLButton btnUp2;
	@UiField BLButton btnDown2;
	@UiField BLButton btnUp3;
	@UiField BLButton btnDown3;
	@UiField IntegerBox edMax;
	@UiField BLButton btnCancel;
	@UiField BLButton btnOk;

	private Doc currentDoc;

	interface Binder extends UiBinder<Widget, RegDsgnDocPropsEx> {
	}

	public RegDsgnDocPropsEx() {
		setWidget(binder.createAndBindUi(this));
	}

	public static void edit(List<String> fldList, Doc e) {
		get();
		assert e != null;
		editor.init(fldList, e);
		editor.setAnimationEnabled(true);
		editor.setAutoHideOnHistoryEventsEnabled(true);
		editor.setGlassEnabled(true);
		editor.center();
	}

	private void init(List<String> fldList, Doc e) {
		currentDoc = e;
		lbAll.clear();
		lbInfo.clear();
		lbUnique.clear();
		
		for (String f : fldList) {
			if (f.length() > 0 && f.charAt(0) == '|')
				lbAll.addItem(f.substring(1));
			else
				lbAll.addItem(f);
		}
		
		// Odczyt listy pól informacyjnych
		JsArrayString arr = currentDoc.getInfoFields();
		for (int i=0; i<arr.length(); i++)
			lbInfo.addItem(arr.get(i));

		// Odczyt listy pól unikalnych
		arr = currentDoc.getUniqueFields();
		for (int i=0; i<arr.length(); i++)
			lbUnique.addItem(arr.get(i));
		
		edMax.setValue(e.getMaxCount());
		
	}

	public static RegDsgnDocPropsEx get() {
		if (editor == null)
			editor = new RegDsgnDocPropsEx();
		return editor;
	}

	@UiHandler("btnCancel")
	void onBtnCancelClick(ClickEvent event) {
		hide();
	}

	private int indexOf(ListBox ls, String item) {
		for (int i = 0; i < ls.getItemCount(); i++)
			if (ls.getItemText(i).equals(item))
				return i;
		return -1;
	}

	@UiHandler("btnAddToInfo")
	void onBtnAddToInfoClick(ClickEvent event) {
		int ix = lbAll.getSelectedIndex();
		if (ix >= 0) {
			String s = lbAll.getItemText(ix);
			if (indexOf(lbInfo, s) < 0)
				lbInfo.addItem(s);
		}
	}

	@UiHandler("btnAddToUnique")
	void onBtnAddToUniqueClick(ClickEvent event) {
		int ix = lbInfo.getSelectedIndex();
		if (ix >= 0) {
			String s = lbInfo.getItemText(ix);
			if (indexOf(lbUnique, s) < 0)
				lbUnique.addItem(s);
		}
	}

	@UiHandler("btnDelFromInfo")
	void onBtnDelFromInfoClick(ClickEvent event) {
		int ix = lbInfo.getSelectedIndex();
		if (ix >= 0)
			lbInfo.removeItem(ix);
	}

	@UiHandler("btnDelFromUnique")
	void onBtnDelFromUniqueClick(ClickEvent event) {
		int ix = lbUnique.getSelectedIndex();
		if (ix >= 0)
			lbUnique.removeItem(ix);
	}

	@UiHandler("btnUp2")
	void onBtnUp2Click(ClickEvent event) {
		int ix = lbInfo.getSelectedIndex();
		if (ix > 0) {
			String s = lbInfo.getItemText(ix);
			lbInfo.removeItem(ix--);
			lbInfo.insertItem(s, ix);
			lbInfo.setSelectedIndex(ix);
		}
	}

	@UiHandler("btnDown2")
	void onBtnDown2Click(ClickEvent event) {
		int ix = lbInfo.getSelectedIndex();
		if (ix < lbInfo.getItemCount() - 1) {
			String s = lbInfo.getItemText(ix);
			lbInfo.removeItem(ix++);
			lbInfo.insertItem(s, ix);
			lbInfo.setSelectedIndex(ix);
		}
	}

	@UiHandler("btnUp3")
	void onBtnUp3Click(ClickEvent event) {
		int ix = lbUnique.getSelectedIndex();
		if (ix > 0) {
			String s = lbUnique.getItemText(ix);
			lbUnique.removeItem(ix--);
			lbUnique.insertItem(s, ix);
			lbUnique.setSelectedIndex(ix);
		}
	}

	@UiHandler("btnDown3")
	void onBtnDown3Click(ClickEvent event) {
		int ix = lbUnique.getSelectedIndex();
		if (ix < lbUnique.getItemCount() - 1) {
			String s = lbUnique.getItemText(ix);
			lbUnique.removeItem(ix++);
			lbUnique.insertItem(s, ix);
			lbUnique.setSelectedIndex(ix);
		}

	}
	@UiHandler("btnOk")
	void onBtnOkClick(ClickEvent event) {
		
		if (edMax.getValue()<1)
			Utils.getCF().showError("Maksymalna ilość powtórzeń nie może być mniejsza od 1");
		else {
			
			// Zapis listy pól informacyjnych
			JsArrayString arr = RareACareDef.getEmptyStringArray();
			for (int i=0; i<lbInfo.getItemCount(); i++)
				arr.push(lbInfo.getItemText(i));
			currentDoc.setInfoFields(arr);

			// Zapis listy pól unikalnych
			arr = RareACareDef.getEmptyStringArray();
			if (edMax.getValue() >1) {
			for (int i=0; i<lbUnique.getItemCount(); i++)
				arr.push(lbUnique.getItemText(i));
			} else if (lbUnique.getItemCount()>0)
				Utils.getCF().showMessage("Ponieważ maksymalna ilość dokumentów jest ustawiona na 1 (nie zezwala na powtarzanie dokumentów), lista unikalnych pól została wyczyszczona");
			currentDoc.setUniqueFields(arr);
			
			currentDoc.setMaxCount(edMax.getValue());

			hide();
		}
	}
}
