package com.blsoft.rareacare.client.ui;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.client.activity.IRegEditActivity;
import com.blsoft.rareacare.client.place.RegPtsListPlace;
import com.blsoft.rareacare.client.requestfactory.CommonReceiver;
import com.blsoft.rareacare.client.requestfactory.DocAttachProxy;
import com.blsoft.rareacare.client.requestfactory.DocumentDataRequest;
import com.blsoft.rareacare.client.requestfactory.FactProxy;
import com.blsoft.rareacare.client.requestfactory.RegUserProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryDataRequest;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocTreeItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.RareACareDef;
import com.blsoft.rareacare.client.ui.DocInstance.DocError;
import com.blsoft.rareacare.client.ui.interfaces.IRegEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.query.client.Function;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Label;
import com.blsoft.rareacare.client.ui.controls.BLButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.blsoft.rareacare.client.requestfactory.DocumentProxy;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.res.Images;
import com.blsoft.rareacare.shared.DocStatus;
import com.blsoft.rareacare.shared.RegRights;

public class RegEditor extends Composite implements IRegEditor {

	private static final Binder binder = GWT.create(Binder.class);
	@UiField FlowPanel docPanel;
	@UiField ScrollPanel docTreeScrollPanel;
	@UiField Label lbCaption;
	@UiField DocInstance docInstance;
	@UiField BLButton btnAdd;
	@UiField BLButton btnAttach;
	@UiField BLButton btnSave;
	@UiField BLButton btnConfirm;
	@UiField BLButton btnRevert;
	@UiField BLButton btnClose;
	@UiField BLButton btnEdit;
	@UiField BLButton btnDelete;

	private IRegEditActivity presenter;
	private Integer regId; // ID Rejestru
	private RareACareDef regDef;
	private DocInstanceControl selectedDoc;
	protected RegistryProxy regProxy;

	public RareACareDef getRegDef() {
		return regDef;
	};

	/** Wizualna kontrolka reprezentująca instancję dokumentu w drzewku po lewej
	 * stronie */
	public class DocInstanceControl extends Composite implements ClickHandler {

		private DocumentProxy doc = null;
		private DocInstanceControl parent = null;
		private Doc docDef;
		private DocTreeItem docTreeDef;
		private HTML mainPanel;
		public Map<String, JSONObject> facts;
		private String uniqueId; // Unikalny ID dokuentu w drzewie - z definicji
									// rejestru
		private Integer docId = null;
		private int ident;
		private DocStatus status = DocStatus.NEW;

		// private Boolean modified = false;

		void initItem(int idxInTree) {
			mainPanel = new HTML("<div class=\"gwt-ItemPanel-intern\">Jakiś dokument</div>");
			mainPanel.setStyleName("gwt-ItemPanel");
			initWidget(mainPanel);
//			mainPanel.setWidth("100%");
			addDomHandler(this, ClickEvent.getType());
			setStylePrimaryName("gwt-ItemPanel");

			docTreeDef = regDef.getDocTree().get(idxInTree);
			this.uniqueId = docTreeDef.getUniqueId();
			this.docDef = regDef.getDoc(docTreeDef.getId());

			// Gewnerowanie danych jako mapy JSONów
			this.facts = new HashMap<String, JSONObject>();
			if (doc != null) {
				for (FactProxy f : doc.getFacts()) {
					facts.put(f.getPath(), JSONParser.parseStrict(f.getContent()).isObject());
				}
				docId = doc.getId();
				status = doc.getStatus();
			}

			updateCaption();

			addToTree(this);
		}

