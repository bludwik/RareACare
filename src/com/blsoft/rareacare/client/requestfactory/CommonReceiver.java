/**
 * 
 */
package com.blsoft.rareacare.client.requestfactory;

import java.util.Set;

import javax.validation.ConstraintViolation;

import com.blsoft.rareacare.client.Utils;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * Klasa upraszczająca obsługę wywołania - obsługuje OnFailure
 * 
 * @author bartek
 * 
 */
public abstract class CommonReceiver<V> extends Receiver<V> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.web.bindery.requestfactory.shared.Receiver#onSuccess(java.
	 * lang.Object)
	 */
	@Override
	public void onFailure(ServerFailure error) {
		String msg = error.getMessage();
		if (msg.contains("Nie zalogowano użytkownika")) {
			Utils.showMessage("Nie zalogowano użytkownika, lub Twoja sesja wygasła. Musisz się ponownie zalogować do systemu");
			Utils.setUser(null);
			Utils.CheckLogInForced();
		}
		else		
			Utils.showMessage("Błąd podczas komunikacji z serwerem:\n" + msg);		
	}
	
	@Override
	  public void onConstraintViolation(Set<ConstraintViolation<?>> violations) {			
		    if (!violations.isEmpty()) {
		    	String msg = "Wystąpiły następujące bedy podczas zapisu na serwerze:\n";
		    	for (ConstraintViolation<?> v : violations) {
		    		msg += "Pole: " + v.getPropertyPath().toString() + "; BŁĄD: " + v.getMessage() + "\n";
		    	}
		    	Utils.getCF().showMessage(msg);		      
		    }
	 }
}
