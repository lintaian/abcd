package com.lps.tqms.user;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;

public class BatchAuth {
	private String examCode;
	private String examName;
	private String grade;
	private Date examDate;
	public Set<ExamType> exams = new TreeSet<ExamType>();
	public Set<ExamTypeGroup> groups = new TreeSet<ExamTypeGroup>();
	private List<QueryInfo> infos = new ArrayList<QueryInfo>();
	
	public BatchAuth() {
	}
	public BatchAuth(String examCode, String examName, String grade,
			Date examDate) {
		super();
		this.examCode = examCode;
		this.examName = examName;
		this.grade = grade;
		this.examDate = examDate;
	}
	
	public QueryInfo getQueryInfo() {
		return getQueryInfo("");
	}
	public QueryInfo getQueryInfo(String queryId) {
		if (queryId == null || "".equals(queryId)) {
			return infos.size() > 0 ? infos.get(0) : null;
		} else {
			for (QueryInfo info : infos) {
				if (queryId.equals(info.getQueryId())) {
					return info;
				}
			}
		}
		return null;
	}
	public String getExamCode() {
		return examCode;
	}
	public String getExamName() {
		return examName;
	}
	public String getGrade() {
		return grade;
	}
	public Date getExamDate() {
		return examDate;
	}
	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}
	public List<QueryInfo> getInfos() {
		return infos;
	}
	public void setInfos(List<QueryInfo> infos) {
		this.infos = infos;
	}
}