		/** Aktualizacja caption dokumentu w drzewku na podstawie danych */
		@SuppressWarnings("deprecation")
		private void updateCaption() {
			JsArrayString flds = docTreeDef.getInfoFields(docDef);
			String sv = "";
			if (flds != null && flds.length() > 0) {
				for (int i = 0; i < flds.length(); i++) {
					String[] f = Utils.parseFieldPath(flds.get(i));
					JSONObject o = facts.get(f[0]);
					if (o != null && o.containsKey(f[1])) {
						sv += "<tr><td>" + Utils.escapeHTML(flds.get(i)) + "</td><td><b>" +
								Utils.escapeHTML(o.get(f[1]).isString().stringValue()) + "</b></td></tr>";
					}
				}
				String rt = "<div class=\"gwt-ItemPanel-intern\"><b>"
						+ Utils.escapeHTML(docDef.getName()) + "</b><br/>"
						+ Utils.escapeHTML(docDef.getDescr()) + "<br/>";

				if (!sv.isEmpty())
					rt += "<table border=\"0\" width=\"100%\"><tbody>" + sv + " </tbody></table>";
				
				if (doc !=null && doc.getAttachments() != null && !doc.getAttachments().isEmpty()) {					
					rt += "<b>Załączniki:</b><br/>";
					for (DocAttachProxy att : doc.getAttachments()) {
						rt +=   "<div id=\"divDel" + att.getId() + "\">"								
								+ "<img src=\"" + Images.INSTANCE.delete16().getURL() 
								+ "\" alt=\"-\" class=\"doc-attach-del-button\" style=\"width:16px;height:16px;cursor:pointer;\" id=\"btnDel" + att.getId() + "\">" 
								+  "<a href= \"getAttach?id=" + att.getId() + "\" target=\"_blank\" download=\"" + att.getName()  + "\">" + att.getName() + "</a><br/></div>";
					}
				}				
				
				mainPanel.setHTML(rt + "</div>");
				
				$(".doc-attach-del-button", mainPanel)
				.unbind(Event.ONCLICK)
				.click(new Function() {
					public boolean f(Event e) {
						Element target = e.getTarget();
						final Element dv = target.getParentElement();
						
						if (Utils.confirm("Czy jesteś pewien, że chcesz usunąć wskazany załącznik")) {
							int id = Integer.parseInt(target.getId().substring(6));
							((DivElement)target.getParentElement()).setInnerHTML("Usuwanie...");
							DocumentDataRequest req = Utils.getCF().getRequestFactory().documentDataRequest();
							req.removeAttachment(id).fire(new CommonReceiver<String>() {

								@Override
								public void onSuccess(String name) {
									if (name != "") { 
										Utils.showMessage("Usunięto załącznik: " + name);
										dv.removeFromParent();
									}
									else
										Utils.showMessage("Błąd w czasie usuwania załącznika");
								}
							});;
						}
						return true;
					}
				});

				
			}
		}

		/** Tworzy element na podtsaiwe odczytanej wartości. Wykorzystywane w
		 * czasie ładowania istniejących dokmentów z bazy */
		public DocInstanceControl(DocumentProxy aDoc) {
			this.doc = aDoc;
			// Szukam rodzica
			for (int i = 0; i < docPanel.getWidgetCount(); i++) {
				if (docPanel.getWidget(i) instanceof DocInstanceControl) {
					DocInstanceControl dc = (DocInstanceControl) docPanel.getWidget(i);
					if (dc.doc == aDoc.getParent()) {
						this.parent = dc;
						break;
					}
				}
			}
			initItem(regDef.getDocTreePos(aDoc.getUniqueId()));
		}

		/** Tworzy pusty nowy root */
		public DocInstanceControl() {
			initItem(0);
		}

		/** Dodaje do selectedDoc nowe puste dokumenty. Wykorzystywany w czasie
		 * dodawnaia nowych dokumentów */
		public DocInstanceControl(DocTreeItem docItem) {
			this.parent = selectedDoc;
			initItem(regDef.getDocTreePos(docItem.getUniqueId()));
		}

		@Override
		public void onClick(ClickEvent event) {
			selectDoc(this);
		}

		/** Odczyt faktów z edytora do kontrlki z oceną, czy uległy zmianie (to
		 * do sprawdzenia) */
		public void readFactsFromEditor() {
			Map<String, JSONObject> f = docInstance.getDocData();
			// if (!modified) {
			// Set<String> ksf = f.keySet();
			// if (facts == null) {
			// modified = true;
			// } else {
			// Set<String> ks = facts.keySet();
			// if (ks.size() != ksf.size())
			// modified = true;
			// else
			// for (String k : ksf)
			// if (!f.get(k).equals(facts.get(k))) {
			// modified = true;
			// break;
			// }
			// }
			// }
			//
			// if (modified)
			facts = f;
			updateCaption();
		}

