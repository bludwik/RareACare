package com.blsoft.rareacare.server;

import com.blsoft.rareacare.model.DDocElem;
import com.google.web.bindery.requestfactory.shared.Locator;

public class DDocElemLocator  extends Locator<DDocElem, String> {
	@Override  
	  public DDocElem create(Class<? extends DDocElem> clazz) {  
	    try {  
	      return clazz.newInstance();  
	    } catch (InstantiationException e) {  
	      throw new RuntimeException(e);  
	    } catch (IllegalAccessException e) {  
	      throw new RuntimeException(e);  
	    }  
	  }  
	  
	  @Override  
	  public DDocElem find(Class<? extends DDocElem> clazz, String id) {  
	    return RequestUtil.getEM().find(clazz, id);  
	  }  
	  
	  /** 
	   * it's never called 
	   */  
	  @Override  
	  public Class<DDocElem> getDomainType() {  
	    throw new UnsupportedOperationException();  
	    // or return null;  
	  }  
	  
	  @Override  
	  public String getId(DDocElem domainObject) {  
	    return domainObject.getId();  
	  }  
	  
	  @Override  
	  public Class<String> getIdType() {  
	    return String.class;  
	  }  
	  
	  @Override  
	  public Object getVersion(DDocElem domainObject) {  
	    return domainObject.getVersion();  
	  }  
}
