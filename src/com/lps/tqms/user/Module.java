package com.lps.tqms.user;

public class Module {
	private int id;
	private String name;
	private String url;
	private String img;
	private int parent;
	private String describe;
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public int getParent() {
		return parent;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public String getImg() {
		return img;
	}
	public String getDesc() {
		return describe;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public void setDesc(String desc) {
		this.describe = desc;
	}
}