		public void setIdent(int aIdent) {
			ident = aIdent;
			getElement().getStyle().setMarginLeft(ident * 2, Unit.EM);
		}
	}

	interface Binder extends UiBinder<Widget, RegEditor> {
	}

	public RegEditor() {
		initWidget(binder.createAndBindUi(this));
	}

	/** Dodaje kontrolkę do drzewa dokumentów; Gdy dodawana kontrolka nie ma
	 * rodziaca, lub dokumentu, lub dokument nie ma rodzica - to drzewo jest
	 * czyszczone i element jest dodawany jako root */
	public void addToTree(DocInstanceControl aDocCtrl) {
		if (aDocCtrl.parent == null) {
			clearTree();
			docPanel.add(aDocCtrl);
		}
		else {

			// Ustalam zakres idx w liście kontrolek, gdzie mam wstawiać
			// nową kontrolkę
			int parentIdx = -1, lastIdx = docPanel.getWidgetCount();
			int idt = 0;
			for (int i = 0; i < docPanel.getWidgetCount(); i++) {
				if (docPanel.getWidget(i) instanceof DocInstanceControl) {
					DocInstanceControl dc = (DocInstanceControl) docPanel.getWidget(i);
					if (dc == aDocCtrl.parent) {
						parentIdx = i;
						idt = dc.ident + 1;
					}
					else if (parentIdx >= 0 && dc.ident < idt) {
						lastIdx = i;
						break;
					}
				}
			}

			// TODO -poprawic ustalanie pozycji - obecnie daje w złej kolejności

			// ustalam, na jakiej pozycji wstawić na podstawie definicji drzewa
			// rejestru
			int posInDef = regDef.getDocTreePos(aDocCtrl.uniqueId);
			int docIdent = regDef.getDocTree().get(posInDef).getIdent();
			aDocCtrl.setIdent(docIdent);
			for (int i = posInDef - 1; i >= 0 && regDef.getDocTree().get(i).getIdent() == docIdent; i--) {
				for (int j = lastIdx - 1; j > parentIdx; j--) {
					if (docPanel.getWidget(j) instanceof DocInstanceControl) {
						if (((DocInstanceControl) docPanel.getWidget(i)).uniqueId.equals(regDef.getDocTree().get(i).getUniqueId())) {
							docPanel.insert(aDocCtrl, j + 1);
							return; // SUKCES - dodano
						}
					}
				}
			}

			// Nie znaleziono nieczego - dodaje zaraz na końcu noda
			docPanel.insert(aDocCtrl, lastIdx);
			return;
		}
	}

	private void clearTree() {
		selectedDoc = null;
		docPanel.clear();
	}

	/** Wybiera wskazany dokument */
	private void selectDoc(DocInstanceControl docInstanceControl) {
		if (docInstanceControl != selectedDoc) {
			if (selectedDoc != null) {
				if (docInstance.getEditable() && Utils.getCF().confirm("Aktualny dokument jest w trybie edycji. Czy chcesz zapisać ewentualne zmiany?")) {
					if (!saveCurrentDoc())
						return;
				}
				selectedDoc.setStyleDependentName("selected", false);
			}

			docInstance.clear();
			selectedDoc = docInstanceControl;

			if (selectedDoc != null) {
				// Ładowanie wskazanego dokumentu do edytora
				docInstance.loadDoc(selectedDoc.facts, selectedDoc.docDef, regDef.getElems(), false);
				selectedDoc.setStyleDependentName("selected", true);
			}
		}
		enableButtons();
	}

	private boolean saveCurrentDoc() {
		if (selectedDoc != null) {
			Map<Element, DocError> val = docInstance.showValidation();
			if (val.size() > 0) {
				Set<Element> ks = val.keySet();
				for (Element e : ks) {
					if (val.get(e).critical) {
						Utils.showError("Nie można zapisać dokumentu z powodu krytycznego błędu: " + val.get(e).msg);
						return false;
					}
				}
				if (!Utils.confirm("Dokument zawiera błędy. Czy chcesz go zapisać"))
					return false;
			}
			selectedDoc.readFactsFromEditor();
			selectedDoc.updateCaption();
			saveDoc(selectedDoc);
			return true;
		}
		return false;
	}

