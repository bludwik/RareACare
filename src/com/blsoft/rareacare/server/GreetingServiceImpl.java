package com.blsoft.rareacare.server;

import javax.persistence.EntityManager;

import com.blsoft.rareacare.client.GreetingService;
import com.blsoft.rareacare.model.User;
import com.blsoft.rareacare.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		EntityManager em = RequestUtil.getEntityManagerFactory().createEntityManager();
		ThreadLocalData.setEm(em);
		em.getTransaction().begin();
		User s;
		try
		{
			s = em.find(User.class,  2); 
			
			em.getTransaction().commit();
		}
		catch (Exception e)
		{
			em.getTransaction().rollback();
			throw e;		
		}
		finally
		{
			em.close();
			ThreadLocalData.setEm(null);
		}
		String ss = "";
		if (s != null)
			ss = s.getFirstName();
		return ss +  "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
