package com.blsoft.rareacare.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.blsoft.rareacare.shared.RegRights;
import javax.persistence.Enumerated;
import static javax.persistence.EnumType.ORDINAL;

@Entity
@Table(name = "RegUser")
public class RegUser extends EntityBase {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	private User user;

	@ManyToOne
	private Registry registry;
	@Column(nullable = false)
	@Enumerated(ORDINAL)
	private RegRights canRead;

	@Column(nullable = false)
	private boolean canDefine;

	@Column(nullable = false)
	@Enumerated(ORDINAL)
	private RegRights canEdit;

	@Column(nullable = false)
	@Enumerated(ORDINAL)
	private RegRights canCorrect;

	static public RegUser NewSuperUser(Registry reg, User usr) {
		RegUser u = new RegUser();
		u.canCorrect = RegRights.ALL;
		u.canDefine = true;
		u.canEdit = RegRights.ALL;
		u.canRead = RegRights.ALL;
		u.registry = reg;
		u.user = usr;
		return u;
	}
	
	public RegUser() {
	}

	public User getUser() {
		return user;
	}

	public void setUser(User param) {
		this.user = param;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry param) {
		this.registry = param;
	}

	public RegRights getCanRead() {
		return canRead;
	}

	public void setCanRead(RegRights param) {
		this.canRead = param;
	}

	public boolean getCanDefine() {
		return canDefine;
	}

	public void setCanDefine(boolean param) {
		this.canDefine = param;
	}

	public RegRights getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(RegRights param) {
		this.canEdit = param;
	}

	public RegRights getCanCorrect() {
		return canCorrect;
	}

	public void setCanCorrect(RegRights param) {
		this.canCorrect = param;
	}

}