	@Override
	public void setPresenter(final IRegEditActivity presenter) {
		this.presenter = presenter;
		docInstance.clear();
		clearTree();

		regId = presenter.getPlace().getRegId();

		// Inicjowanie operacji dodawania do cache definicji rejestru
		regProxy = Utils.getRegDefFromCache(regId);
		if (regProxy == null) {
			RegistryDataRequest req = Utils.getCF().getRequestFactory().registryDataRequest();
			req.findRegistry(presenter.getPlace().getRegId()).with("currRegUser", "currRegUser.user", "owner").fire(
					new CommonReceiver<RegistryProxy>() {
						public void onSuccess(RegistryProxy u) {
							Utils.setRegDefToCache(regId, u);
							regProxy = u;
							setRegDef((RareACareDef) RareACareDef.createFromString(u.getDef()));
							loadDocuments();
						}
					});
		}
		else {
			setRegDef((RareACareDef) RareACareDef.createFromString(regProxy.getDef()));
			loadDocuments();
		}

	}

	private void setRegDef(RareACareDef regDef) {
		this.regDef = regDef;
		lbCaption.setText(regDef.getName());
	}

	/** Ładuje wszystkie dokumenty powiązane z wybranym dokumentem-rootem */
	protected void loadDocuments() {
		// Jeśli nie ma roota - wydano polecnie dodania nowego dokumentu
		if (presenter.getPlace().getInstId() == 0) {
			selectDoc(new DocInstanceControl());
			editDoc();
		}
		else {
			// Jesli jest - laduje wszystkie i buduję drzewo
			DocumentDataRequest req = Utils.getCF().getRequestFactory().documentDataRequest();
			req.getDocs(presenter.getPlace().getInstId()).with("facts", "parent", "attachments", "creator").fire(
					new CommonReceiver<List<DocumentProxy>>() {

						@Override
						public void onSuccess(List<DocumentProxy> response) {
							buildTreeAndShow(response);
							selectDoc(getRoot());
						}
					}
					);
		}

	}

	/** Buduje graficzne drzewo instancji dokumentów na podstawie zaczytanych
	 * danych */
	protected void buildTreeAndShow(List<DocumentProxy> docs) {

		docPanel.clear();

		// / Szukam Roota i inicjuje od niego drzewo
		for (int i = 0; i < docs.size(); i++)
			if (docs.get(i).getParent() == null) {
				buildTree(docs, docs.get(i));
				break;
			}
	}

	private void buildTree(List<DocumentProxy> docs, DocumentProxy parent) {

		// Dodaje parenta

		new DocInstanceControl(parent);

		// int ct = docs.size();
		// int fct = docs.get(0).getFacts().size();
		// int fct2 = docs.get(1).getFacts().size();
		// Utils.getCF().showMessage(Utils.format("ct: {0} f1: {1};  f2: {2}",
		// ct, fct, fct2));
		//

		// Filtruję bepośrednie dzieci nodu
		ArrayList<DocumentProxy> ls = new ArrayList<DocumentProxy>();
		for (DocumentProxy d : docs) {
			DocumentProxy p = d.getParent();
			if (p != null && p.equals(parent))
				ls.add(d);
		}

		// Dodaje do panelu drzewa
		for (int i = 0; i < ls.size(); i++) {
			buildTree(docs, ls.get(i));
		}

	}

	@UiHandler("btnAdd")
	/** Dodawnaie nowego dokumentu */
	void onBtnAddClick(ClickEvent event) {
		enableButtons();
		if (btnAdd.isEnabled() && regDef.getDocTree().length() > 0) {
			if (getRoot() == null) {
				// Dodawnaie nowgo roota
				new DocInstanceControl();
			} else {
				// Dodawanie wybranych dokumentów do zaznaczonego nodu
				RegEditorNewDoc.show(this);
			}
		}
	}

	@UiHandler("btnAttach")
	/** Dodawnaie nowego załącznika */
	void onBtnAttachClick(ClickEvent event) {
		enableButtons();
		if (btnAttach.isEnabled()) {
			RegEditorAttachUploader.show(this.selectedDoc.doc.getRootId().toString() + '#' + this.selectedDoc.doc.getId().toString(), this);
		}
	}

