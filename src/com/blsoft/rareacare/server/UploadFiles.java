package com.blsoft.rareacare.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.shared.UConsts;

import org.apache.commons.fileupload.FileItem;

import com.blsoft.rareacare.model.DocAttach;
import com.blsoft.rareacare.model.Document;


public class UploadFiles extends UploadAction {
	private static final long serialVersionUID = 1L;

	Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
	/** Maintain a list with received files and their content types. */
	Hashtable<String, File> receivedFiles = new Hashtable<String, File>();

	/** Override executeAction to save the received files in a custom place and
	 * delete this items from session. */
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		String response = "";
		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				try {
					// / Create a new file based on the remote file name in the
					// client
					// String saveName =
					// item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+",
					// "_");
					// File file =new File("/tmp/" + saveName);

					// / Create a temporary file placed in /tmp (only works in
					// unix)
					// File file = File.createTempFile("upload-", ".bin", new
					// File("/tmp"));

					// / Create a temporary file placed in the default system
					// temp folder
					String pth = getServletContext().getInitParameter("attachmentsPath");
					String dir = item.getFieldName().substring(0, item.getFieldName().indexOf('_'));
					int docId = Integer.parseInt(dir.substring(dir.indexOf('#')+1));
					dir = dir.replace('#', File.separatorChar);
					
					(new File(pth + dir + File.separator)).mkdirs();
					File file = new File(pth + dir + File.separator + item.getFieldName() + '-' + item.getName()); //. createTempFile("upload-", ".bin");
					item.write(file);
					
					EntityManager em = RequestUtil.getEntityManagerFactory().createEntityManager();
					ThreadLocalData.setEm(em);
					ThreadLocalData.setRequest(request);
					
					em.getTransaction().begin(); 
					try {

						Document doc = em.find(Document.class, docId);

						RequestUtil.checkCanRead(doc);		

						DocAttach att = new DocAttach();
						att.setName(item.getName());
						att.setPath(dir + File.separator + item.getFieldName() + '-' + item.getName());
						java.sql.Timestamp tm = new java.sql.Timestamp(new java.util.Date().getTime()); 
						if (att.getId() == null) {
							att.setCreator(RequestUtil.checkUser());
							att.setCreateTime(tm);
						}
						att.setUpdater(RequestUtil.getCurrentUser());
						att.setUpdateTime(tm);
						att.setParent(doc);
						em.persist(att);
						
						// / Save a list with the received files
						receivedFiles.put(item.getFieldName(), file);
						receivedContentTypes.put(item.getFieldName(), item.getContentType());
												
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
					

					// / Send a customized message to the client.
					response += "File saved as " + file.getAbsolutePath();

				} catch (Exception e) {
					throw new UploadActionException(e);
				}
			}
		}

		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);

		// / Send your customized message to the client.
		return response;
	}

	/** Get the content of an uploaded file. */
	@Override
	public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fieldName = request.getParameter(UConsts.PARAM_SHOW);
		File f = receivedFiles.get(fieldName);
		if (f != null) {
			response.setContentType(receivedContentTypes.get(fieldName));
			FileInputStream is = new FileInputStream(f);
			copyFromInputStreamToOutputStream(is, response.getOutputStream());
		} else {
			renderXmlResponse(request, response, XML_ERROR_ITEM_NOT_FOUND);
		}
	}

	/** Remove a file when the user sends a delete request. */
	@Override
	public void removeItem(HttpServletRequest request, String fieldName) throws UploadActionException {
		File file = receivedFiles.get(fieldName);
		receivedFiles.remove(fieldName);
		receivedContentTypes.remove(fieldName);
		if (file != null) {
			file.delete();
		}
	}
}