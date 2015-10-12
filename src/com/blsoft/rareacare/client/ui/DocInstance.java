package com.blsoft.rareacare.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.blsoft.rareacare.client.Utils;

import static com.blsoft.rareacare.client.Utils.*;

import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Doc;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.DocItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.Elem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.ElemItem;
import com.blsoft.rareacare.client.requestfactory.RegistryProxy.ValueKey;
import com.blsoft.rareacare.client.res.DocElemTemplates;
import com.blsoft.rareacare.shared.ItemKind;
import com.blsoft.rareacare.shared.RegElemDataType;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;

import static com.google.gwt.query.client.GQuery.*;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTMLPanel;

/** Kontrolka do edycji dokumentu - buduje VIEW na podstawie definicji
 * 
 * @author bartek */
public class DocInstance extends Composite {
	private Doc doc;
	private JsArray<Elem> elems;

	private FlowPanel mainPanel;
	private Label lbDocName;
	private Label lbDocDescr;
	private HTMLPanel elemsPanel;
	private String htm;
	private Map<String, JSONObject> facts;
	private Boolean editable = false;
	private boolean validationEnabled = false;

	public Boolean getEditable() {
		return editable;
	}

	public DocInstance() {
		mainPanel = new FlowPanel();
		initWidget(mainPanel);
		mainPanel.setWidth("100%");
	}

	public DocInstance(Doc doc, JsArray<Elem> elems) {
		this();
		init(doc, elems);
	}

	public void init(Doc doc, JsArray<Elem> elems) {
		this.doc = doc;
		this.elems = elems;
		clear();
		buildDoc();
	}

	private void buildDoc() {

		lbDocName = new Label(doc.getName());
		lbDocName.setStyleName("doc-name");
		mainPanel.add(lbDocName);
		validationEnabled = false;

		if (!doc.getDescr().isEmpty()) {
			lbDocDescr = new HTML(doc.getDescr());
			lbDocDescr.setStyleName("doc-descr");
			mainPanel.add(lbDocDescr);
		}

		// Budowanie elementów
		htm = "";
		buildDocItems(doc.getItems(), "", "", true);
		htm = "    <div class=\"doc-back\">\r\n" +
				"    <div class=\"doc-all\">\r\n" +
				htm +
				"</div></div>";

		String sss = htm;
		elemsPanel = new HTMLPanel(sss);
		mainPanel.add(elemsPanel);

		// Wypełnianie danymi
		if (facts != null && !facts.isEmpty()) {
			Set<String> pf = facts.keySet();
			for (String p : pf) {
				JSONObject data = facts.get(p);
				Element parentElem = getParentElemFromPath(p);
				if (data != null) {
					Set<String> ks = data.keySet();
					for (String k : ks) {
						JSONValue v = data.get(k);
						if (v != null) {

							GQuery allInputs = $("input, textarea, select, label", parentElem);

							for (int i = 0; i < allInputs.length(); i++) {
								Element e = allInputs.get(i);
								if  (e.getAttribute("data-name").equals(k) && getParentHeader(e) == parentElem) {
									String tagName = e.getTagName();
									if (tagName.equals("SELECT")) {
										NodeList<OptionElement> op = ((SelectElement) e).getOptions();
										JSONArray ar = v.isArray(); // Multiselect z
																	// Array
										if (ar != null) {
											for (int ai = 0; ai < ar.size(); ai++) {
												String ss = ar.get(ai).isString().stringValue();
												for (int x = 0; x < op.getLength(); x++) {
													if (op.getItem(x).getValue().equals(ss))
														op.getItem(x).setSelected(true);
												}
											}
										} else {
											JSONString s = v.isString(); // Not
																			// Multiselect
																			// -
																			// wartość
																			// jako
																			// String
											if (s != null) {
												String ss = s.stringValue();
												for (int x = 0; x < op.getLength(); x++) {
													if (op.getItem(x).getValue().equals(ss))
														op.getItem(x).setSelected(true);
												}
											}
										}
									} else if (e.getAttribute("type").equals("radio")) {
										JSONString s = v.isString();
										String ss;
										if ((s != null) && ((ss = s.stringValue()) != null)) {
											InputElement ee = (InputElement) e;
											if (ee.getValue().equals(ss))
												ee.setChecked(true);
										}
									} else {
										JSONString s = v.isString();
										String ss;
										if ((s != null) && ((ss = s.stringValue()) != null)) {
											if (tagName.equals("INPUT") || tagName.equals("TEXTAREA")) {
												InputElement ee = (InputElement) e;
												if (ee.getType().equals("checkbox"))
													ee.setChecked(ss.equals("T"));
												else
													ee.setValue(ss);
											} else if (tagName.equals("LABEL")) {
												LabelElement ee = (LabelElement) e;
												ee.setInnerHTML(Utils.escapeHtmlNl(ss));
												
											}
										}

									}
								}
							}
						}
					}
				}			
			}
			
			// Usuwam wszystkie elementy, dla których zaimportowano dane
			$(".doc-header[data-delete=\"true\"]", elemsPanel).remove();
		}

		addEvents(mainPanel.getElement());

		// Ustalanie Editable

		if (!editable) {
			$("input, textarea", mainPanel).attr("readonly", "true");
			$("select, input[type='radio']", mainPanel).attr("disabled", "true");
		}
		
		// Inicjacja DateTimePickerów (poprzez klasyczny JS, bo sa to kontrolki klasyczne JavaScript
		Utils.updateDateTimePicker();

		// elemsPanel = new HTMLPanel("");
		// mainPanel.add(elemsPanel);
		// buildElems(doc.getItems());
		//
	}

