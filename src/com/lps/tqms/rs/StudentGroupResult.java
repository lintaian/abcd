package com.lps.tqms.rs;

import java.util.ArrayList;
import java.util.List;

public class StudentGroupResult {
	public String studentName;
	public String examName;
	public List<String> cNames = new ArrayList<String>();
	public List<String> cCodes = new ArrayList<String>();
	public List<Object> datas = new ArrayList<Object>();
	public StudentGroupResult() {
	}
	public StudentGroupResult(String studentName, String examName) {
		super();
		this.studentName = studentName;
		this.examName = examName;
	}
}
