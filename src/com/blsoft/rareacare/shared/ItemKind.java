package com.blsoft.rareacare.shared;

public enum ItemKind {
	/** Pole edycyjne; WYmagają ID i label */
	Edit, Memo, Radios, Combo, List, CheckBox,

	/** Komentarz; Dopuszcza Label i Descr */
	Comment,

	/** Kreska rozdzielająca */
	Separator;

//	/** Grupuje elementy z możliwością tworzenia tablicy (listy); Wymaga ID */
//	Header;

	public String getDescr() {
		switch (this) {
		case Edit:
			return "Pole edycyjne";
		case Memo:
			return "Pole edycyje duże";
		case Radios:
			return "Pole wyboru typu zaznaczane";
		case Combo:
			return "Pole wyboru Combo";
		case List:
			return "Pole wyboru - lista";
		case CheckBox:
			return "Pole zaznaczane (Tak/Nie)";
		case Comment:
			return "Komenarz";
		case Separator:
			return "Separator";
//		case Header:
//			return "Nagówek (Grupa)";
		}
		return null;
	}

}
