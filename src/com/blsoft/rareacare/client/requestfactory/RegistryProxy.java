package com.blsoft.rareacare.client.requestfactory;

import java.util.List;

import com.blsoft.rareacare.client.Utils;
import com.blsoft.rareacare.model.Registry;
import com.blsoft.rareacare.server.EntityLocator;
import com.blsoft.rareacare.shared.ElemKind;
import com.blsoft.rareacare.shared.ItemKind;
import com.blsoft.rareacare.shared.RegElemDataType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = Registry.class, locator = EntityLocator.class)
public interface RegistryProxy extends BaseEntityProxy { 

	String getName();

	void setName(String param);

	UserProxy getOwner();

	void setOwner(UserProxy param);

	List<RegUserProxy> getRegUsers();

	void setRegUsers(List<RegUserProxy> param);

	public String getDef();

	public void setDef(String param);
	
	/** Opis rejestru */
	public String getDescr();

	public void setDescr(String param);
	
	public RegUserProxy getCurrRegUser();
	

	// Operacje na polu Def (JSON)

	public abstract class BaseJSO extends JavaScriptObject {
		// You can add some static fields here, like status codes, etc.
		/**
		 * Required by {@link JavaScriptObject}
		 */
		protected BaseJSO() {
		}

		/**
		 * Uses <code>eval</code> to parse a JSON response from the server
		 * 
		 * @param responseString
		 *            the raw string containing the JSON repsonse
		 * @return an JavaScriptObject, already cast to an appropriate type
		 */
		public static <T extends BaseJSO> T createFromString(String responseString) {
			return JsonUtils.safeEval(responseString);
		};
	
//		public static final native <T extends BaseJSO> T createFromString(String responseString) /*-{
//		// You should be able to use a safe parser here
//		// (like the one from json.org)
//		return JsonUtils.safeEval(responseString);
////		return eval('(' + responseString + ')');
//	}-*/;
//	
		public final native String toJson() /*-{ return JSON.stringify(this);}-*/;
		
		public final native static JsArrayString getEmptyStringArray() /*-{ return []; }-*/;		
	}

	public class Elem extends BaseJSO {
		protected Elem() {}
		public final native String getId() /*-{	return this.id;}-*/;
		public final native void setId(String s) /*-{	this.id = s;}-*/;
		public final native String getLabel() /*-{	return (this.label==null)? "" : this.label;}-*/;
		public final native void setLabel(String s) /*-{	this.label = s;}-*/;
		public final native JsArray<ElemItem> _getItems() /*-{	return this.items;}-*/;
		public final native void setItems(JsArray<ElemItem> items) /*-{ this.items = items;}-*/;

		@SuppressWarnings("unchecked")
		public final JsArray<ElemItem> getItems() {
			JsArray<ElemItem> res = _getItems();
			if (res==null) {
				res = (JsArray<ElemItem>)RareACareDef.createArray();
				setItems(res);
			}
			return res;
		}
		
		public final static ElemItem getElemItem(JsArray<Elem> elems, String elem) {
			String[] ar = elem.split("/");
			for(int i=0; i<elems.length();i++) {
				Elem el;
				if ((el = elems.get(i)).getId().equals(ar[0])) {
					String inm = "";
					if (ar.length>1)
						inm = ar[1];
					for(int j=0;j< el.getItems().length();j++) {
						ElemItem it = el.getItems().get(j);
						if (it.getId().equals(inm))
							return it;  // Prawidłoe wyjście - znalazł
					}
				}
			}
			return null;
		}
	}
	
	
	public class ValueKey extends BaseJSO {
		protected ValueKey() {}
		public final native String getValue() /*-{	return this.value;}-*/;
		public final native void setValue(String s) /*-{	this.value = s;}-*/;
		public final native String getKey() /*-{	return this.key;}-*/;   
		public final native void setKey(String s) /*-{	this.key = s;}-*/;
		public final String getAsString() {
			return getValue() + ((getKey() == null || getKey() == "") ? "" : "|" + getKey());
		}
		public final void setAsString(String value) {
			String[] ar = value.split("\\|");
			setValue((ar.length>0) ? ar[0] : "");
			setKey((ar.length>1) ? ar[1] : "");
		}
		public static final ValueKey create(String value) {
			ValueKey rt = (ValueKey) ValueKey.createObject();
			rt.setAsString(value);
			return rt;
		}
	}