	private Element getParentElemFromPath(String p) {
		if (p.isEmpty())
			return elemsPanel.getElement();
		String[] ar = p.split("/");
		Element parent = elemsPanel.getElement();
				
		for (int i=0; i<ar.length;i++) {
			GQuery ls = $(".doc-header[data-fullname=\"" + ar[i] + "\"]", parent);
			int l;
			for(l=0; l<ls.length(); l++) {
				if (getParentHeader(ls.get(l)) == parent) 
					parent = ls.get(l);					
			}
			if (l==ls.length()) {
				String nm = ar[i];
				int ix = nm.indexOf('(');
				if (ix>=0)
					nm = nm.substring(0, ix);
				ls = $(".doc-header[data-name=\"" + nm + "\"]", parent);
				for(l=0; l<ls.length(); l++) {
					if (getParentHeader(ls.get(l)) == parent) {
						
						// Tworzę kopię na podstawie template
						parent = Element.as(ls.get(l).cloneNode(true));
						
						//Szukam ostatniego takiego samego
						Element lastEl = ls.get(l);
						Element ee = ls.get(l).getNextSiblingElement();
						while(ee != null && ee.getAttribute("data-name").equals(nm)) {
							lastEl = ee;
							ee = ee.getNextSiblingElement();
						}

						// Wstawiam element
						lastEl.getParentElement().insertAfter(parent, lastEl);
						parent.setAttribute("data-fullname", ar[i]);
						parent.setAttribute("data-delete", "false");  
						ls.get(l).setAttribute("data-delete", "true");  // Zaznaczam do usunięcia template, bo jest wersja z danymi
						
						// ZMieniam nazwę radiobuttons, by nie zaznaczały się wspólnie z tymi z rónoległych wstęg
						GQuery lr = $("input[type=\"radio\"]", parent);
						for(int r=0; r< lr.length();r++) {
							InputElement rr = (InputElement) lr.get(r);
							if (getParentHeader(rr) == parent) {
								nm = rr.getName();
								nm += '|' + ar[i];
								rr.setName(nm);
							}
						}
						
						break;
					}
					if (l==ls.length())
						return null; // Jakis bład w ścieżkach - nie dodaję
				}
			}
		}
		return parent;
	}


