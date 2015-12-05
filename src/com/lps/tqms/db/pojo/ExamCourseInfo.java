package com.lps.tqms.db.pojo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_exam_course_info")
public class ExamCourseInfo {
	@Id
	private int id;
	@Column
	private String examCode;
	@Column
	private String examName;
	@Column
	private double examPoint;
	@Column
	private int comprehensive;
	public int getId() {
		return id;
	}
	public String getExamCode() {
		return examCode;
	}
	public String getExamName() {
		return examName;
	}
	public double getExamPoint() {
		return examPoint;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public void setExamPoint(double examPoint) {
		this.examPoint = examPoint;
	}
	public int getComprehensive() {
		return comprehensive;
	}
	public void setComprehensive(int comprehensive) {
		this.comprehensive = comprehensive;
	}
}
