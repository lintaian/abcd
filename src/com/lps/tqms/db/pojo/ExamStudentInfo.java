package com.lps.tqms.db.pojo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_exam_student_info")
public class ExamStudentInfo {
	@Id
	private int id;
	@Column
	private String studentId;
	@Column
	private String studentName;
	@Column
	private String classCode;
	@Column
	private String examCode;
	@Column
	private String examNo;
	public int getId() {
		return id;
	}
	public String getStudentId() {
		return studentId;
	}
	public String getStudentName() {
		return studentName;
	}
	public String getClassCode() {
		return classCode;
	}
	public String getExamCode() {
		return examCode;
	}
	public String getExamNo() {
		return examNo;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}
	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
	public void setExamNo(String examNo) {
		this.examNo = examNo;
	}
}
