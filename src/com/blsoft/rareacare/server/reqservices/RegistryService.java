package com.blsoft.rareacare.server.reqservices;

import java.util.List;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.blsoft.rareacare.model.DDocElem;
import com.blsoft.rareacare.model.RegUser;
import com.blsoft.rareacare.model.Registry;
import com.blsoft.rareacare.server.RequestUtil;

/** Serwis odposiwadający na requesty dotyczące rejestrów
 * 
 * @author bartek */

public class RegistryService extends BaseService {

	static JSONParser jParser = new JSONParser();

	public Registry findRegistry(Integer id) throws ServletException {
		Registry pt = RequestUtil.getEM().find(Registry.class, id);
		RegUser ct = pt.getCurrRegUser();
		if (ct == null || !ct.getUser().equals(RequestUtil.checkUser())) {

			if (RequestUtil.getCurrentUser().getIsSuperuser()) {
				// Superuser może wszystko ze wszystkim
				pt.setCurrRegUser(RegUser.NewSuperUser(pt, RequestUtil.getCurrentUser()));
			}
			else 
			{
				List<RegUser> usrs = RequestUtil
						.getEM()
						.createQuery(
								"SELECT r FROM RegUser r WHERE r.user = ?1 and r.registry.id = ?2",
								RegUser.class)
						.setParameter(1, RequestUtil.getCurrentUser())
						.setParameter(2, id)
						.getResultList();
				if (usrs.size() > 0)
					pt.setCurrRegUser(usrs.get(0));
			}
		}
		return pt;
	}

	public Registry findRegistryWithUsers(Integer id) {
		Registry rg = null;// RequestUtil;
		// .getEM()
		// .createQuery("SELECT r FROM Registry r INNER JOIN FETCH r.regCurrentUser u ON u.user = ?1",
		// Registry.class)
		// .setParameter(1, RequestUtil.getCurrentUser())
		// .getSingleResult();
		// rg = findRe
		return rg;
	}

	public List<Registry> getRegs(Integer from, Integer count, String patern) throws ServletException {
//		Integer i = RequestUtil.checkUser().getId();
		@SuppressWarnings("unchecked")
		List<Registry> ls = RequestUtil
				.getEM()
				 .createQuery(
				 "SELECT r \n" +
				 	"FROM Registry r \n" +
				 	"INNER JOIN r.regUsers ru\n" +
				 	"LEFT JOIN FETCH r.owner \n" +
				 	"WHERE UPPER(r.name) like ?1 and ru.user = ?2\n" +
				 	"ORDER BY r.name ")
//				.createNativeQuery("select r.*, o.* \n" +
//						"from registry r \n" +
//						"  left join users o on o.id=r.owner_id \n" +
//						"where r.name ilike ?1 \n" +
//						"    and (r.owner_id = ?2 \n" +
//						"            OR exists(select 1 from RegUser rr where rr.registry_id = r.id and rr.user_id = ?2)) ", Registry.class)
				.setParameter(1, "%" + patern.toUpperCase() + "%")
				.setParameter(2, RequestUtil.checkUser())
//				.setHint("eclipselink.join-fetch", "r.owner")
				.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS)
				.setFirstResult(from)
				.setMaxResults(count)
				.getResultList();

		return ls;

	}

	public Integer getCount(String patern) throws ServletException {
//		Integer i = RequestUtil.checkUser().getId();
		Integer ct = ((Long) RequestUtil
				.getEM()
				 .createQuery(
				 "SELECT count(r) \n" +
				 	"FROM Registry r \n" +
				 	"INNER JOIN r.regUsers ru\n" +
				 	"WHERE UPPER(r.name) like ?1 and ru.user = ?2")
				.setParameter(1, "%" + patern.toUpperCase() + "%")
				.setParameter(2, RequestUtil.checkUser())
				.getSingleResult()).intValue();

				
				//				.createNativeQuery("select count(*) from registry r \n" +
//					"where r.name ilike ?1\n" +
//					"    and (r.owner_id = ?2 \n" +
//					"         OR exists(select 1 from RegUser rr where rr.registry_id = r.id and rr.user_id = ?2)) ")
//				.setParameter(1, "%" + patern.toUpperCase() + "%")
//				.setParameter(2, i)
//				.getSingleResult()).intValue();
		return ct;
	}

	public Registry persistAndLoad(Registry e, Boolean requery) throws ServletException {
		setCreatorUpdater(e);

		// Jeśli zaposuję bez podania właściciela, to wpisuję aktualnego usera
		if (e.getOwner() == null)
			e.setOwner(RequestUtil.checkUser());

		RequestUtil.getEM().persist(e);
		RequestUtil.getEM().flush();
		if (requery)
			return findRegistry(e.getId());
		else
			return e;
	}

	public RegUser persistAndLoad(RegUser e) throws ServiceException, ServletException {
		setCreatorUpdater(e);
		EntityManager em = RequestUtil.getEM();
		List<RegUser> olr = em.createQuery("select r from RegUser r WHERE r.registry.id = ?1 and r.user.id = ?2 and r.id <> ?3", RegUser.class)
				.setParameter(1, e.getRegistry().getId())
				.setParameter(2, e.getUser().getId())
				.setParameter(3, (e.getId() == null) ? 0 : e.getId())
				.getResultList();

		if (olr.size() > 0)
			throw new ServiceException("Istnieją już w bazie uprawnienia dla wskazanego użytkownika");

		em.persist(e);
		em.flush();
		return e;
	}

	public List<RegUser> getRegUsers(Integer regId) {
		List<RegUser> ls = RequestUtil.getEM()
				.createQuery("SELECT r FROM RegUser r WHERE r.registry.id = ?1 ORDER BY r.user.name, r.user.firstName ", RegUser.class)
				.setParameter(1, regId).getResultList();
		return ls;

	}

	public DDocElem persistAndLoad(DDocElem p) throws ServletException {
		java.sql.Timestamp tm = new java.sql.Timestamp(new java.util.Date().getTime());
		if (p.getId() == null) {
			p.setCreator(RequestUtil.checkUser());
			p.setCreateTime(tm);
		}
		p.setUpdater(RequestUtil.getCurrentUser());
		p.setUpdateTime(tm);
		RequestUtil.getEM().persist(p);
		return p;
	}

	public void remove(DDocElem p) {
		RequestUtil.getEM().remove(p);
	}

	public List<DDocElem> getDDocElems(String pattern) {
		List<DDocElem> olr = RequestUtil
				.getEM()
				.createQuery("select r from DDocElem r WHERE Upper(r.id) like ?1 or Upper(r.label) like ?1 or Upper(r.description) like ?1", DDocElem.class)
				.setParameter(1, (pattern + '%').toUpperCase())
				.getResultList();
		return olr;
	}

	public void dDocElemAdd(String elem) throws ServiceException {
		try {
			JSONObject j = (JSONObject) jParser.parse(elem);
			EntityManager em = RequestUtil.getEM();
			DDocElem o = em.find(DDocElem.class, j.get("id").toString());
			java.sql.Timestamp tm = new java.sql.Timestamp(new java.util.Date().getTime());
			if (o == null) {
				o = new DDocElem();
				o.setId(j.get("id").toString());
				o.setCreator(RequestUtil.checkUser());
				o.setCreateTime(tm);
			}
			o.setUpdater(RequestUtil.getCurrentUser());
			o.setUpdateTime(tm);
			o.setDef(elem);
			String s = j.get("label").toString();
			o.setLabel(s);
			em.persist(o);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

}
