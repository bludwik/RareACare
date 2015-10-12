package com.blsoft.rareacare.shared;

public enum ElemKind {
	/** Predefiniowany element */
	Elem,

	/** Komentarz; Dopuszcza Label i Descr */
	Comment,

	/** Kreska rozdzielająca */
	Separator,

	/** Grupuje elementy z możliwością tworzenia tablicy (listy); Wymaga ID */
	Header,
	
	PageBreak;

	public String getDescr() {
		switch (this) {
		case Elem:
			return "Element";
		case Comment:
			return "Komenarz";
		case Separator:
			return "Separator";
		case Header:
			return "Grupa";
		case PageBreak:
			return "Podział strony";
		}
		return null;
	}


}
