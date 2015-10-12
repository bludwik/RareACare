/**
 * G��wna klasa aplikacji tu s� g��wne, globalne statyczne zmienne - np. hibernate
 */
package com.blsoft.rareacare.server;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.blsoft.rareacare.model.Address;
import com.blsoft.rareacare.model.Document;
import com.blsoft.rareacare.model.Institution;
import com.blsoft.rareacare.model.RegUser;
import com.blsoft.rareacare.model.Registry;
import com.blsoft.rareacare.model.User;
import com.blsoft.rareacare.shared.RegRights;

/**
 * Statyczna klasa tworz�ca EntityManager i wi���ca z aktualnym w�tkiem
 * oraz dostarczaj�ca innych informacji lokalnych dla jednego requestu - np. sam request
 * 
 * @author bartek
 * 
 */
public class RequestUtil {
	private static final EntityManagerFactory entityManagerFactory;

	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			entityManagerFactory = Persistence.createEntityManagerFactory("RareACare");
			
			initSession();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	/** Generowanie poddstawowych danych - na początku w czasie dev.
	 * 
	 */
	private static void initSession() throws Exception {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		try
		{
			
			if ((Long)em.createQuery("SELECT count(p) FROM User p ").getSingleResult() >0) {
				return;
			}
			
			Institution ins = new Institution();
			ins.setName("Jakaś nazwa");
			ins.setAddress(new Address());
			ins.getAddress().setCity("Siedlec");
			
			
			User us = new User();
			us.setName("Admin");
			us.setFirstName("Bolek");
			us.setLogin("a");
			us.setPassword("a");
			us.setInstitution(ins);
			em.persist(us);

			us = new User();
			us.setName("Test5");
			us.setFirstName("Janek");
			us.setLogin("b");
			us.setPassword("b"); 
			us.setInstitution(ins);
			em.persist(us);
			
			us = new User();
			us.setName("Kowalski");
			us.setFirstName("Tadeusz");
			us.setLogin("c");
			us.setPassword("c"); // TODO - doda� kodowanie hasa itp.
			em.persist(us);
			
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
	}

	/**
	 * Funkcja zwraca sesja JPA powiązana z danym wątkiem; Sesja ta ma juz
	 * otwartą transakcję
	 * 
	 * @return
	 */
	public static EntityManager getEM() {
		return ThreadLocalData.getEm();
	}
	public static HttpServletRequest getRequest() {
		return ThreadLocalData.getRequest();
	}
	
	public static User getCurrentUser() {
		return getRequest().getSession() != null ?  (User) getRequest().getSession().getAttribute("User"): null;				
	}
	
	public static void showError(String msg) {
		throw new RuntimeException(msg);
	}
	
	public static User checkUser() throws ServletException {
		HttpServletRequest req = getRequest();
		if (req.getSession() == null)
			showError("Nie odnaleziono sesji");
		User us = (User) req.getSession().getAttribute("User");
		if (us == null)
			showError("Nie zalogowano użytkownika");
		return us;
	}
	
	public static void checkCanRead(Document doc) throws ServletException {
		boolean bOk = false;
		User us = checkUser();
		Registry reg = doc.getRegistry();
		Collection<RegUser> ruc = us.getRegUser();
		for(RegUser ru : ruc) {
			if (ru.getRegistry().getId().equals(reg.getId())) {
				bOk = ru.getCanRead() == RegRights.ALL 
										|| ru.getCanRead() == RegRights.OWN && doc.getCreator().getId().equals(us.getId());
				break;
			}
		}
		if (!bOk)
			throw new ServletException("Nie posiadasz uprawnień do odczytu dokumentu/załączników");
	}

	public static void checkCanEdit(Document doc) throws ServletException {
		boolean bOk = false;
		User us = checkUser();
		Registry reg = doc.getRegistry();
		Collection<RegUser> ruc = us.getRegUser();
		for(RegUser ru : ruc) {
			if (ru.getRegistry().getId().equals(reg.getId())) {
				bOk = ru.getCanEdit() == RegRights.ALL 
										|| ru.getCanEdit() == RegRights.OWN && doc.getCreator().getId().equals(us.getId());
				break;
			}
		}
		if (!bOk)
			throw new ServletException("Nie posiadasz uprawnień do edycji dokumentu");
	}

}
