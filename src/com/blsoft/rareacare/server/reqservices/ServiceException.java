package com.blsoft.rareacare.server.reqservices;

import java.io.Serializable;

public class ServiceException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String msg;

	  public ServiceException() {
	  }

	  public ServiceException(String msg) {
	    this.msg = msg;
	  }

	  public String getMessage() {
	    return this.msg;
	  }}