	public class ElemItem extends BaseJSO {
		protected ElemItem() {}
		public final native String getId() /*-{	return this.id;}-*/;
		public final native void setId(String s) /*-{	this.id = s;}-*/;
		public final native String getLabel() /*-{	return this.label;}-*/;
		public final native void setLabel(String s) /*-{	this.label = s;}-*/;
		public final native int getLp() /*-{	return (this.lp==null)?0:this.lp;}-*/;
		public final native void setLp(int s) /*-{	this.lp = s;}-*/;
		public final native String _getKind() /*-{	return this.kind;}-*/;
		public final native void _setKind(String s) /*-{	this.kind = s;}-*/;
		public final native String getDescr() /*-{	return this.descr;}-*/;
		public final native void setDescr(String s) /*-{	this.descr = s;}-*/;
		public final native boolean getIsList() /*-{	return this.isList;}-*/;  // Dla Input, choć nie wiem, czy też na pozioimie elementu...
		public final native void setIsList(boolean s) /*-{	this.isList = s;}-*/;
		public final native String getUnits() /*-{	return this.units;}-*/;
		public final native void setUnits(String s) /*-{	this.units = s;}-*/;
		public final native String getNormMin() /*-{	return this.normMin;}-*/;
		public final native void setNormMin(String s) /*-{	this.normMin = s;}-*/;
		public final native String getNormMax() /*-{	return this.normMax;}-*/;
		public final native void setNormMax(String s) /*-{	this.normMax = s;}-*/;
		public final native String getNormDescr() /*-{	return this.normDescr;}-*/;
		public final native void setNormDescr(String s) /*-{	this.normDescr = s;}-*/;
		public final native String _getDataType() /*-{	return this.dataType;}-*/;
		public final native void _setDataType(String s) /*-{	this.dataType = s;}-*/;
	
		public final native JsArray<ValueKey> _getItems() /*-{	return this.items;}-*/;  // Lista elementó dla combo, edit, radios, checkboxes
		public final native void setItems(JsArray<ValueKey> items) /*-{ this.items = items;}-*/;
		public final native boolean getLimitToList() /*-{	return (this.limitToList==null) ? false : this.limitToList; }-*/;  
		public final native void setLimitToList(boolean s) /*-{  this.limitToList = s;}-*/;

		// Dodatkowe parametry
		public final native boolean getRequired() /*-{	return (this.required==null) ? false : this.required; }-*/;  
		public final native void setRequired(boolean s) /*-{  this.required = s;}-*/;
		public final native String getRegExpr() /*-{	return (this.regExpr==null)? "" : this.regExpr;}-*/;
		public final native void setRegExpr(String s) /*-{	this.regExpr = s;}-*/;
		public final native String getPlaceHolder() /*-{	return this.placeHolder;}-*/;
		public final native void setPlaceHolder(String s) /*-{	this.placeHolder = s;}-*/;
		public final native String getMin() /*-{	return this.min;}-*/;
		public final native void setMin(String s) /*-{	this.min = s;}-*/;
		public final native String getMax() /*-{	return this.max;}-*/;
		public final native void setMax(String s) /*-{	this.max = s;}-*/;
		public final native int getMaxLen() /*-{	return (this.maxLen==null) ? 0 : this.maxLen;}-*/;
		public final native void setMaxLen(int s) /*-{	this.maxLen = s;}-*/;

		
		@SuppressWarnings("unchecked")
		public final JsArray<ValueKey> getItems() {
			JsArray<ValueKey> res = _getItems();
			if (res==null) {
				res = (JsArray<ValueKey>)ValueKey.createArray();
				setItems(res);
			}
			return res;
		}
		
		public final String getItemsAsString() {
			JsArray<ValueKey> ls = getItems();
			String ret = "";
			for (int i=0;i<ls.length();i++) 
				ret += ls.get(i).getAsString() + "\n";
			return ret;
		}
		