	/** Dodaje event klawisza dodwania i usuwania elementu */
	private void addEvents(Element parent) {
		// dodawanie eventów
		$(".doc-header-add-button", parent)
				.unbind(Event.ONCLICK)
				.click(new Function() {
					public boolean f(Event e) {
						// Element target = e.getTarget();
						Element target = Element.as(e.getEventTarget());
						addDomElement(target.getParentElement().getParentElement(), target.getId().substring(6));
						return true;
					}
				});

		$(".doc-header-del-button", parent)
		.unbind(Event.ONCLICK)
		.click(new Function() {
			public boolean f(Event e) {

				if (!Utils.getCF().confirm("Czy jeteś pewien, że chcesz usunąć element z dokumentu?"))
					return true;

				Element target = Element.as(e.getEventTarget());
				target.getParentElement().getParentElement().removeFromParent();
				return true;
			}
		});
		
//		GQuery ls = $("input[data-name<>\"\"], textarea[data-name<>\"\"], select[data-name<>\"\"]", parent);
		GQuery ls = $("input, textarea, select", parent);
		ls.unbind(Event.ONBLUR);
		ls.bind(Event.ONBLUR, new Function() {
			public boolean f(Event e) {
				if (validationEnabled)
					showValidation();
				return true;
			}
			
		});
		
	}

	/** Wykonuje walidację dokumentu i oznacza błedne elementy na na dokumencie. Zwraca mapę błędnych kontrolek z treścią błędu */ 
	protected Map<Element, DocError> showValidation() {
		Map<Element, DocError> er = validateDoc();
		Set<Element> ls = er.keySet();
		validationEnabled  = true;
		
		// Usuwam stare labelki z błedami
		$(".input-error-label, .input-error-label-critical", elemsPanel).remove();
		
		// Dodaję nowe labelki z błędami
		for(Element e : ls) {
			LabelElement ne = LabelElement.as(DOM.createLabel());
			DocError docError = er.get(e);
			ne.setInnerText(docError.msg);
			ne.setClassName((docError.critical?"input-error-label" : "input-error-label-critical"));
			e.getParentElement().insertBefore(ne, e);
			Element nb = DOM.createElement("br");
			nb.setClassName((docError.critical?"input-error-label" : "input-error-label-critical"));
			e.getParentElement().insertAfter(nb, ne);
		}
		return er;
	}

	private native void setOuterHTML(Element e, String html) /*-{
																e.outerHTML = html;
																}-*/;

