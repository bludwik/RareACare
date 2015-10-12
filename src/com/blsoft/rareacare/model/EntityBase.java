package com.blsoft.rareacare.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Klasa bazowa dla wszystkich objekt�w bazy. Dzi�ki temu mo�na stosowa�
 * generyczny locator
 * 
 * @author bartek
 * 
 */
@MappedSuperclass
public class EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	protected Integer id;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	@ManyToOne
	private User creator;
	@ManyToOne
	private User updater;
	@Basic
	private java.sql.Timestamp createTime;
	@Basic
	private java.sql.Timestamp updateTime;

	public Integer getId() {
		return id;
	}

	public Long getVersion() {
		return version;
	}

	/**
	 * 
	 * @return Użytkownik, który utworzył objekt
	 */
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	/**
	 * @return U�ytkownik aktualizuj�cy wpis
	 */
	public User getUpdater() {
		return updater;
	}

	public void setUpdater(User updater) {
		this.updater = updater;
	}

	/**
	 * @return the createTime
	 */
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the updateTime
	 */
	public java.sql.Timestamp getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public void setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	

}