		public final void setItemsAsString(String value) {
			if (value == null)
				setItems(null);
			else {
				String[] ar = value.split("\n");
				@SuppressWarnings("unchecked")
				JsArray<ValueKey> ls = (JsArray<ValueKey>) ElemItem.createArray();
				for (int i = 0; i < ar.length; i++) {
					ls.push(ValueKey.create(ar[i]));
				}
				setItems(ls);
			}
		}		
		
		public final ItemKind getKind() {
			try {
				String s = _getKind();
				return ItemKind.valueOf(s);
				
			} catch (Exception e) {
				return ItemKind.Edit;
			}
		};
		public final void setKind(ItemKind s) {
			if (s==null)
				_setKind(null);
			else
				_setKind(s.toString());
		}
		
		public final RegElemDataType getDataType() {
			try {
				return RegElemDataType.valueOf(_getDataType());
			} catch (Exception e) {
				return RegElemDataType.STRING;
			}
		}
		public final void setDataType(RegElemDataType s) {
			if (s==null)
				_setDataType(null);
			else
				_setDataType(s.toString());
		}
		
		public final String getNorm() {
			return (!Utils.isEmpty(getNormDescr()) ? getNormDescr() 
					: !Utils.isEmpty(getNormMin()) && !Utils.isEmpty(getNormMax()) ? getNormMin() + " - " + getNormMax() 
							: !Utils.isEmpty(getNormMin()) ? "> " + getNormMin() :
								!Utils.isEmpty(getNormMax()) ? "< " + getNormMax() : "");
		}
		
		public final boolean hasNorm() {
			return (getKind() == ItemKind.Edit && (!Utils.isEmpty(getNormDescr()) ||  !Utils.isEmpty(getNormMin()) || !Utils.isEmpty(getNormMax())));
		}
		public final boolean listContainsKey(String v) {
			JsArray<ValueKey> it = getItems();
			for(int i=0;i<getItems().length();i++) {
				if (it.get(i).getKey().equals(v))
					return true;
			}
			return false;
		}		
	}

	/** Pozycje w dokumencie*/
	public class DocItem extends BaseJSO {
		protected DocItem() {}
		public final native String getId() /*-{	return this.id;}-*/;
		public final native void setId(String s) /*-{	this.id = s;}-*/;
		public final native String getLabel() /*-{	return this.label;}-*/;
		public final native void setLabel(String s) /*-{	this.label = s;}-*/;
		public final native int getLp() /*-{	return this.lp;}-*/;
		public final native void setLp(int s) /*-{	this.lp = s;}-*/;
		public final native String _getKind() /*-{	return this.kind;}-*/;
		public final native void _setKind(String s) /*-{ this.kind = s;}-*/;
		public final native int getRepeatMin() /*-{	return (this.repeatMin==null)?0:this.repeatMin;}-*/;  // Dla Header
		public final native void setRepeatMin(int rep) /*-{	this.repeatMin = rep;}-*/;
		public final native int getRepeatMax() /*-{	return (this.repeatMax==null)?1:this.repeatMax;}-*/;  // Dla Header
		public final native void setRepeatMax(int rep) /*-{	this.repeatMax = rep;}-*/;
		public final native String getPrimaryKey() /*-{	return this.pk;}-*/;	  // Dla Header
		public final native void setPrimaryKey(String s) /*-{	this.pk = s;}-*/;
		public final native String getDescr() /*-{	return this.descr;}-*/;
		public final native void setDescr(String s) /*-{	this.descr = s;}-*/;
		public final native JsArray<DocItem> _getItems() /*-{	return this.items;}-*/;  // Podelementy dla grupera (header)
		public final native void setItems(JsArray<DocItem> items) /*-{ this.items = items;}-*/;

		@SuppressWarnings("unchecked")
		public final JsArray<DocItem> getItems() {
			JsArray<DocItem> res = _getItems();
			if (res==null) {
				res = (JsArray<DocItem>)RareACareDef.createArray();
				setItems(res);
			}
			return res;
		}

		
		public final ElemKind getKind() {
			try {
				return ElemKind.valueOf(_getKind());
			} catch (Exception e) {
				return ElemKind.Elem;
			}
		}
		
		public final void setKind(ElemKind k) {
			if (k==null)
				_setKind(null);
			else
				_setKind(k.toString());
		}
	}
	
