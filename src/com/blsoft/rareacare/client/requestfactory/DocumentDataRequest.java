package com.blsoft.rareacare.client.requestfactory;

import java.util.List;

import com.blsoft.rareacare.server.BaseServiceLocator;
import com.blsoft.rareacare.server.reqservices.DocumentService;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(value = DocumentService.class, locator = BaseServiceLocator.class)
public interface DocumentDataRequest extends RequestContext {

	// Dokumenty
	Request<DocumentProxy> findDocument(Integer id);

	Request<DocumentProxy> getDocWithFacts(Integer id);

	Request<Void> removeFactsFromDoc(Integer docId);

	Request<DocumentProxy> persistAndLoad(DocumentProxy e, Boolean requery);

	Request<List<DocumentProxy>> getRootDocs(Integer regId, Integer from, Integer count, String patern, String orderby, Boolean sortAscending, String fields);

	Request<Integer> getRootDocCount(Integer regId, String patern, String fields);

	Request<Void> removeDoc(Integer docId);

	Request<String> removeAttachment(Integer attId);

	/** Zwraca wszystkie dokumenty, które u swojej podstawy mają wskazany
	 * dokument root */
	Request<List<DocumentProxy>> getDocs(Integer rootDocId);

	Request<Void> persistFact(FactProxy e);

	Request<Void> removeFact(FactProxy e);
	
}