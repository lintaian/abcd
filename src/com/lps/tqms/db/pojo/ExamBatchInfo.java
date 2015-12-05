package com.lps.tqms.db.pojo;

import java.sql.Date;
import java.util.Comparator;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_exam_batch_info")
public class ExamBatchInfo implements Comparator<ExamBatchInfo> {
	@Id
	private int id;
	@Column
	private String examCode;
	@Column
	private String examName;
	@Column
	private String grade;
	@Column
	private Date examDate;
	public int getId() {
		return id;
	}
	public String getExamCode() {
		return examCode;
	}
	public String getExamName() {
		return examName;
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
	public Date getExamDate() {
		return examDate;
	}
	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	@Override
	public int compare(ExamBatchInfo a, ExamBatchInfo b) {
		int result = a.examDate.compareTo(b.examDate);
		if (result == 0){
			result = a.examCode.compareTo(b.examCode);
			if (result ==0){
				result = a.id - b.id;
			}
		}
		return  result;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((examCode == null) ? 0 : examCode.hashCode());
		result = prime * result
				+ ((examDate == null) ? 0 : examDate.hashCode());
		result = prime * result
				+ ((examName == null) ? 0 : examName.hashCode());
		result = prime * result + ((grade == null) ? 0 : grade.hashCode());
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExamBatchInfo other = (ExamBatchInfo) obj;
		if (examCode == null) {
			if (other.examCode != null)
				return false;
		} else if (!examCode.equals(other.examCode))
			return false;
		if (examDate == null) {
			if (other.examDate != null)
				return false;
		} else if (!examDate.equals(other.examDate))
			return false;
		if (examName == null) {
			if (other.examName != null)
				return false;
		} else if (!examName.equals(other.examName))
			return false;
		if (grade == null) {
			if (other.grade != null)
				return false;
		} else if (!grade.equals(other.grade))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
}