	/** Zamkniecie formularza i powrót do listy pacjentów */
	@UiHandler("btnClose")
	void onBtnCloseClick(ClickEvent event) {
		closeForm();
	}

	private void closeForm() {
		if (selectedDoc != null && docInstance.getEditable() && Utils.getCF().confirm("Aktualny dokument jest w trybie edycji. Czy chcesz zapisać ewentualne zmiany?")) {
			if (!saveCurrentDoc())
				return;
		}
		presenter.goTo(new RegPtsListPlace(presenter.getPlace().getRegId().toString()));
	}

	/** Zapis dokumentu do bazy */
	void saveDoc(final DocInstanceControl dic) {

		if (dic != null) {

			boolean docIsNew = false;

			DocumentDataRequest req = Utils.getCF().getRequestFactory().documentDataRequest();
			DocumentProxy doc = dic.doc;
			if (doc == null) {
				doc = req.create(DocumentProxy.class);
				docIsNew = true;
			}
			else
				doc = req.edit(doc);

			// Ustalanie ścieżki
			DocInstanceControl cd = dic;
			String path = "";
			do {
				path = '/' + cd.docDef.getId()
						+ (cd == dic ? getSubValues(dic) : "")
						+ path;
			} while ((cd = cd.parent) != null);

			doc.setDocDefId(dic.docDef.getId());
			doc.setPath(path);
			if (dic.parent != null)
				doc.setParent(dic.parent.doc);
			doc.setRegistry(Utils.getRegDefFromCache(regId));
			doc.setUniqueId(dic.uniqueId);
			doc.setStatus(dic.status);
			// doc.setUniqueSubValues("");

			DocInstanceControl rt = getRoot();
			if (rt != null)
				doc.setRootId(rt.docId);
			;

			req.persistAndLoad(doc, false); // .with("facts")

			// Teraz usuwanie starych faktów
			if (!docIsNew)
				req.removeFactsFromDoc(doc.getId());

			if (doc.getFacts() != null) {
				// List<FactProxy> fct = doc.getFacts();
				// for (int i = 0; i < fct.size(); i++)
				// req.removeFact(fct.get(i));
				doc.getFacts().clear();
			}
			// Dodawanie nowych
			Set<String> ks = dic.facts.keySet();
			for (String k : ks) {
				FactProxy fact = req.create(FactProxy.class);
				JSONObject js = dic.facts.get(k);
				fact.setLp((int) js.get("__lp").isNumber().doubleValue());
				js.put("__lp", null);
				fact.setContent(js.toString());
				fact.setDocument(doc);
				fact.setPath(k);
				req.persistFact(fact);
			}

			req.find(doc.stableId()).fire(new Receiver<DocumentProxy>() {

				@Override
				public void onSuccess(DocumentProxy response) {
					DocumentDataRequest req = Utils.getCF().getRequestFactory().documentDataRequest();
					req.getDocWithFacts(response.getId()).with("facts", "attachments", "creator").fire(new CommonReceiver<DocumentProxy>() {

						@Override
						public void onSuccess(DocumentProxy response) {
							dic.doc = response;
							dic.docId = response.getId();
							// Utils.getCF().showMessage(
							// response.getId().toString() + " ilość dzieci: " +
							// (response.getFacts() == null ? "NULL" :
							// ((Integer) response.getFacts()
							// .size()).toString()));
							docInstance.loadDoc(selectedDoc.facts, selectedDoc.docDef, regDef.getElems(), false);
							enableButtons();
						}
					});
				}
			});
			enableButtons();
		}
	}

	private String getSubValues(DocInstanceControl dic) {
		DocTreeItem dti = regDef.getDocTree().get(regDef.getDocTreePos(dic.uniqueId));
		JsArrayString flds = dti.getUniqueFields(dic.docDef);
		String sv = "";
		if (flds != null && flds.length() > 0) {
			for (int i = 0; i < flds.length(); i++) {
				String[] f = Utils.parseFieldPath(flds.get(i));
				JSONObject o = dic.facts.get(f[0]);
				if (o != null && o.containsKey(f[1]))
					sv += (sv.isEmpty() ? "" : ",") + o.get(f[1]).isString().stringValue();
			}
			if (!sv.isEmpty())
				return '(' + sv + ')';
		}
		return "";
	}

