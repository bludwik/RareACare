package com.blsoft.rareacare.shared;

import java.util.ArrayList;
import java.util.List;


/**
 * Klasa parsująca i deparsująca XML na objekty
 * @author bartek
 *
 */
public class DRegParser {

	public class CustElem {}
	public class CustSubElem extends CustElem {}
	
	public class Elem {
		public String id;
		public String label;
		public List<CustSubElem> items = new ArrayList<CustSubElem>();
	}
	
	public class Doc {
		public String id;
		public String name;
		public List<CustElem> elems = new ArrayList<CustElem>();
		public List<Doc> subdocs = new ArrayList<Doc>();
	}
	
	public class Header extends CustElem{
		public String text; 
	}
	
	public class Comment extends CustSubElem{
		public String text; 
	}	
	
	public class Separator extends CustSubElem{
	}	
	
	public class PageBreak extends CustElem{
	}	
	
	public class Input extends CustSubElem{
		String label;
		String descr;
		String subId;
		String units;
		Float normMin;
		Float normMax;
		String normDescr;
		RegElemDataType DataType;
	}
	
	
	public class RareACareDocDef {
		public Integer id;
		public Integer ver;
		public String name;
		List<Elem> elems = new ArrayList<Elem>();
		List<Doc> docs = new ArrayList<Doc>();
	}

//	static public RareACareDocDef parse(String xml) {} 
}
