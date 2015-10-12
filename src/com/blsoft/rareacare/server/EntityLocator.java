package com.blsoft.rareacare.server;

import com.blsoft.rareacare.model.EntityBase;
import com.google.web.bindery.requestfactory.shared.Locator;

/**
 * Generyczny lokator obsugujący EntityBase, a więc wszystkie entities w ramach funkcji koniecznie wymaganych przez RequestFactory;
 * Pozostae metody trzeba implementować za pośrednictwem reqservices indywidualnie dla poszczególnych klas
 * @author bartek
 *
 */

public class EntityLocator extends Locator<EntityBase, Integer> {
	@Override  
	  public EntityBase create(Class<? extends EntityBase> clazz) {  
	    try {  
	      return clazz.newInstance();  
	    } catch (InstantiationException e) {  
	      throw new RuntimeException(e);  
	    } catch (IllegalAccessException e) {  
	      throw new RuntimeException(e);  
	    }  
	  }  
	  
	  @Override  
	  public EntityBase find(Class<? extends EntityBase> clazz, Integer id) {  
	    return RequestUtil.getEM().find(clazz, id);  
	  }  
	  
	  /** 
	   * it's never called 
	   */  
	  @Override  
	  public Class<EntityBase> getDomainType() {  
	    throw new UnsupportedOperationException();  
	    // or return null;  
	  }  
	  
	  @Override  
	  public Integer getId(EntityBase domainObject) {  
	    return domainObject.getId();  
	  }  
	  
	  @Override  
	  public Class<Integer> getIdType() {  
	    return Integer.class;  
	  }  
	  
	  @Override  
	  public Object getVersion(EntityBase domainObject) {  
	    return domainObject.getVersion();  
	  }  
}
