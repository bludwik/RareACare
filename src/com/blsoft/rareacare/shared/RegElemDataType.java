package com.blsoft.rareacare.shared;

public enum RegElemDataType {
	STRING,
	NUMBER,
	DATETIME,
	DATE,
	TIME,
	NODATA;
	
	public String getDescr() {
		switch (this) {
		case STRING:
			return "Tekst";
		case NUMBER:
			return "Liczba";
		case DATE:
			return "Data";
		case DATETIME:
			return "Data i godzina";
		case TIME:
			return "Czas (godzina)";
		case NODATA:
			return "Brak danych";
		
		}
		return null;
	}
}
