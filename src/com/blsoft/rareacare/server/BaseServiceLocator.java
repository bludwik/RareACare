package com.blsoft.rareacare.server;

import com.google.web.bindery.requestfactory.shared.ServiceLocator;
/**
 * Klasa wyszukująca lokator dla poszczególnych klas
 * @author bartek
 *
 */

public class BaseServiceLocator implements ServiceLocator {

	@Override
	  public Object getInstance(Class<?> clazz) {
	    try {
	      return clazz.newInstance();
	    } catch (InstantiationException e) {
	      throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	      throw new RuntimeException(e);
	    }
	  }

}
