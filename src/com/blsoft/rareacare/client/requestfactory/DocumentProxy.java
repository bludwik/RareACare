package com.blsoft.rareacare.client.requestfactory;

import java.util.List;

import com.blsoft.rareacare.model.Document;
import com.blsoft.rareacare.server.EntityLocator;
import com.blsoft.rareacare.shared.DocStatus;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(value = Document.class, locator = EntityLocator.class)
public interface DocumentProxy extends BaseEntityProxy {

	/** Dokument nadrzędny */
	DocumentProxy getParent();

	/** Określanie wskaźnika na dokument nadrzędny */
	void setParent(DocumentProxy param);

	/** dokementy podrzędne */
	List<DocumentProxy> getSubDocs();

	/** Przypisanie dokumentów podrzędnych */
	void setSubDocs(List<DocumentProxy> param);

	/** ID definicji dokumentu */
	String getDocDefId();

	void setDocDefId(String param);


	/** Rejestr, z którym jest powiązany dokument */
	public RegistryProxy getRegistry();
	public void setRegistry(RegistryProxy param);

	
	
	/** Ścieżka do dokumentu w hierarchii dokumentów */
	public String getPath();
	
	public void setPath(String param);

	/** Lista faktów w ramach dokumentu */
	public List<FactProxy> getFacts();

	public void setFacts(List<FactProxy> param); 
	
	/** Lista załaczników */
	public List<DocAttachProxy> getAttachments();

	public void setAttachments(List<DocAttachProxy> param);	
	
	/** Unikalny ID okreslający pozycje w drzewie - powiązny z uniqueID w definicji rejestru w drzewie dokumentów */
	public String getUniqueId() ;

	public void setUniqueId(String param) ;

//	public String getUniqueSubValues();
	
//	public void setUniqueSubValues(String param);
	
	public Integer getRootId();
	
	public void setRootId(Integer rootId);
	
	public DocStatus getStatus();

	public void setStatus(DocStatus status);	
	
	EntityProxyId<DocumentProxy> stableId();
	

}