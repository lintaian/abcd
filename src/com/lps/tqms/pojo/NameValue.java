package com.lps.tqms.pojo;

public class NameValue {
	public String name;
	public double val;
	public String code;
	
	public NameValue() {}
	public NameValue(String name, double val) {
		this.name = name;
		this.val = val;
	}
	public NameValue(String name, double val, String code) {
		super();
		this.name = name;
		this.val = val;
		this.code = code;
	}
}
