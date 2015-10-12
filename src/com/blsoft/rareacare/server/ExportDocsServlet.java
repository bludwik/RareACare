package com.blsoft.rareacare.server;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.blsoft.rareacare.model.RegUser;
import com.blsoft.rareacare.model.Registry;
import com.blsoft.rareacare.model.User;
import com.blsoft.rareacare.shared.RegRights;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ExportDocsServlet extends HttpServlet {

	final int DOCID = 0;
	final int UNIQUEID = 2;
	final int PATH = 3;
	final int ROOT = 4;
	final int CONTENT = 6;
	final int FPATH = 7;
	final int LP = 8;

	Registry reg = null;
	JSONObject def;
	JSONArray tr;
	JSONArray elems;

	class ColHeader {
		String uniqueid;
		String path; // Ścieżka do dokumentu; Gdy null, to domyślna
		String fpath; // ścieżka do pól
		String elem; // ID elementu
		String fld; // Nazwa pola elem/nazwa_pola
		boolean use = false; // Czy używać, czy usunąć

		/** Czy rekord odpowiada dokładnie kolumnie */
		boolean same(String fld, Object[] row) {
			boolean rt = row[UNIQUEID].toString().equalsIgnoreCase(uniqueid)
					&& (row[PATH].toString().equalsIgnoreCase(path) || row[PATH].toString().indexOf('(') < 0)
					&& row[FPATH].toString().equalsIgnoreCase(fpath)
					&& fld.equalsIgnoreCase(this.fld);

			// Uzupełniam pustą ścieżkę wzorcową na podstawie danych (buy ni
			// generować)
			if (rt && path == null && row[PATH].toString().indexOf('(') < 0)
				path = row[PATH].toString();

			return rt;
		}

		/** Czy podany rekord odpowiada bazowemy elementowi (nie analizuje
		 * dokładnie pół, a jedynie ID elementu */
		boolean sameBase(String fld, Object[] row) {

			return row[UNIQUEID].toString().equalsIgnoreCase(uniqueid)
					&& extract(row[FPATH]).equalsIgnoreCase(fpath)
					&& fld.startsWith(elem);
		}

		/** Wycina nawiasy i ich zawartość ze ścieżki */
		private String extract(Object object) {
			StringBuilder sb = new StringBuilder();
			String s = object.toString();
			boolean br = false;
			for (int i = 0; i < s.length(); i++) {
				char ch = s.charAt(i);
				switch (ch) {
				case '(':
					br = true;
					break;
				case ')':
					br = false;
					break;
				default:
					if (!br)
						sb.append(ch);
				}
			}
			return sb.toString();
		}

		ColHeader(String uid, String pth, String fpth, String elm, String fld) {
			uniqueid = uid;
			fpath = fpth;
			elem = elm;
			path = pth;
			this.fld = fld;
			if (elem == null) {
				int p = fld.indexOf('/');
				if (p >= 0)
					elem = fld.substring(0, p);
				else
					elem = fld;
			}
		}

	}

	private EntityManager em;

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/vnd.ms-excel");

		em = RequestUtil.getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();

		ThreadLocalData.setEm(em);
		ThreadLocalData.setRequest(req);

		try {

			User user = RequestUtil.checkUser();

			// Generowanie danych do eksportu

			int regId = Integer.parseInt(req.getParameter("regid"));
			reg = em.find(Registry.class, regId);
			if (reg == null)
				RequestUtil.showError("Nie odnaleziono rejestru: " + regId);

			boolean bOk = false;
			Collection<RegUser> ruc = user.getRegUser();
			for (RegUser ru : ruc) {
				if (ru.getRegistry().getId().equals(reg.getId())) {
					bOk = ru.getCanRead() == RegRights.ALL;
					break;
				}
			}
			if (!bOk)
				RequestUtil.showError("Nie posiadasz uprawnień do odczytu wszystkich dokumentów z rejestru. Eksport nie może być wykonany");

			List<Object[]> ls = em.createNativeQuery(
					"select distinct\n" +
							"    d.id,\n" +
							"    d.docdefid,\n" +
							"    d.uniqueid,\n" +
							"    d.path,\n" +
							"    d.rootid,\n" +
							"    f.id as fid,\n" +
							"    f.content,\n" +
							"    f.path as fpath,\n" +
							"    f.lp\n" +
							"from \n" +
							"    document d \n" +
							"    inner join registry r on r.id = d.registry_id\n" +
							"    left join fact f on f.document_id = d.id\n" +
							"    left join reguser ru on r.id = d.registry_id and ru.user_id = ?2\n" +
							"where d.registry_id = ?1\n" +
							"    and (r.owner_id = ?2\n" +
							"            OR exists(select 1 from RegUser rr \n" +
							"               where rr.registry_id = r.id \n" +
							"                   and rr.user_id = ?2 \n" +
							"                   and (rr.canread = 2 or rr.canread = 1 and d.creator_id = ?2)))\n" +
							"order by d.rootid, d.id, f.lp")
					.setParameter(1, regId)
					.setParameter(2, user.getId())
					.getResultList();

			ArrayList<ColHeader> cols = new ArrayList<ColHeader>();

			// Sortowanie wg pacjentów pozycji w drzewie dokumentów

			JSONParser parser = new JSONParser();
			try {

				def = (JSONObject) parser.parse(reg.getDef());
				tr = (JSONArray) def.get("docTree");
				elems = (JSONArray) def.get("elems");
				final Map<String, Long> arUid = new HashMap<String, Long>();
				for (int i = 0; i < tr.size(); i++) {
					JSONObject n = ((JSONObject) tr.get(i));
					arUid.put((String) n.get("uniqueId"), (Long) n.get("lp"));
				}

				Collections.sort(ls, new Comparator<Object[]>() {

					@Override
					public int compare(Object[] arg0, Object[] arg1) {
						if (arg0[ROOT].equals(arg1[ROOT])) {
							Long v0 = arUid.get(arg0[UNIQUEID]);
							Long v1 = arUid.get(arg1[UNIQUEID]);
							if (v0 != null && v1 != null) {
								if (v0.equals(v1)) {
									// Porównywanie ID dokumentu
									if (arg0[DOCID].equals(arg1[DOCID]))
										return (Integer) (arg0[LP]) - (Integer) arg1[LP]; // Porównanie
																				// pozycji
																				// elementów
									else
										return (Integer) arg0[DOCID] - (Integer) arg1[DOCID];
								} else
									return (int) (v0 - v1); // Porównywanie
															// dokumentów

							}
							else
								return 0; // TODO Błąd, ale lepiej
											// wyeksportować,
											// niz zgłosic błed;
						} else
							return (Integer) arg0[ROOT] - (Integer) arg1[ROOT]; // Porównywanie
																				// pacjentów
					}
				});

				// Budowanie listy kolumn wzorcowych
				JSONArray docs = (JSONArray) def.get("docs");
				for (int i = 0; i < tr.size(); i++) {
					JSONObject n = ((JSONObject) tr.get(i));
					String docId = (String) n.get("id");
					for (int d = 0; d < docs.size(); d++) {
						JSONObject doc = (JSONObject) docs.get(d);
						if (doc.get("id").toString().equalsIgnoreCase(docId)) {
							JSONArray itms = (JSONArray) doc.get("items");
							addCols(n.get("uniqueId").toString(), null, "", itms, cols);
							break;
						}
					}
				}

				// Wstawianie rzeczywistych kolumn - przeglądam wszystkie dane i
				// dodaje wszystkie mozliwe kolumny

				for (Object[] r : ls) {
					try {
						JSONObject o = (JSONObject) parser.parse(r[CONTENT].toString());
						for (Object nm : o.keySet()) {
							boolean found = false;
							for (ColHeader c : cols) {
								if (c.same(nm.toString(), r)) {
									found = true;
									c.use = true;
									break;
								}
							}
							if (!found) {
								// Szukam bazowego wpisu
								for (int i = 0; i < cols.size(); i++) {
									if (cols.get(i).sameBase(nm.toString(), r)) {
										// Zanalzłem bazowy - wstawiam
										ColHeader c = new ColHeader(r[UNIQUEID].toString(), r[PATH].toString(), r[FPATH].toString(), null, nm.toString());
										c.use = true;
										cols.add(i, c);
										break;
									}
								}
							}
						}
					} catch (ParseException e) {
						// Pomijam błędne wpisy
					}
				}

				// Usuwam nieużywane kolumny

				for (int i = cols.size() - 1; i >= 0; i--) {
					if (!cols.get(i).use)
						cols.remove(i);
				}

			} catch (ParseException e1) {
				RequestUtil.showError("Nieprawidłowa definicja rejestru");
			}

			// Eksport danych do Excela

			resp.setHeader("Content-Disposition", "attachment; filename=" + reg.getName() + ".xls");
			try {

				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet("Eksport danych");
				HSSFRow rowhead = sheet.createRow((short) 0);

				rowhead.createCell((short) 0).setCellValue("id");

				// Tworzenie kolumn
				for (short i = 0; i < cols.size(); i++) {
					ColHeader c = cols.get(i);
					rowhead.createCell(i).setCellValue(c.path + '/' + c.fpath + '/' + c.fld);
				}

				// Zapis danych do kolumn

				Object lastRootId = null;
				short rowId = 0;
				HSSFRow row = null;
				for (short index = 0; index < ls.size(); index++) {
					Object[] r = ls.get(index);
					try {
						if (!r[ROOT].equals(lastRootId)) {
							rowId++;
							lastRootId = r[ROOT];
							row = sheet.createRow(rowId);
						}
						JSONObject o = (JSONObject) parser.parse(r[CONTENT].toString());
						for (Object nm : o.keySet()) {
							boolean found = false;
							for (int i = 0; i < cols.size(); i++) {
								ColHeader c = cols.get(i);
								if (c.same(nm.toString(), r)) {
									row.createCell((short) i).setCellValue(o.get(nm).toString());
									found = true;
									break;
								}
							}
							if (!found) {
								RequestUtil.showError("Nie odnaleziono kolumny: " + r);
							}
						}
					} catch (ParseException e) {
						// Pomijam błędne wpisy
					}
				}

				// Generowanie odpowiedzi
				java.io.OutputStream out = resp.getOutputStream();
				wb.write(out);
				out.close();

				em.getTransaction().commit();

			} catch (Exception e) {
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
				System.out.println(" Dane nie mogą być wyeksportowane: " + e);
				throw e;
			}

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

	private void addCols(String uid, String path, String fpth, JSONArray itms, ArrayList<ColHeader> cols) {
		if (itms == null)
			return;

		for (int i = 0; i < itms.size(); i++) {
			JSONObject o = (JSONObject) itms.get(i);
			if (o.get("kind").equals("Elem")) {
				// Szukiam definicji elementu
				Object nm = o.get("id");
				for (Object e : elems) {
					if (((JSONObject) e).get("id").equals(nm)) {
						// Znalazłem def. elekentu - dodaję pola
						JSONArray its = (JSONArray) ((JSONObject) e).get("items");
						Object sid;
						for (int p = 0; p < its.size(); p++) {
							sid = ((JSONObject) its.get(p)).get("id");
							String f = ((sid == null || sid.toString().isEmpty()) ? nm.toString() : nm.toString() + "/" + sid.toString());
							cols.add(new ColHeader(uid, path, fpth, o.get("id").toString(), f)); // Dodawanie
																									// elementów
																									// do
																									// kolumn
																									// (Wszystkich
																									// pól
																									// elementu)
						}
					}
				}
			} else
				addCols(uid, path, (fpth.isEmpty() ? "" : fpth + "/") + o.get("id"), (JSONArray) o.get("items"), cols);
		}
	}
}
