package com.blsoft.rareacare.server.reqservices;

import java.util.List;

import javax.persistence.Query;
import javax.servlet.ServletException;

import com.blsoft.rareacare.model.User;
import com.blsoft.rareacare.model.UserLoginLog;
import com.blsoft.rareacare.server.RequestUtil;
import com.blsoft.rareacare.shared.LogOperation;

/**
 * Serwis wykonujący czynności na potrzeby obsugi pacjenta; Każda klasa musi to
 * mieć oddzielnie utworzone
 * 
 * @author bartek
 * 
 */

public class UserService extends BaseService {

	static public void logUserEvent(User user, LogOperation lo, String comment) {
		if (user != null) {
			UserLoginLog log = new UserLoginLog();
			log.setUser(user);
			log.setOpertion(LogOperation.LOGIN);
			log.setComment(comment);
			try {
				RequestUtil.getEM().persist(log);
			} catch (Exception e) {
			}
		}
	}

	public Integer getCount() {
		Integer ct = ((Long) RequestUtil.getEM().createQuery("select count(p) from User p").getSingleResult()).intValue();
		return ct;
	};

	public User findUser(Integer id) {
		User pt = RequestUtil.getEM().find(User.class, id);
		return pt;
	};

	public User getLoggedUser() throws ServletException {

		return RequestUtil.checkUser();
		// return null;
	};

	public void logOut() {
		User us = (User) RequestUtil.getRequest().getSession().getAttribute("User");
		logUserEvent(us, LogOperation.LOGOUT, null);
		RequestUtil.getRequest().getSession().removeAttribute("User");
	}

	public User login(String loginTxt, String passTxt) {
		logOut();
		Query q = RequestUtil.getEM().createQuery("select u FROM User u WHERE u.login = ?1 AND u.password = ?2");
		q.setParameter(1, loginTxt).setParameter(2, passTxt);
		@SuppressWarnings("unchecked")
		List<User> usrs = q.getResultList();
		if (usrs.size() == 1) {
			// Logowanie do sesji
			RequestUtil.getRequest().getSession().setAttribute("User", usrs.get(0));
			logUserEvent(usrs.get(0), LogOperation.LOGIN, null);
			return usrs.get(0);
		} else
			throw new RuntimeException("Nieznany użytkownik, lub nieprawidłowe hasło");

	};

	@SuppressWarnings("unchecked")
	public List<User> getUsers(String patern, Integer from, Integer count) {
		List<User> ls;
		if (patern.isEmpty()) {
			ls = RequestUtil.getEM().createQuery("select u from User u left join fetch u.institution i  order by u.name, u.firstName")
					.setFirstResult(from).setMaxResults(count).getResultList();
		} else {
			ls = RequestUtil.getEM().createQuery("select u from User u left join fetch u.institution i WHERE UPPER(concat(u.name, ' ',u.firstName, '(', u.login, ')')) like ?1 order by u.name, u.firstName")
					.setParameter(1, "%" + patern.toUpperCase() + "%").setFirstResult(from).setMaxResults(count).getResultList();
		}
		return ls;
	};
	
	public String changePass(Integer userId, String oldPass, String newPass) throws ServletException {
		User us = RequestUtil.getEM().find(User.class, userId);
		if (us==null)
			return "Nie odnaleziono użytkownika o podanym Id";
		if (us.getPassword().equals(oldPass)) {
			us.setPassword(newPass);
			setCreatorUpdater(us);
			RequestUtil.getEM().flush();
			return "";
		}
		else
			return "Nieprawidłowe hasło";
	}
	
	public String setNewPass(int userId, String newPass) throws ServletException {
		User us = RequestUtil.getEM().find(User.class, userId);
		if (us==null)
			return "Nie odnaleziono użytkownika o podanym Id";

		us.setPassword(newPass);
		setCreatorUpdater(us);
		RequestUtil.getEM().flush();
		return "";
	}
}
