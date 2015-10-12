package com.blsoft.rareacare.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.blsoft.rareacare.model.Institution;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.MERGE;

import org.eclipse.persistence.annotations.JoinFetch;

import static org.eclipse.persistence.annotations.JoinFetchType.OUTER;

/**
 * Entity implementation class for Entity: User
 * 
 */
@Entity
@Table(name = "Users")
public class User extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;
	@Basic
	@Column(nullable = false)
	@NotNull
	private String name;
	@Basic
	@Column(nullable = false)
	@NotNull
	private String firstName;
	@Basic
	private String login;
	@Basic
	private String password;
	@OneToMany(mappedBy = "owner")
	private Collection<Registry> ownedRegitries;
	@OneToMany(mappedBy = "user")
	private Collection<RegUser> regUser;
	@OneToMany(mappedBy = "user")
	private Collection<UserLoginLog> userLoginLog;
	private String telephone;
	private String email;
	@Basic(optional = false)
	private Boolean isSuperuser = false;
	@ManyToOne(cascade = { PERSIST, MERGE })
	@JoinFetch(OUTER)
	private Institution institution;
	@Column(length = 32000)
	private String comment;

	public User() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String param) {
		this.name = param;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String param) {
		this.firstName = param;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String param) {
		this.login = param;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String param) {
		this.password = param;
	}

	public Collection<Registry> getOwnedRegitries() {
		return ownedRegitries;
	}

	public void setOwnedRegitries(Collection<Registry> param) {
		this.ownedRegitries = param;
	}

	public Collection<RegUser> getRegUser() {
		return regUser;
	}

	public void setRegUser(Collection<RegUser> param) {
		this.regUser = param;
	}

	public Collection<UserLoginLog> getUserLoginLog() {
		return userLoginLog;
	}

	public void setUserLoginLog(Collection<UserLoginLog> param) {
		this.userLoginLog = param;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String param) {
		this.telephone = param;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String param) {
		this.email = param;
	}

	public Boolean getIsSuperuser() {
		return isSuperuser;
	}

	public void setIsSuperuser(Boolean param) {
		this.isSuperuser = param;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution param) {
		this.institution = param;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String param) {
		this.comment = param;
	}

}
