package com.blsoft.rareacare.server.reqservices;

import java.util.List;

import javax.persistence.Query;

import com.blsoft.rareacare.model.Institution;
import com.blsoft.rareacare.server.RequestUtil;

/**
 * Serwis instytucji
 * 
 * @author bartek
 * 
 */
public class InstitutionService extends BaseService {

	public Integer getCount() {
		Integer ct = ((Long) RequestUtil.getEM().createQuery("select count(p) from Institution p").getSingleResult()).intValue();
		return ct;
	};

	public Institution findInstitution(Integer id) {
		Institution pt = RequestUtil.getEM().find(Institution.class, id);
		return pt;
	};

	@SuppressWarnings("unchecked")
	public List<Institution> getInstitutions(String patern, Integer start, Integer count) {
		Query qr = RequestUtil.getEM().createQuery("from Institution u where UPPER(u.name) like ?1 order by u.name")
				.setParameter(1, '%' + patern.toUpperCase() + '%').setFirstResult(start);
		if (count>=0)
			qr.setMaxResults(count);
		return qr.getResultList();
	};

	public Institution persistAndLoad(Institution e, Boolean requery) {
		RequestUtil.getEM().persist(e);
		if (requery)
			return findInstitution(e.getId());
		else
			return e;
	}
}
