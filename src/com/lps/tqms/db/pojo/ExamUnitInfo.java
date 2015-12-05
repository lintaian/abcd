package com.lps.tqms.db.pojo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_exam_unit_info")
public class ExamUnitInfo {
	@Id
	private int id;
	@Column
	private String unitCode;
	@Column
	private String unitName;
	@Column
	private String parentCode;
	public int getId() {
		return id;
	}
	public String getUnitCode() {
		return unitCode;
	}
	public String getUnitName() {
		return unitName;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
}