	private DocInstanceControl getRoot() {
		for (int i = 0; i < docPanel.getWidgetCount(); i++) {
			if (docPanel.getWidget(i) instanceof DocInstanceControl) {
				DocInstanceControl rt = (DocInstanceControl) docPanel.getWidget(i);
				if (rt.parent == null) {
					return rt;
				}
			}
		}
		return null;
	}

	/** Zapis wszystkich zmodyfikowanych dokumentów do bazy */
	@UiHandler("btnSave")
	void onBtnSaveClick(ClickEvent event) {
		enableButtons();
		if (btnSave.isEnabled())
			saveCurrentDoc();
		enableButtons();
	}

	/** Zatwierdzanie dokumentu */
	@UiHandler("btnConfirm")
	void onBtnConfirmClick(ClickEvent event) {
		enableButtons();
		if (selectedDoc != null && btnConfirm.isVisible() && btnConfirm.isEnabled()) {
			if (docInstance.showValidation().size() == 0) {
				if (Utils.confirm("Czy jesteś pewien, że chcesz zatwierdzić dokument?")) {
					selectedDoc.status = DocStatus.CONFIRMED;
					selectedDoc.readFactsFromEditor();
					saveDoc(selectedDoc);
				}
			} else {
				Utils.showError("Nie można zatwierdzić dokumentu, gdyż zawiera on błędy");
			}
		}
	}

	/** Zatwierdzanie dokumentu/cofanie zatwierdzenia */
	@UiHandler("btnRevert")
	void onBtnRevertClick(ClickEvent event) {
		enableButtons();
		if (selectedDoc != null && btnRevert.isVisible() && btnRevert.isEnabled()) {
			if (selectedDoc.status == DocStatus.CONFIRMED) {
				if (Utils.confirm("Czy jesteś pewien, że chcesz skorygować dokument?")) {
					selectedDoc.status = DocStatus.CORRECTING;
					editDoc();
				}
			}
		}
	}

	/** Zapis wszystkich zmodyfikowanych dokumentów do bazy */
	@UiHandler("btnEdit")
	void onBtnEditClick(ClickEvent event) {
		editDoc();
	}

	private void editDoc() {
		enableButtons();
		if (selectedDoc != null && !docInstance.getEditable() && btnEdit.isEnabled()) {
			docInstance.loadDoc(selectedDoc.facts, selectedDoc.docDef, regDef.getElems(), true);
			enableButtons();
		}
	}

	/** Usuwanie aktywnego dokumentu */
	@UiHandler("btnDelete")
	void onBtnDeleteClick(ClickEvent event) {
		enableButtons();
		if (selectedDoc != null && docInstance.getEditable() && btnDelete.isEnabled() && Utils.getCF().confirm(
				"Czy jestes pewien, że chcesz usunąć trwale dokument? (Operacja ta jest nieodwracalna)")) {

			final DocInstanceControl sdoc = selectedDoc;
			// Czy dokument nie ma dzieci
			for (int i = 0; i < docPanel.getWidgetCount(); i++) {
				if (docPanel.getWidget(i) instanceof DocInstanceControl) {
					DocInstanceControl dc = (DocInstanceControl) docPanel.getWidget(i);
					if (dc.parent == sdoc) {
						Utils.getCF().showError("Wybrany dokument ma dokumenty podrzędne i nie może być usunięty. Usuń najpierw dokumenty podrzędne.");
						return;
					}
				}
			}

			// usuwam - jeśli ma Doc, to równiez w bazie; Inaczej jedynie w
			// kliencie
			docInstance.clear(); // To daję na poczatku, by nie bytał o zapis
			selectDoc(null);
			if (sdoc.docId != null) {
				DocumentDataRequest req = Utils.getCF().getRequestFactory().documentDataRequest();
				req.removeDoc(sdoc.docId).fire(new CommonReceiver<Void>() {

					@Override
					public void onSuccess(Void response) {
					}

					@Override
					public void onFailure(ServerFailure error) {

						// W razi ebłedu podczas usuwania - przesyłam o tym
						// informacje i od nowa wszystki ładuję
						Utils.getCF().showMessage("Błąd w czasie usuwania dokumentu na serwerze: " + error.getMessage());
						loadDocuments();
					}
				});
			}

			docPanel.remove(sdoc);

			if (sdoc.parent == null)
				closeForm();
			else
				enableButtons();
		}
	}

