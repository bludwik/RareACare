/**
 * 
 */
package com.blsoft.rareacare.server;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

/**
 * Subklasa RequestFactoryServlet inicjuj�ca sesj� i tranzakcj� Hibernate, w
 * ramach kt�rej jest wykonywana ca�a komnikacja z baz�. Dzi�ki temu nie trzeba
 * otwiera� i zamyka� sesji i tranzakcji oraz wszystkie polecenia s� wykonywana
 * w jednej tranzakcji.
 * 
 * Dodatkowo w doPost w przysz�o�ci trzeba b�dzie zaimplementowa� testowanie
 * uprawnie� - czyli czy zalogowany user ma prawo otworzy� odpowiedni rejestr.
 * 
 * @author bartek
 * 
 */
@SuppressWarnings("serial")
public class JpaRequestFactoryServlet extends RequestFactoryServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		EntityManager em = RequestUtil.getEntityManagerFactory()
				.createEntityManager();
		ThreadLocalData.setEm(em);
		ThreadLocalData.setRequest(req);
		em.getTransaction().begin();
		try {
			
			// RequestUtil.checkUser();  TODO - dodać inteligentne sprawdzanie zalogowania - ale nie dla eventu logowania...
			
			super.doPost(req, resp); // Zasadnicza obs�uga ka�dego wywo�ania

			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
			ThreadLocalData.setEm(null);
			ThreadLocalData.setRequest(null);
		}
	}
}
