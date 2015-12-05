package com.lps.tqms.user;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.lps.tqms.pojo.UnitType;
import com.lps.tqms.pojo.UnitType.Type;

public class QueryInfo {
	private String queryId;
	private UnitQueryInfo unitQueryInfo = new UnitQueryInfo();
	private Set<UnitType> unitTypes = new TreeSet<UnitType>(new Comparator<UnitType>() {
		@Override
		public int compare(UnitType o1, UnitType o2) {
			return o1.getCode() - o2.getCode();
		}
	});

	public QueryInfo() {
	}
	public QueryInfo(String queryId, UnitQueryInfo unitQueryInfo) {
		super();
		this.queryId = queryId;
		this.unitQueryInfo = unitQueryInfo;
	}

	public Set<UnitType> getUnitTypes(int typeCode, boolean include) {
		Set<UnitType> types = new TreeSet<UnitType>(new Comparator<UnitType>() {
			@Override
			public int compare(UnitType o1, UnitType o2) {
				return o1.getCode() - o2.getCode();
			}
		});
		for (UnitType unitType : unitTypes) {
			if (unitType.getCode() > typeCode || (include && unitType.getCode() == typeCode)) {
				types.add(unitType);
			}
		}
		return types;
	}
	public UnitQueryInfo getUnitInfo(String unitCode) {
		UnitQueryInfoWrap rs = new UnitQueryInfoWrap();
		getUnitInfo(unitQueryInfo, unitCode, rs);
		return rs.info;
	}
	private void getUnitInfo(UnitQueryInfo info, String unitCode, UnitQueryInfoWrap rs) {
		if (info.getUnitCode().equals(unitCode)) {
			rs.info = info;
		} else {
			for (UnitQueryInfo queryInfo : info.getChilds()) {
				getUnitInfo(queryInfo, unitCode, rs);
			}
		}
	}
	public void buildUnitTypes() {
		buildUnitTypes(unitQueryInfo);
		unitTypes.remove(new UnitType(Type.GRADE));
	}
	private void buildUnitTypes(UnitQueryInfo unitQueryInfo) {
		if (!unitQueryInfo.isUnShow()) {
			unitTypes.add(new UnitType(unitQueryInfo.getUnitTypeName(), unitQueryInfo.getUnitTypeCode()));
		}
		for (UnitQueryInfo child : unitQueryInfo.getChilds()) {
			buildUnitTypes(child);
		}
	}
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	public UnitQueryInfo getUnitQueryInfo() {
		return unitQueryInfo;
	}
	public Set<UnitType> getUnitTypes() {
		return unitTypes;
	}
	public void setUnitQueryInfo(UnitQueryInfo unitQueryInfo) {
		this.unitQueryInfo = unitQueryInfo;
	}
	public void setUnitTypes(Set<UnitType> unitTypes) {
		this.unitTypes = unitTypes;
	}
}