	private void buildDocItems(JsArray<DocItem> items, String id, String headKey, boolean addAddButton) {

		for (int i = 0; i < items.length(); i++) {
			DocItem it = items.get(i);
			switch (it.getKind()) {
			case Comment:
				htm += format("<table border=\"0\" class=\"doc-comment\">\n" +
						"<tbody>\n" +
						"  <tr>\n" +
						"    <td class=\"doc-comment-label\">{0}</td>\n" +
						"    <td class=\"doc-comment-comment\">{1}</td>\n" +
						"  </tr>\n" +
						"</tbody></table>",
						escapeHtmlNl(it.getLabel()), escapeHtmlNl(it.getDescr()));
				break;

			case Separator:
				htm += DocElemTemplates.INSTANCE.Separator().getText();
				break;

			case Header:

				String headId = joinItems(id, it.getId() + (headKey.isEmpty() ? "" : '(' + headKey + ')'));

				if (editable && it.getRepeatMax() > it.getRepeatMin() && (addAddButton || i > 0)) 
					htm += "<div class=\"doc-header-add-button\"><button type=\"button\" id=\"btnAdd" + headId + "\">Dodaj: " + escapeHtmlNl(it.getLabel()) + "</button></div>";

				htm += "<div class=\"doc-header\" data-name=\"" + it.getId() + "\" data-fullname=\"" + it.getId() + (headKey.isEmpty() ? "" : '(' +escapeHTML(headKey) + ')') + "\" data-pk=\"" + escapeHTML(it.getPrimaryKey()) + "\">";

				if (editable && it.getRepeatMax() > it.getRepeatMin())
					htm += "<div class=\"doc-header-del-button\"><button type=\"button\" id=\"btnRemove" + headId + "\">Usuń</button></div>";

				if (it.getLabel() != null || it.getDescr() != null)
					htm += Utils.format("<table style=\"width:100%\" border=\"0\"><tbody>\n" +
							"  <tr>\n" +
							"    <td class=\"doc-header-label\">{0}</td>\n" +
							"    <td class=\"doc-header-comment\">{1}</td>\n" +
							"  </tr>\n" +
							"</tbody></table>", escapeHtmlNl(it.getLabel()), escapeHtmlNl(it.getDescr()));

				buildDocItems(it.getItems(), headId, "", true);

				htm += "\n</div>"; // Koniec calości
				break;

			case Elem:
				htm += "<div class=\"doc-elem\">";
				// Naglówek elementu
				if (it.getLabel() != null || it.getDescr() != null)
					htm += Utils.format("<table border=\"0\" style=\"width:100%\"><tbody>\n" +
							"  <tr>\n" +
							"    <td class=\"doc-elem-label\">{0}</td>\n" +
							"    <td class=\"doc-elem-comment\">{1}</td>\n" +
							"  </tr>\n" +
							"</tbody></table>",
							escapeHtmlNl(it.getLabel()), escapeHtmlNl(it.getDescr()));

				// Szukam definicji elementu
				for (int j = 0; j < elems.length(); j++) {
					String itemId;
					Elem elem = elems.get(j);
					if (elem.getId().equals(it.getId())) {

						// Renderuję wskazany element
						for (int k = 0; k < elem.getItems().length(); k++) {
							ElemItem eIt = elem.getItems().get(k);
							String itemName = joinItems(it.getId(), eIt.getId());
							itemId = id + '|' + itemName;
							switch (eIt.getKind()) {

							case Comment:
								htm += Utils.format("<table border=\"0\" class=\"doc-elemItem-comment\"><tbody>\n" +
										"  <tr>\n" +
										"    <td class=\"doc-elemItem-label\">{0}</td>\n" +
										"    <td class=\"doc-elemItem-comment-comment\">{1}</td>\n" +
										"  </tr>\n" +
										"</tbody></table>",
										escapeHtmlNl(eIt.getLabel()), escapeHtmlNl(eIt.getDescr()));
								break;

							case CheckBox:
								htm += format(
										"    <table class=\"doc-elemItem\" border=\"0\">\n" +
												"      <tbody>\n" +
												"        <tr>\n" +
												"          <td class=\"doc-elemItem-label\">{0}</td>\n" +
												"          <td class=\"doc-elemItem-body\"><input name=\"{1}\" id=\"{1}\" data-name=\"{2}\" type=\"checkbox\" ></td>\n" +
												"          <td class=\"doc-elemItem-comment\">{3}</td>\n" +
												"        </tr>\n" +
												"      </tbody>\n" +
												"    </table>", escapeHtmlNl(eIt.getLabel()), itemId, itemName,  escapeHtmlNl(eIt.getDescr()));
								break;

							case Combo:
							case List:

								String comboItems = "";
								{
									for (int l = 0; l < eIt.getItems().length(); l++) {
										ValueKey itm = eIt.getItems().get(l);
										comboItems +=
												"<option value=\"" + (isEmpty(itm.getKey()) ? escapeHTML(itm.getValue()) : escapeHTML(itm.getKey())) + "\">" + escapeHtmlNl(itm
														.getValue()) + "</option>\n";
									}
								}

								htm +=
										"<table class=\"doc-elemItem\" border=\"0\">\n" +
												"  <tbody>\n" +
												"    <tr> \n" +
												"      <td class=\"doc-elemItem-label\">" + escapeHtmlNl(eIt.getLabel()) + "</td>\n" +
												"      <td class=\"doc-elemItem-body\"><select name=\"" + itemId + "\" data-name=\"" + itemName + "\" id=\"" + itemId + "\"" +
												((eIt.getKind() == ItemKind.List) ? " multiple=\"true\"" : "") + ">" +
												comboItems + "</select> </td>\n" +
												((!isEmpty(eIt.getDescr())) ? "      <td class=\"doc-elemItem-comment\">"
														+ escapeHtmlNl(eIt.getDescr()) + "</td>\n" : "") +
												"    </tr>\n" +
												"  </tbody>\n" +
												"</table>";

								break;

							case Radios:
								String radios = "";
								{
									for (int l = 0; l < eIt.getItems().length(); l++) {
										ValueKey itm = eIt.getItems().get(l);
										String key = isEmpty(itm.getKey()) ? escapeHTML(itm.getValue()) : escapeHTML(itm.getKey());
										radios +=
												"<span class=\"doc-elemItem-radio\"><input type=radio data-name=\"" + itemName + "\" name=\"" + itemId 
														+ "\" value=\"" + key + "\" " //id=\"" + itemId + '|' + l + '"'
														+ ">" + escapeHTML(itm.getValue()) + "<BR></span>\n";

									}
								}

								htm +=
										"<table class=\"doc-elemItem\" border=\"0\">\n" +
												"  <tbody>\n" +
												"    <tr> \n" +
												"      <td class=\"doc-elemItem-label\">" + escapeHtmlNl(eIt.getLabel()) + "</td>\n" +
												"      <td class=\"doc-elemItem-body\">\n" + radios + "</td>\n" +
												((!isEmpty(eIt.getDescr())) ? "      <td class=\"doc-elemItem-comment\">"
														+ escapeHtmlNl(eIt.getDescr()) + "</td>\n" : "") +
												"    </tr>\n" +
												"  </tbody>\n" +
												"</table>";

								break;

							case Edit:

								String tp = "text";
								String cls= "";
								switch (eIt.getDataType()) {

								case DATE:
									cls = editable ? "datepicker" : "";
									break;
								case DATETIME:
									cls = editable ? "datetimepicker" : "";
									break;
								case NUMBER:
									tp = "number";
									break;
								case TIME:
									cls = editable ? "timepicker" : "";
									break;
								default:
									break;
								}

								if (editable)
									htm +=
										"<table class=\"doc-elemItem\" border=\"0\">\n" +
												"  <tbody>\n" +
												"    <tr> \n" +
												"      <td class=\"doc-elemItem-label\">" + escapeHtmlNl(eIt.getLabel()) + "</td>\n" +
												"      <td class=\"doc-elemItem-body\"><input id=\"" + itemId +
												"\" class=\"" + cls + "\"  type=\""+ tp + "\" data-name = \"" + itemName + "\" placeholder=\"" + escapeHtmlNl(eIt.getPlaceHolder()) + "\"></td>\n" +
												"      <td class=\"doc-elemItem-comment\"><table style=\"width:100%\"><tr>" +
												(isEmpty(eIt.getUnits()) ? "" : "<td>" + escapeHtmlNl(eIt.getUnits()) + "</td>\n") +
												((eIt.hasNorm()) ? "      <td class=\"doc-elemItem-norm\">" + escapeHtmlNl(eIt.getNorm()) + "</td>\n" : "") +
												((!isEmpty(eIt.getDescr())) ? "      <td class=\"doc-elemItem-comment\">" + escapeHtmlNl(eIt.getDescr()) + "</td>\n" : "") +
												"      </tr></table></td>\n" +
												"    </tr>\n" +
												"  </tbody>\n" +
												"</table>";
								else
									htm +=
										"<table class=\"doc-elemItem\" border=\"0\">\n" +
											"  <tbody>\n" +
											"    <tr> \n" +
											"      <td class=\"doc-elemItem-label\">" + escapeHtmlNl(eIt.getLabel()) + "</td>\n" +
											"      <td class=\"doc-elemItem-body\"><label id=\"" + itemId +
											"\" class=\"" + cls + "\"  type=\""+ tp + "\" data-name = \"" + itemName + "\"></label></td>\n" +
											"      <td class=\"doc-elemItem-comment\"><table style=\"width:100%\"><tr>" +
											(isEmpty(eIt.getUnits()) ? "" : "<td>" + escapeHtmlNl(eIt.getUnits()) + "</td>\n") +
											((eIt.hasNorm()) ? "      <td class=\"doc-elemItem-norm\">" + escapeHtmlNl(eIt.getNorm()) + "</td>\n" : "") +
											((!isEmpty(eIt.getDescr())) ? "      <td class=\"doc-elemItem-comment\">" + escapeHtmlNl(eIt.getDescr()) + "</td>\n" : "") +
											"      </tr></table></td>\n" +
											"    </tr>\n" +
											"  </tbody>\n" +
											"</table>";
								break;

							case Memo:
								
								if (editable) 
									htm +=
										"<table class=\"doc-elemItem\" border=\"0\">\r\n" +
												"      <tbody>\r\n" +
												"        <tr>\r\n" +
												"          <td class=\"doc-elemItem-label\">" + escapeHtmlNl(eIt.getLabel()) + "</td>\r\n" +
												"          <td class=\"doc-elemItem-body\"><textarea id=\"" + itemId +
												"\"  placeholder=\"" + escapeHtmlNl(eIt.getPlaceHolder()) + "\" data-name=\"" + itemName + "\"></textarea></td>\r\n" +
												"          <td class=\"doc-elemItem-comment\">" + escapeHtmlNl(eIt.getDescr()) + "</td>\r\n" +
												"        </tr>\r\n" +
												"      </tbody>\r\n" +
												"    </table>";
								else
									htm +=
										"<table class=\"doc-elemItem\" border=\"0\">\r\n" +
											"      <tbody>\r\n" +
											"        <tr>\r\n" +
											"          <td class=\"doc-elemItem-label\">" + escapeHtmlNl(eIt.getLabel()) + "</td>\r\n" +
											"          <td class=\"doc-elemItem-body\"><label id=\"" + itemId +
											"\" data-name=\"" + itemName + "\"></label></td>\r\n" +
											"          <td class=\"doc-elemItem-comment\">" + escapeHtmlNl(eIt.getDescr()) + "</td>\r\n" +
											"        </tr>\r\n" +
											"      </tbody>\r\n" +
											"    </table>";
									
								break;

							case Separator:
								htm += "<hr class=\"doc-separator\"/>";
								break;

							default:
								break;
							}
						}

						break;
					}
				}
				htm += "</div>"; // Koniec elementu

				break;

			case PageBreak:
				htm += "  </div></div>\n" +
						"  <div class=\"doc-back\">\n" +
						"     <div class=\"doc-all\">\n";

			default:
				break;
			}
		}
	}

