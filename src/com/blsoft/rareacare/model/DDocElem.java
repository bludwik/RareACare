package com.blsoft.rareacare.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.persistence.Lob;

@Entity
@Table(name = "DDocElem")
public class DDocElem {

	@Id @Column(length = 100, nullable = false) private String id;

	@Version @Column(name = "version", nullable = false) private Long version;

	@ManyToOne private User creator;
	@ManyToOne private User updater;
	@Basic private java.sql.Timestamp createTime;
	@Basic private java.sql.Timestamp updateTime;

	@Column(nullable = false) private String label;
	@Column(length = 5000) private String description;

	@Lob
	private String def;

	public DDocElem() {
	}

	public String getId() {
		return id;
	}

	public void setId(String param) {
		this.id = param;
	}

	public Long getVersion() {
		return version;
	}

	/** @return U�ytkownik, kt�ry utworzy� objekt */
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	/** @return U�ytkownik aktualizuj�cy wpis */
	public User getUpdater() {
		return updater;
	}

	public void setUpdater(User updater) {
		this.updater = updater;
	}

	/** @return the createTime */
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	/** @param createTime
	 *            the createTime to set */
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	/** @return the updateTime */
	public java.sql.Timestamp getUpdateTime() {
		return updateTime;
	}

	/** @param updateTime
	 *            the updateTime to set */
	public void setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String param) {
		this.label = param;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String param) {
		this.description = param;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String param) {
		this.def = param;
	}

}