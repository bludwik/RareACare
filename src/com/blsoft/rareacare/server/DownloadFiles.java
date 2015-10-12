package com.blsoft.rareacare.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.blsoft.rareacare.model.DocAttach;

@SuppressWarnings("serial")
public class DownloadFiles extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		int id = Integer.parseInt(req.getParameter("id"));
		if (id <= 0)
			throw new ServletException("Nieprawidłowa wartość parametru");

		EntityManager em = RequestUtil.getEntityManagerFactory().createEntityManager();
		ThreadLocalData.setEm(em);
		ThreadLocalData.setRequest(req);
		
		
		em.getTransaction().begin(); 
		try {
			
			RequestUtil.checkUser();

			DocAttach att = em.find(DocAttach.class, id);
			if (att == null)
				RequestUtil.showError("Nie odnaleziono wskazanego załącznika o ID = " + id);
			
			String pth = getServletContext().getInitParameter("attachmentsPath");
			File f = new File(pth + att.getPath());
			if (!f.exists())
				RequestUtil.showError("Nie odnaleziono wskazanego pliku na serwerze");
			
			RequestUtil.checkCanRead(att.getParent());
			
			// resp.setContentType("application/pdf");
			java.io.OutputStream out = resp.getOutputStream();

			byte[] buffer = new byte[4096];
			int n;


			FileInputStream in = new FileInputStream(f);
			try {
				while ((n = in.read(buffer)) > 0) {
					out.write(buffer, 0, n);
				}
			} finally {
				out.close();
				in.close();
			}

			resp.setHeader("Content-Disposition", "attachment; filename=" + att.getName());
			
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
