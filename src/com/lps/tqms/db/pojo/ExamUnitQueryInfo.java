package com.lps.tqms.db.pojo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_exam_unit_query_info")
public class ExamUnitQueryInfo {
	@Id
	private int id;
	@Column
	private String queryId;
	@Column
	private String unitCode;
	@Column
	private String unitName;
	@Column
	private String parentCode;
	@Column
	private String unitTypeName;
	@Column
	private int unitTypeCode;
	public int getId() {
		return id;
	}
	public String getQueryId() {
		return queryId;
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
	public String getUnitTypeName() {
		return unitTypeName;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
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
	public void setUnitTypeName(String unitTypeName) {
		this.unitTypeName = unitTypeName;
	}
	public int getUnitTypeCode() {
		return unitTypeCode;
	}
	public void setUnitTypeCode(int unitTypeCode) {
		this.unitTypeCode = unitTypeCode;
	}
}