	public void clear() {
		mainPanel.clear();
		editable = false;
	}

	/** Odczytuje dane z dokumentu HTML do listy faktów */
	public Map<String, JSONObject> getDocData() {
		GQuery allInputs = $("input, textarea, select", mainPanel);
		facts.clear();

		int lp = 1;
		for (int i = 0; i < allInputs.length(); i++) {
			Element e = allInputs.get(i);
			String nm = e.getAttribute("data-name");
			if (!nm.isEmpty()) {
				String pth = getPathFromDom(e); // id.substring(0, id.indexOf('|'));
				JSONObject o = facts.get(pth);
				if (o == null) {
					o = JSONParser.parseStrict("{}").isObject();
					o.put("__lp", new JSONNumber(lp++));
					facts.put(pth, o);
				}

				if (e instanceof SelectElement && e.getTagName().equals("SELECT")) {
					if (((SelectElement) e).isMultiple()) {
						NodeList<OptionElement> opl = ((SelectElement) e).getOptions();
						JSONArray ar = JSONParser.parseStrict("[]").isArray();
						int ct = 0;
						for (int x = 0; x < opl.getLength(); x++) {
							if (opl.getItem(x).isSelected())
								ar.set(ct++, new JSONString(opl.getItem(x).getValue()));
						}

						o.put(nm, ar);
					} else {
						String s = ((SelectElement) e).getValue();
						o.put(nm, new JSONString(s));
					}
				} else if (e instanceof InputElement) {
					InputElement ib = (InputElement) e;
					String s = ib.getValue();
					String tp = ib.getType();
					if (tp.equals("radio")) {
						if (ib.isChecked())
							o.put(nm, new JSONString(s));
					} else if (tp.equals("checkbox")) {
						o.put(nm, new JSONString((ib.isChecked()) ? "T" : "N"));
					} else {
						switch (ib.getClassName()) {
						case "datepicker" :
							if (!Utils.isDate(s)) {
								Utils.showMessage("Nieprawidłowa data: " + s + ".\nWartości nie zapisano");
								continue;
							}
							break;
						case "datetimepicker" :
							if (!Utils.isDateTime(s)) {
								Utils.showMessage("Nieprawidłowa data i godzina: " + s + ".\nWartości nie zapisano");
								continue;
							}
							break;
						case "timepicker" :
							if (!Utils.isTime(s)) {
								Utils.showMessage("Nieprawidłowa godzina: " + s + ".\nWartości nie zapisano");
								continue;
							}
							break;
						default:
							break;
						}
						
						o.put(nm, new JSONString(s));
					}
				}
			}
		}
		// JSONObject ff = facts.get("");
		// Utils.getCF().showMessage(ff.toString());
		return facts;
	}
	
