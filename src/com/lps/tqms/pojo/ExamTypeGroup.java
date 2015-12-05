package com.lps.tqms.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum ExamTypeGroup {
	PUBLIC("公共课", "101", "score_pub", ExamType.CHINESE, ExamType.ENGLISH),
	ARTS("文科", "102", "score_art", ExamType.CHINESE, ExamType.MATH_ARTS, 
			ExamType.ENGLISH, ExamType.POLITICS, 
			ExamType.HISTORY, ExamType.GEOGRAPHY),
	SCIENCE("理科", "103", "score_sci", ExamType.CHINESE, ExamType.MATH_SCIENCE, 
			ExamType.ENGLISH, ExamType.PHYSICS, 
			ExamType.CHEMISTRY, ExamType.BIOLOGY),
	ARTS_MAJOR("文科综合", "104", "score_art_major", ExamType.POLITICS, ExamType.HISTORY, 
			ExamType.GEOGRAPHY),
	SCIENCE_MAJOR("理科综合", "105", "score_sci_major", ExamType.PHYSICS, ExamType.CHEMISTRY, 
			ExamType.BIOLOGY),
	ALL("全科", "106", "score", ExamType.CHINESE, ExamType.MATH, 
			ExamType.MATH_ARTS, ExamType.MATH_SCIENCE, ExamType.ENGLISH, 
			ExamType.POLITICS, ExamType.HISTORY, ExamType.GEOGRAPHY,
			ExamType.PHYSICS, ExamType.CHEMISTRY, ExamType.BIOLOGY);
  	public ExamType[] exams;
  	public String name;
  	public String code;
  	public String queryName;
  	private ExamTypeGroup(String name, String code, String queryName, ExamType ...exams) {
  		this.name = name;
  		this.exams = exams;
  		this.code = code;
  		this.queryName = queryName;
  	}
  	public static ExamTypeGroup get(String code) {
  		for (ExamTypeGroup et : values()) {
  			if (et.code.equals(code)) {
  				return et;
  			}
  		}
  		return null;
  	}
  	public static ExamTypeGroup getByClassCode(String classCode) {
  		String code = classCode.substring(12, 14);
  		if (code.equals("01")) {
			return ExamTypeGroup.ARTS;
		} else if (code.equals("02")) {
			return ExamTypeGroup.SCIENCE;
		} else {
			return ExamTypeGroup.ALL;
		}
  	}
  	public List<String> getExamNames() {
  		List<String> rs = new ArrayList<String>();
  		for (ExamType examType : exams) {
  			rs.add(examType.name);
		}
  		return rs;
  	}
  	public List<String> getExamCodes() {
  		List<String> rs = new ArrayList<String>();
  		for (ExamType examType : exams) {
  			rs.add(examType.code);
		}
  		return rs;
  	}
  	public List<String> getExamNames(Set<ExamType> exams) {
  		List<String> rs = new ArrayList<String>();
  		for (ExamType examType : this.exams) {
  			if (exams.contains(examType)) {
  				rs.add(examType.name);
			}
  		}
  		return rs;
  	}
  	public List<String> getExamCodes(Set<ExamType> exams) {
  		List<String> rs = new ArrayList<String>();
  		for (ExamType examType : this.exams) {
  			if (exams.contains(examType)) {
  				rs.add(examType.code);
			}
  		}
  		return rs;
  	}
}
