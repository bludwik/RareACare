package com.blsoft.rareacare.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/** Załaczniki do dokumentu w postaci plików PDF */
@Entity
public class DocAttach extends EntityBase {

	private static final long serialVersionUID = 4032435044929690139L;

	@ManyToOne private Document parent;
	private String name;

	private String path;

	// @Lob
	// @Basic(fetch=FetchType.LAZY, optional=false)
	// private Byte[] content;
	/** @return the document */
	public Document getParent() {
		return parent;
	}

	/** @param document
	 *            the document to set */
	public void setParent(Document document) {
		this.parent = document;
	}

	/** @return the name */
	public String getName() {
		return name;
	}

	/** @param name
	 *            the name to set */
	public void setName(String name) {
		this.name = name;
	}

	/** względna ścieżka do pliku w stosunku do katalogu z dokumentami z opcji ogólnych */
	public String getPath() {
		return path;
	}

	public void setPath(String param) {
		this.path = param;
	}

}
