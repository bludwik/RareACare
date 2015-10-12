package com.blsoft.rareacare.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.blsoft.rareacare.model.Address;
import com.blsoft.rareacare.model.User;

import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "institution")
public class Institution extends EntityBase {

	private static final long serialVersionUID = 1L;
	@NotNull
	private String name;
	@Embedded
	private Address address;
	@Column(length = 100)
	private String telephone;
	private String email;
	@OneToMany(mappedBy = "institution", cascade = ALL)
	private List<User> users;

	public Institution() {
	}

	public String getName() {
		return name;
	}

	public void setName(String param) {
		this.name = param;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address param) {
		this.address = param;
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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> param) {
		this.users = param;
	}
}