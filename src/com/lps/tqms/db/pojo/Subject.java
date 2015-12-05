package com.lps.tqms.db.pojo;
/**
 * 科目信息POJO
 * @author lta
 *
 */
public class Subject {
	private String id;
	private String name;
	private String queryName;
	private int artSci;
	private int real;
	private String description;
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getQueryName() {
		return queryName;
	}
	public int getArtSci() {
		return artSci;
	}
	public String getDescription() {
		return description;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	public void setArtSci(int artSci) {
		this.artSci = artSci;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getReal() {
		return real;
	}
	public void setReal(int real) {
		this.real = real;
	}
}