	public class DocError {
		String msg;
		boolean critical;
		
		DocError(String msg, boolean critic) {
			this.msg = msg;
			critical = critic;
		}

		DocError(String msg) {
			this(msg, false);
		}
	}
	
	/** Waliduje poprawność wypełnienia formularza i w razie błedów generuje mapę Element/Treść błędu*/
	public Map<Element, DocError> validateDoc() {
		Map<Element, DocError> res = new HashMap<Element, DocError>();
		GQuery allInputs = $("input, textarea, select", mainPanel);
		ArrayList<String> radiosDone = new ArrayList<String>();
	
		for (int i = 0; i < allInputs.length(); i++) {
			Element e = allInputs.get(i);
			String nm = e.getAttribute("data-name");
			if (!nm.isEmpty()) {	
				
				// Radios o podanej nazwie przeywarzam raz, bo wartośc zwracana z wszystkich
				if (e.getTagName().equals("INPUT") && InputElement.as(e).getType().equals("radio")) {
					String n = e.getAttribute("name"); 
					if (radiosDone.contains(n))
						continue;
					else
						radiosDone.add(n);
				}
				
				String v = coalesce(getElemValue(e));
				
				// Szukam definicji elementu
				ElemItem it = Elem.getElemItem(elems, nm);
				if (it != null) {
					
					// Zasadnicza walidacja z uwzględnieniem kryteriów dotyczacych elementu
					
					if (v.isEmpty()) {
						if (it.getRequired()) {
							res.put(e, new DocError("Pole musi być wypełnione"));
							continue;
						}
					
						// Czy nie jest wymagana z racji bycia kluczem głownym
						Element par = getParentHeader(e);
						if (par.getClassName().equals("doc-header") && nm.equals(par.getAttribute("data-pk"))) {
							res.put(e, new DocError("Pole musi być wypełnione przed zapisem, gdyż identyfikuje dane z panelu",  true));
							continue;
						}
					}
					
					
					if (!v.isEmpty()) {
						if (it.getMaxLen()>0 && it.getMaxLen() < v.length()) {
							res.put(e, new DocError("Wartość nie może być dłuższa, niż " + ((Integer)it.getMaxLen()).toString() + " znaków"));
							continue;
						}
						
						if (it.getDataType() == RegElemDataType.NUMBER) {
							if (!Utils.isNumeric(v)) {
								res.put(e, new DocError("Wartość musi zawierać liczbę"));
								continue;
							}
							double d = Double.parseDouble(v); 
							if (!it.getMax().isEmpty() && Double.parseDouble(it.getMax()) < d) {
								res.put(e, new DocError("Wartość musi być mniejsza, lub równa: " + it.getMax()));
								continue;
							}
							if (!it.getMin().isEmpty() && Double.parseDouble(it.getMin()) > d) {
								res.put(e, new DocError("Wartość musi być większa, lub równa: " + it.getMin()));
								continue;
							}
						}
	
						if (it.getDataType() == RegElemDataType.DATE) {
							if (!Utils.isDate(v)) {						
								res.put(e, new DocError("Wartość musi zawierać datę"));
							continue;
							}
						}
						
						if (it.getDataType() == RegElemDataType.DATETIME && !Utils.isDateTime(v)) {
							res.put(e, new DocError("Wartość musi zawierać datę i czas"));
							continue;
						}
	
						if (it.getDataType() == RegElemDataType.TIME && !Utils.isTime(v)) {
							res.put(e, new DocError("Wartość musi zawierać czas"));
							continue;
						}
						
						if (it.getLimitToList() && !it.listContainsKey(v)) {
							res.put(e, new DocError("Wartość musi znajdować sie na liście dostępnych wartości"));
							continue;
						}
						
						if (!it.getRegExpr().isEmpty() && !v.matches(it.getRegExpr()) ) {
							res.put(e, new DocError("Format wprowadzonej wartości nie jest prawidłowy"));
							continue;
						}
					}
				}
			}
		}
		return res;
	}
	
	
	private String getElemValue(Element e) {
		if (e instanceof SelectElement && e.getTagName().equals("SELECT")) {
			if (((SelectElement) e).isMultiple()) {
				NodeList<OptionElement> opl = ((SelectElement) e).getOptions();
				String res = "";
				for (int x = 0; x < opl.getLength(); x++) {
					if (opl.getItem(x).isSelected())
						res += (res.isEmpty() ? "" : ",") + opl.getItem(x).getValue();
				}
				return res;
			} else {
				return ((SelectElement) e).getValue();
			}
		} else if (e instanceof InputElement) {
			InputElement ib = (InputElement) e;
			String s = ib.getValue();
			String tp = ib.getType();
			
			// Jeśli radio, to szukam wartośc wynikająca z wszystkich radios o tej nazwie - value z zaznaczonego
			if (tp.equals("radio")) {
				if (ib.isChecked())
					return s;
				else {
					GQuery ls = $("input[name=\"" + e.getAttribute("name") + "\"]", e.getParentElement().getParentElement());
					for(int i=0; i<ls.length(); i++) {
						InputElement ird = InputElement.as(ls.get(i));
						if (ird.isChecked())
							return ird.getValue();
					} 
					return "";
				}
			} else if (tp.equals("checkbox")) {
				return (ib.isChecked() ? "T" : "N");
			} else
				return s;
		}
		return "";
	}
	
