package com.lps.tqms.rs;

import java.util.ArrayList;
import java.util.List;

public class StudentResult {
	public String studentName;
	public String examName;
	public List<String> aNames = new ArrayList<String>();
	public List<String> kNames = new ArrayList<String>();
	public List<Object> datas = new ArrayList<Object>();
	public StudentResult() {
	}
	public StudentResult(String studentName, String examName) {
		super();
		this.studentName = studentName;
		this.examName = examName;
	}
}
