package com.blsoft.rareacare.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/** Pojenyczy fakt w dokumencie - tu są składowane dane wpisane przez
 * użytkownika. Generalnie jedenrekord odpowiada jednemu elementowi dokumentu -
 * np. ECHO - może zawierać kilka pól */
@Entity
public class Fact extends EntityBase {

	private static final long serialVersionUID = 1L;
	@Lob private String path;
	@Lob private String content;
	@ManyToOne private Document document;
	
	/** Lp na cele dobrego sortowania w czasie eksportu */
	private Integer lp;

	public Fact() {
	}

	public String getPath() {
		return path;
	}

	public void setPath(String param) {
		this.path = param;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String param) {
		this.content = param;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document param) {
		this.document = param;
	}

	public Integer getLp() {
		return lp;
	}

	public void setLp(Integer param) {
		this.lp = param;
	}

}