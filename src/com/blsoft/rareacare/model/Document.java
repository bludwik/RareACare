package com.blsoft.rareacare.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.EnumType.ORDINAL;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.blsoft.rareacare.shared.DocStatus;


/** Pojedyncza instancja dokumentu w ramach pacjenta w rejestrze. Po za
 * referencją do pacjenta (a przez to do rejestru) zapamiętujemy też ID
 * definicji dokumentu w definicji rejestru Dokumenty moga być zagnieżdżone -
 * czyli dokument może mieć podddokumenty - zgodnie z definicją w drzewie
 * dokumentów w definicji rejestru
 * 
 * @author bartek */
@Entity
public class Document extends EntityBase { 
	

	private static final long serialVersionUID = 1L;

	@ManyToOne private Document parent;

	@OneToMany(mappedBy = "parent") private List<Document> subDocs;

	private String docDefId;

	@ManyToOne private Registry registry;

	private Integer rootId;

	@Lob private String path;

	@OneToMany(mappedBy = "document", fetch = EAGER, cascade = { PERSIST, REMOVE, REFRESH, MERGE, ALL }) private List<Fact> facts;

	private String uniqueId;

	@Enumerated(ORDINAL)
	private DocStatus status = DocStatus.NEW;
	
	@OneToMany(mappedBy = "parent", cascade = ALL) private List<DocAttach> attachments;	

	/** Dokument nadrzędny */
	public Document getParent() {
		return parent;
	}

	/** Określanie wskaźnika na dokument nadrzędny */
	public void setParent(Document param) {
		this.parent = param;
	}

	/** dokementy podrzędne */
	public List<Document> getSubDocs() {
		return subDocs;
	}

	/** Przypisanie dokumentów podrzędnych */
	public void setSubDocs(List<Document> param) {
		this.subDocs = param;
	}

	/** ID definicji dokumentu */
	public String getDocDefId() {
		return docDefId;
	}

	public void setDocDefId(String param) {
		this.docDefId = param;
	}

	/** Rejestr, z którym jest powiązany dokument */
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry param) {
		this.registry = param;
	}


	/** Id dokumentu roota - aby łatwo można bylo znaleźć wszystkie dokumenty w
	 * drzewie - czyli należące do wskazanego pacjenta
	 * 
	 * @return the rootId */
	public Integer getRootId() {
		return rootId;
	}

	public void setRootId(Integer rootId) {
		this.rootId = rootId;
	}

	/** Ścieżka do dokumentu w hierarchii dokumentów */
	public String getPath() {
		return path;
	}

	public void setPath(String param) {
		this.path = param;
	}

	/** Lista faktów w ramach dokumentu */
	public List<Fact> getFacts() {
		return facts;
	}

	public void setFacts(List<Fact> param) {
		this.facts = param;
	}

	/** Lista załaczników */
	public List<DocAttach> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<DocAttach> param) {
		this.attachments = param;
	}

	/** Unikalny ID okreslający pozycje w drzewie - powiązny z uniqueID w
	 * definicji rejestru w drzewie dokumentów */
	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String param) {
		this.uniqueId = param;
	}

	/**
	 * @return the status
	 */
	public DocStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(DocStatus status) {
		this.status = status;
	}

}