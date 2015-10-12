package com.blsoft.rareacare.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.blsoft.rareacare.model.User;
import com.blsoft.rareacare.shared.LogOperation;

import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;

@Entity
public class UserLoginLog implements Serializable {

	private static final long serialVersionUID = 1L;

	public UserLoginLog() {
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	private long id;
	private Timestamp logTime = (new Timestamp(new java.util.Date().getTime()));
	private LogOperation opertion;
	@ManyToOne
	private User user;
	private String comment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Timestamp getLogTime() {
		return logTime;
	}

	public void setLogTime(Timestamp param) {
		this.logTime = param;
	}

	public LogOperation getOpertion() {
		return opertion;
	}

	public void setOpertion(LogOperation param) {
		this.opertion = param;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User param) {
		this.user = param;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String param) {
		this.comment = param;
	}

}