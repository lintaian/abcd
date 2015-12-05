package com.lps.tqms.rs;

import java.util.ArrayList;
import java.util.List;

public class StudentTemp {
	public String name;
	public List<Object> data = new ArrayList<Object>();
	public StudentTemp() {
	}
	public StudentTemp(String name) {
		this.name = name;
	}
	public StudentTemp(String name, List<Object> data) {
		super();
		this.name = name;
		this.data = data;
	}
}