	/**Definicja dokumentu*/
	public class Doc extends BaseJSO {
		protected Doc() {}
		public final native String getId() /*-{	return this.id;}-*/;
		public final native void setId(String value) /*-{	this.id = value;}-*/;
		public final native String getName() /*-{	return this.name;}-*/;
		public final native void setName(String value) /*-{	this.name = value;}-*/;
		public final native String getDescr() /*-{	return this.descr;}-*/;
		public final native void setDescr(String value) /*-{	this.descr = value;}-*/;
		public final native JsArray<DocItem> _getItems() /*-{	return this.items;}-*/;
		public final native void setItems(JsArray<DocItem> itms) /*-{	this.items = itms;}-*/;

		// Te 3 pola są też definowane na poziomie TreeItem i stamtąd maja być pobierane (nie z Doc); Jeśli tam nie ma, to automatycznie będa pobrane stąd 
		public final native JsArrayString _getInfoFields() /*-{	return this.infoFields;}-*/;
		public final native void setInfoFields(JsArrayString itms) /*-{	this.infoFields = itms;}-*/;
		public final native JsArrayString _getUniqueFields() /*-{	return this.uniqueFields;}-*/;
		public final native void setUniqueFields(JsArrayString itms) /*-{	this.uniqueFields = itms;}-*/;
		public final native void setMaxCount(int value)  /*-{	this.maxCount = value;}-*/;
		public final native int getMaxCount()  /*-{	return ( this.maxCount == null ? 1 :  this.maxCount);}-*/;
		
		
		@SuppressWarnings("unchecked")
		public final JsArray<DocItem> getItems() {
			JsArray<DocItem> res = _getItems();
			if (res==null) {
				res = (JsArray<DocItem>)RareACareDef.createArray();
				setItems(res);
			}
			return res;
		}
		public final JsArrayString getInfoFields() {
			JsArrayString res = _getInfoFields();
			if (res==null) {
				res = RareACareDef.getEmptyStringArray();
				setInfoFields(res);
			}
			return res;
		}
		public final JsArrayString getUniqueFields() {
			JsArrayString res = _getUniqueFields();
			if (res==null) {
				res = RareACareDef.getEmptyStringArray();
				setUniqueFields(res);
			}
			return res;
		}
		
		public final DocItem getItemFromPath(String pth) {
			String[] pthar = pth.split("/");
			int p = 0;
			JsArray<DocItem> its = getItems();
			while (p < its.length()) {
				int i;
				String pt = pthar[p];
				int ix = pt.indexOf('(');
				if (ix>=0)
					pt = pt.substring(0, ix);
				for (i = 0; i < its.length(); i++) {
					DocItem it = its.get(i);
					if (it.getId().equals(pt)) {
						if (p == pthar.length - 1)
							return it;   // Prawidłowe wyjście
						else {
							if (it.getKind() != ElemKind.Header)
								return null;
							its = it.getItems();
							p++;
							break;
						}
					}
				}
				if (i==its.length())
					return null;
			}
			return null;
		}
	}

	/** Hierarhia dokumentów  wramach rejestr*/
	public class DocTreeItem extends BaseJSO {
		protected DocTreeItem() {}
		public final native String getId() /*-{	return this.id;}-*/;
		public final native void setId(String value) /*-{	this.id = value;}-*/;
		public final native int getIdent() /*-{	return this.ident;}-*/;
		public final native void setIdent(int value) /*-{	this.ident = value;}-*/;
		public final native int getLp() /*-{	return this.lp;}-*/;
		public final native void setLp(int s) /*-{	this.lp = s;}-*/;
		public final native void setPath(String value)  /*-{	this.path = value;}-*/; 
		public final native String getPath() /*-{	return this.path;}-*/;
		/** Unikatowy ID dokumentu w drzewie - czyli jesli jest kilka identycznych dokumentów w drzewie, to każdy z nich ma inny UniqueID) */
		public final native void setUniqueId(String value)  /*-{	this.uniqueId = value;}-*/; 
		public final native String getUniqueId() /*-{	return this.uniqueId;}-*/;
		public final native JsArrayString _getInfoFields() /*-{	return this.infoFields;}-*/;
		public final native void setInfoFields(JsArrayString itms) /*-{	this.infoFields = itms;}-*/;
		public final native JsArrayString _getUniqueFields() /*-{	return this.uniqueFields;}-*/;
		public final native void setUniqueFields(JsArrayString itms) /*-{	this.uniqueFields = itms;}-*/;
		public final native void setMaxCount(int value)  /*-{	this.maxCount = value;}-*/;
		public final native int _getMaxCount()  /*-{ return (this.maxCount==null ? 0 : this.maxCount);}-*/;
		

