package com.blsoft.rareacare.server.reqservices;

import java.io.File;
import java.util.List;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;

import com.blsoft.rareacare.model.DocAttach;
import com.blsoft.rareacare.model.Document;
import com.blsoft.rareacare.model.Fact;
import com.blsoft.rareacare.server.RequestUtil;

public class DocumentService extends BaseService {

	public Document findDocument(Integer id) {
		// throw new RuntimeException("11 No i co: " + id.toString());
		Document doc = RequestUtil.getEM().find(Document.class, id);
		return doc;
	}

	public Document getDocWithFacts(Integer id) {
		// throw new RuntimeException("22 No i co: " + id.toString());
		Document doc = (Document) RequestUtil
				.getEM()
				.createQuery("SELECT DISTINCT p\n" +
						"FROM Document p\n" +
						"LEFT JOIN FETCH p.facts \n" +
						"LEFT JOIN FETCH p.attachments \n" +
						"LEFT JOIN FETCH p.creator \n" +
						"WHERE p.id = :id")
				.setParameter("id", id)
				.setHint("javax.persistence.cache.retrieveMode",
						CacheRetrieveMode.BYPASS) // Wyłaczam cache, bo nie
													// wykonywał SQLa
				.getSingleResult();

		// .createNativeQuery("SELECT  *\n" +
		// "FROM document p\n" +
		// "LEFT JOIN  fact f on p.id = f.document_id\n" +
		// "WHERE p.id = ?", Document.class)
		// .setParameter(1, id)
		// .setHint("eclipselink.join-fetch", "p.facts")
		// .setHint("javax.persistence.cache.retrieveMode",
		// CacheRetrieveMode.BYPASS) // Wyłaczam
		// cache,
		// bo
		// nie
		// wykonywał
		// SQLa
		// .getSingleResult();

		return doc;
	}

	public void removeFactsFromDoc(Integer docId) {
		RequestUtil
				.getEM()
				.createNativeQuery("delete from fact where document_id = ?1")
				.setParameter(1, docId)
				.executeUpdate();
		return;
	}

	/** Zwraca listę dokumentów ROOTów w podanym rejestrez. Dokumenty te w
	 * istocie reprezentują zwykle pacjentów 
	 * @throws ServletException */
	public List<Document> getRootDocs(Integer regId, Integer from, Integer count, String patern, String orderby, Boolean sortAscending, String fields) throws ServletException {

		StringBuilder role = createRole(patern, fields);

		String sql = "select p.*, f.*\n" +
				"from document p\n" +
				"inner join  fact f on p.id = f.document_id\n" +
				"inner join reguser u on u.registry_id = p.registry_id and u.user_id = ?1 \n" +
				"    and ((u.canedit = 2 or u.canedit = 2 and p.creator_id = ?1)\n" +
				"             or (u.canread = 2 or u.canread = 1 and p.creator_id = ?1))\n" +
				"where p.parent_id is null \n" +
				role.toString() +
				"and p.registry_id = ?2\n" +
				(orderby.isEmpty() ? "" : "order by f.content::json->>'" + orderby + "'" + (sortAscending ? " asc\n" : " desc\n"));

		@SuppressWarnings("unchecked")
		List<Document> ls = RequestUtil
				.getEM()
				.createNativeQuery(sql, Document.class)
				.setParameter(1, RequestUtil.checkUser().getId())
				.setParameter(2, regId)
				.setHint("eclipselink.join-fetch", "p.facts")
				.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS) // Wyłaczam
				.setFirstResult(from)
				.setMaxResults(count)
				.getResultList();
		return ls;
	}

	private StringBuilder createRole(String patern, String fields) {
		StringBuilder role = new StringBuilder();
		String[] flds = fields.split("\\|");
		if (!patern.isEmpty()) {
			for (String f : flds) {
				if (role.length() != 0)
					role.append(" or ");
				role.append(" ff.content::json->>'").append(f).append("' ilike '").append(patern).append("%'\n");
			}
			if (role.length() > 0)
				role.insert(0, "    and exists (select 1 from fact ff \n where ff.document_id = p.id \n 	 and (").append("))\n");
		}
		return role;
	}

	public Integer getRootDocCount(Integer regId, String patern, String fields) throws ServletException {

		StringBuilder role = createRole(patern, fields);

		String sql = "select count(*)\n" +
				"from document p\n" +
				"inner join reguser u on u.registry_id = p.registry_id and u.user_id = ?1 \n" +
				"    and ((u.canedit = 2 or u.canedit = 2 and p.creator_id = ?1)\n" +
				"             or (u.canread = 2 or u.canread = 1 and p.creator_id = ?1))\n" +
				"where p.parent_id is null " +
				role.toString() +
				"and p.registry_id = ?2\n";

		Integer ct = ((Long) RequestUtil.getEM()
				.createNativeQuery(sql)
				.setParameter(1, RequestUtil.checkUser().getId())
				.setParameter(2, regId)
				.getSingleResult()).intValue();
		return ct;
	}

	/** Zwraca listę wszystkich dokumentów podpiętych do wskazanego roota
	 * (również pośrednio) */
	public List<Document> getDocs(Integer rootDocId) {
		@SuppressWarnings("unchecked")
		List<Document> ls = RequestUtil
				.getEM()
				.createQuery("SELECT DISTINCT p\n" +
						"FROM Document p\n" +
						"LEFT JOIN FETCH p.facts\n" +
						"LEFT JOIN FETCH p.attachments \n" +
						"LEFT JOIN FETCH p.creator \n" +
						"WHERE (p.rootId = :id or p.id = :id)  \n" +
						"ORDER BY p.createTime ")
				.setParameter("id", rootDocId)
				.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS) // Wyłaczam
																							// cache,
																							// bo
																							// nie
																							// wykonywał
																							// SQLa
				.getResultList();
		return ls;
	}

	public Document persistAndLoad(Document e, Boolean requery) throws ServletException {
		setCreatorUpdater(e);
		//

		RequestUtil.getEM().persist(e);
		RequestUtil.getEM().flush();
		if (requery)
			return findDocument(e.getId());
		else
			return e;
	}

	public void persistFact(Fact e) throws ServletException {
		setCreatorUpdater(e);
		RequestUtil.getEM().persist(e);
		RequestUtil.getEM().flush();
		return;
	}

	public void removeFact(Fact e) {
		RequestUtil.getEM().remove(e);
		RequestUtil.getEM().flush();
		return;
	}

	public void removeDoc(Integer docId) {
		EntityManager em = RequestUtil.getEM();
		Document e = em.find(Document.class, docId);
		if (e != null) {
			for (Fact f : e.getFacts())
				em.remove(f);
			em.remove(e);
			em.flush();
		}
		return;
	}

	public String removeAttachment(Integer attId) {
		EntityManager em = RequestUtil.getEM();
		DocAttach e = em.find(DocAttach.class, attId);
		if (e != null) {
			String pth = RequestUtil.getRequest().getSession().getServletContext().getInitParameter("attachmentsPath");
			File file = new File(pth + e.getPath());
			file.delete();
			String nm = e.getName();
			em.remove(e);
			em.flush();
			return nm;
		}
		return "";
	}

}