	/** Odczytuje dane z pola w danym nodzie */
	private String getDocData(Element parent, String name) {
		GQuery allInputs = $("input, textarea, select", parent);

		for (int i = 0; i < allInputs.length(); i++) {
			Element e = allInputs.get(i);
			if  (e.getAttribute("data-name").equals(name) && getParentHeader(e) == parent) {
				
				if (e.getTagName().equals("INPUT") && InputElement.as(e).getType().equals("radio")) {
					if (InputElement.as(e).isChecked())
						return getElemValue(e);
				} else
					return getElemValue(e);
			}
		}
		return "";
	}
	
	private Element getParentHeader(Element e) {
		e = e.getParentElement();
		while (e != null) {
			if (e.getClassName().equals("doc-header") || e == elemsPanel.getElement())
				return e;
			e = e.getParentElement();
		}
		return null;
	}

	private String getPathFromDom(Element e) {
		Element root = elemsPanel.getElement();
		Element p = e.getParentElement();
		String pth = "";
		while (p != null && p != root) {
			String nm = p.getAttribute("data-name");
			String pk = p.getAttribute("data-pk");
			if (!nm.isEmpty()) {
				String v = "";
				if (!pk.isEmpty()) 
					v = getDocData(p, pk).replace('/', '_').replace('(', '{').replace(')', '}').replace('|', '_');
				if (v.isEmpty())
					pth = nm +  (pth.isEmpty() ? "" : "/") + pth;
				else
					pth = nm + '(' + v + ')' + (pth.isEmpty() ? "" : "/") + pth;
			}
			p = p.getParentElement();
		}
		return pth;
	}

	public void loadDoc(Map<String, JSONObject> facts, Doc docDef, JsArray<Elem> elems, Boolean editable) {
		clear();
		this.doc = docDef;
		this.elems = elems;
		this.facts = facts;
		this.editable = editable;
		buildDoc();
	}

	private Element addDomElement(Element parentElement, String pth) {
		int p = pth.lastIndexOf('/');
		String subpth;
		if (p >= 0)
			subpth = pth.substring(0, p);
		else
			subpth = "";
		p = pth.lastIndexOf('(');
		String key = "";
		if (p>0)
			key = pth.substring(p+1, pth.lastIndexOf(')'));

		DocItem it = doc.getItemFromPath(pth);
		Element el = null;
		if (it != null) {
			@SuppressWarnings("unchecked")
			JsArray<DocItem> its = (JsArray<DocItem>) Doc.createArray();
			its.push(it);
			htm = "";
			buildDocItems(its, subpth, key, false); 
			el = DOM.createDiv();
			parentElement.appendChild(el);
			setOuterHTML(el, htm);
			addEvents(el.getParentElement());
		}
		return el;
	}

}
