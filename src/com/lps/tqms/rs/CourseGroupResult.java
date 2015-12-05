package com.lps.tqms.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseGroupResult {
	public String unitName;
	public String unitCode;
	public String courseName;
	public List<String> cNames = new ArrayList<String>();
	public List<String> cCodes = new ArrayList<String>();
	public List<Object> childs = new ArrayList<Object>();
	public List<Object> markLines = new ArrayList<Object>();
	public Map<String, Object> points = new HashMap<String, Object>();
	public int students;
	
	public CourseGroupResult() {
	}
	public CourseGroupResult(String unitName, String unitCode) {
		super();
		this.unitName = unitName;
		this.unitCode = unitCode;
	}
}
