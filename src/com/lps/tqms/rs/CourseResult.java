package com.lps.tqms.rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseResult {
	public String unitName;
	public String unitCode;
	public String courseName;
	public List<String> aNames = new ArrayList<String>();
	public List<String> kNames = new ArrayList<String>();
	public List<Object> markLines = new ArrayList<Object>();
	public Map<String, Object> points = new HashMap<String, Object>();
	public List<List<Object>> childs = new ArrayList<List<Object>>();
	public int students;
	
	public CourseResult() {
	}
	public CourseResult(String unitName, String unitCode) {
		super();
		this.unitName = unitName;
		this.unitCode = unitCode;
	}
}
