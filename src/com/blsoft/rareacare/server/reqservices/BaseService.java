package com.blsoft.rareacare.server.reqservices;

import javax.servlet.ServletException;

import com.blsoft.rareacare.model.EntityBase;
import com.blsoft.rareacare.server.RequestUtil;


/**
 * Bazowy serwis wykonujący podstawowe czynności na entities
 * @author bartek
 *
 */
public class BaseService {
	
	public void setCreatorUpdater(EntityBase be) throws ServletException {
		java.sql.Timestamp tm = new java.sql.Timestamp(new java.util.Date().getTime()); 
		if (be.getId() == null) {
			be.setCreator(RequestUtil.checkUser());
			be.setCreateTime(tm);
		}
		be.setUpdater(RequestUtil.getCurrentUser());
		be.setUpdateTime(tm);
	}
	
	public void persist(EntityBase be) throws ServletException {
		setCreatorUpdater(be);
		RequestUtil.getEM().persist(be);
		RequestUtil.getEM().flush();
	};

	public void remove(EntityBase be) {
		RequestUtil.getEM().remove(be);
		RequestUtil.getEM().flush();
	};

}
