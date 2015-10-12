package com.blsoft.rareacare.model;

import static org.eclipse.persistence.annotations.JoinFetchType.INNER;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.JoinFetch;

import com.blsoft.rareacare.model.Document;

import javax.persistence.Lob;

@Entity
@Table(name = "Registry")
public class Registry extends EntityBase {

	private static final long serialVersionUID = 1L;
	@Column(nullable = false) @NotNull private String name;
	@ManyToOne @JoinFetch(INNER) @NotNull private User owner;
	@OneToMany(mappedBy = "registry") private List<RegUser> regUsers;
	@Column(nullable = false) @Lob private String def;
	@Lob private String descr;
	@OneToMany(mappedBy = "registry") private List<Document> documents;
	private String rootDocDefId;
	
	/** Uprawnienia aktualnie zalogowanego użytkownika */
	@Transient
	private RegUser currRegUser;

	public Registry() {
	}

	public String getName() {
		return name;
	}

	public void setName(String param) {
		this.name = param;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User param) {
		this.owner = param;
	}

	public List<RegUser> getRegUsers() {
		return regUsers;
	}

	public void setRegUsers(List<RegUser> param) {
		this.regUsers = param;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String param) {
		this.def = param;
	}

	/** Opis rejestru */
	public String getDescr() {
		return descr;
	}

	public void setDescr(String param) {
		this.descr = param;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> param) {
		this.documents = param;
	}

	public String getRootDocDefId() {
		return rootDocDefId;
	}

	public void setRootDocDefId(String param) {
		this.rootDocDefId = param;
	}

	public RegUser getCurrRegUser() {
		return currRegUser;
	}

	public void setCurrRegUser(RegUser user) {
		this.currRegUser = user;
	}

}