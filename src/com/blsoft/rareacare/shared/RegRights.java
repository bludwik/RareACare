package com.blsoft.rareacare.shared;

public enum RegRights {
	NONE,
	OWN,
	ALL;	
	
    public String toString() {
    	switch (this) {
		case NONE: return "-";
		case OWN: return "Własne";
		case ALL:	return "Wszystkie";
		default: return "?";			
		}
    }
    
    public static int getIdx(RegRights r) {
    	if (r==null)
    		return 0;
    	switch (r) {
		case NONE: return 0;
		case OWN : return 1;
		case ALL : return 2;
		}
		return 0;
    }
    public static RegRights getRightFromIndex(int i) {
    	switch (i) {
		case 0 : return NONE;
		case 1 : return OWN;
		case 2 : return ALL;
		}
		return NONE;
    }
}