		public final JsArrayString getInfoFields(Doc d) {
			JsArrayString res = _getInfoFields();
			if (res==null) 
				return d.getInfoFields();
			else
				return res;
		}
		
		public final JsArrayString getUniqueFields(Doc d) {
			JsArrayString res = _getUniqueFields();
			if (res==null) 
				return d.getUniqueFields();
			else
				return res;
		}
		
		public final Integer getMaxCount(Doc d)  {
			int res = _getMaxCount();
			if (res==0)
				res = d.getMaxCount();
			return (res==0? 1 : res);
		}
		
		public final String getInfoFieldsAsString(Doc doc) {
			JsArrayString flds = getInfoFields(doc);
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<flds.length();i++) {
				if (sb.length()>0)
					sb.append('|');
				sb.append(flds.get(i));
			}
			return sb.toString();
		}
	}


	public class RareACareDef extends Doc {
		protected RareACareDef() {}
		public final native String getVer() /*-{ return this.ver;}-*/;
		public final native void setVer(String value) /*-{	this.ver = value;}-*/;
		public final native JsArray<Elem> _getElems() /*-{ return this.elems;}-*/;
		public final native void _setElems(JsArray<Elem> res) /*-{ this.elems = res;}-*/;
		public final native JsArray<Doc> _getDocs() /*-{ return this.docs;}-*/;
		public final native void _setDocs(JsArray<Doc> res) /*-{ this.docs = res;}-*/;
		public final native JsArray<DocTreeItem> _getDocTree() /*-{	return this.docTree;}-*/;
		public final native void _setDocTree(JsArray<DocTreeItem> res) /*-{ this.docTree = res;}-*/;

		@SuppressWarnings("unchecked")
		public final JsArray<Elem> getElems() {
			JsArray<Elem> res = _getElems();
			if (res==null) {
				res = (JsArray<Elem>)RareACareDef.createArray();
				_setElems(res);
			}
			return res;
		}
		
		@SuppressWarnings("unchecked")
		public final JsArray<Doc> getDocs() {
			JsArray<Doc> res = _getDocs();
			if (res==null) {
				res = (JsArray<Doc>)RareACareDef.createArray();
				_setDocs(res);
			}
			return res;
		}
		
		@SuppressWarnings("unchecked")
		public final JsArray<DocTreeItem> getDocTree() {
			JsArray<DocTreeItem> res = _getDocTree();
			if (res==null) {
				res = (JsArray<DocTreeItem>)RareACareDef.createArray();
				_setDocTree(res);
			}
			return res;
		}
		
		
		public final int getDocTreePos(String uId) {
			JsArray<DocTreeItem> dt = getDocTree();
			for(int i=0; i<dt.length();i++) {
				if (dt.get(i).getUniqueId().equals(uId))
					return i;
			}
			return -1;
		}
		
		/**
		 * poszukuje lementu o podanym ID   
		 * @param id
		 * @return Zwraca element o podanym ID, lub null, gdy nie znajdzie 
		 */
		public final Elem getElem(String id) {
			JsArray<Elem> el = getElems();
			for (int i = 0; i<el.length(); i++) {
				if (el.get(i).getId().equals(id))
					return el.get(i);
			}
			return null;
		}
		/**
		 * poszukuje dokumentu o podanym ID   
		 * @param id
		 * @return Zwraca dokument o podanym ID, lub null, gdy nie znajdzie 
		 */
		public final Doc getDoc(String id) {
			JsArray<Doc> el = getDocs();
			for (int i = 0; i<el.length(); i++) {
				if (el.get(i).getId().equals(id))
					return el.get(i);
			}
			return null;
		}
	}
}