	/** Funkcja enabluje odpowiednio klawisze i inne kontrolki */
	private void enableButtons() {
		RegUserProxy u = regProxy.getCurrRegUser();
//		DocInstanceControl sd = selectedDoc;
//		DocumentProxy dc = sd.doc;
//		UserProxy cr = dc==null ? null : dc.getCreator();
		
		boolean canEdit = (u.getCanEdit() == RegRights.ALL
				|| u.getCanEdit() == RegRights.OWN && (selectedDoc.doc == null || selectedDoc.doc.getCreator().getId() == Utils.getUserId()));
		btnEdit.setEnabled(selectedDoc != null && selectedDoc.status != DocStatus.CONFIRMED && !docInstance.getEditable() && selectedDoc != null && canEdit);
		btnSave.setEnabled(docInstance.getEditable() && selectedDoc != null && canEdit);
		btnDelete.setEnabled(docInstance.getEditable() && selectedDoc != null && canEdit);
		btnAdd.setEnabled(!docInstance.getEditable() && selectedDoc != null && (u.getCanEdit() == RegRights.OWN || u.getCanEdit() == RegRights.ALL));
		btnAttach.setEnabled(selectedDoc != null && selectedDoc.status != DocStatus.CONFIRMED && !docInstance.getEditable() && selectedDoc != null && canEdit);
		btnConfirm.setVisible(selectedDoc == null || selectedDoc.status != DocStatus.CONFIRMED);
		btnConfirm.setEnabled(selectedDoc != null && canEdit && (selectedDoc.status != DocStatus.CONFIRMED && docInstance.getEditable()));

		btnRevert.setVisible(selectedDoc != null && selectedDoc.status == DocStatus.CONFIRMED);
		btnRevert.setEnabled(selectedDoc != null && canEdit
				&& (selectedDoc.status == DocStatus.CONFIRMED
				&& (u.getCanCorrect() == RegRights.ALL || u.getCanCorrect() == RegRights.OWN && (selectedDoc.doc == null || selectedDoc.doc.getCreator()
						.getId() == Utils.getUserId()))));
	}

	/** Zwraca liste elementów drzewa dokumentów, które można dodać do wybranego
	 * dokumentu jako dzieci I rzędu. Analizuje definicje drzewa dokumentów z
	 * uwazględnieniem maksymalnej dopuszczalnej ilośći dokumentów okreslonego
	 * typu w gałęzi */
	public List<DocTreeItem> getDocsToAdd() {
		List<DocTreeItem> ls = new ArrayList<DocTreeItem>();
		if (selectedDoc != null) {
			int start = regDef.getDocTreePos(selectedDoc.uniqueId);
			JsArray<DocTreeItem> docTree = regDef.getDocTree();
			int ident = docTree.get(start).getIdent() + 1;
			DocTreeItem currItem;
			for (int i = start + 1; i < docTree.length() && (currItem = docTree.get(i)).getIdent() >= ident; i++) {
				if (currItem.getIdent() == ident && currItem.getMaxCount(regDef.getDoc(currItem.getId())) > getDocCount(currItem.getId()))
					ls.add(docTree.get(i));
			}
		}
		return ls;
	}

	/** Zwraca ilość dodanych dokumentów okreslonego typu dla aktualnego nodu */
	private int getDocCount(String id) {
		int start = docPanel.getWidgetIndex(selectedDoc) + 1;
		DocInstanceControl w;
		int res = 0;
		int idt = selectedDoc.ident + 1;
		for (int i = start; i < docPanel.getWidgetCount() && (w = (DocInstanceControl) docPanel.getWidget(i)).ident >= idt; i++) {
			if (w.ident == idt && w.docDef.getId().equals(id))
				res++;
		}
		return res;
	}

	/** Dodawanie nowego dokumentu do już istniejącego drzewa */
	public void addNewDoc(DocTreeItem docItem) {
		if (docItem != null) {
			selectDoc(new DocInstanceControl(docItem));
			editDoc();
		}
	}
}
