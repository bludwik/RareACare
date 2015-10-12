/**
 * 
 */
package com.blsoft.rareacare.server;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

/**
 * Klasa przechowująca sesję DB w czasie aktualnego wywoania
 * 
 * @author bartek
 * 
 */
public class ThreadLocalData {
	private static ThreadLocal<EntityManager> emHolder = new ThreadLocal<EntityManager>();
	private static ThreadLocal<HttpServletRequest> reqHolder = new ThreadLocal<HttpServletRequest>();

	private ThreadLocalData() {
	}

	public static EntityManager getEm() {
		return emHolder.get();
	}

	public static void setEm(EntityManager em) {
		emHolder.set(em);
	}

	public static HttpServletRequest getRequest() {
		return reqHolder.get();
	}

	public static void setRequest(HttpServletRequest req) {
		reqHolder.set(req);
	}

}
