package com.blsoft.rareacare.model;

import javax.persistence.*;

import com.blsoft.rareacare.client.requestfactory.AddressProxy;

import java.io.Serializable;

@Embeddable
public class Address implements Serializable, AddressProxy {

	private static final long serialVersionUID = 1L;
	@Column(length = 250)
	private String street;
	@Column(length = 50)
	private String str_no;
	@Column(length = 50)
	private String str_loc;
	private String city;
	@Column(length = 30)
	private String zipcode;
	@Column(length = 100)
	private String region;
	@Column(length = 100)
	private String country;

	public String getStreet() {
		return street;
	}

	public void setStreet(String param) {
		this.street = param;
	}

	public String getStr_no() {
		return str_no;
	}

	public void setStr_no(String param) {
		this.str_no = param;
	}

	public String getStr_loc() {
		return str_loc;
	}

	public void setStr_loc(String param) {
		this.str_loc = param;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String param) {
		this.city = param;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String param) {
		this.zipcode = param;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String param) {
		this.region = param;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String param) {
		this.country = param;
	}


}