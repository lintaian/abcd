package com.lps.tqms.pojo;

public class UnitType {
	private String name;
	private int code;
	private String unitCode;
	public UnitType() {
	}
	public UnitType(String name, int code) {
		super();
		this.name = name;
		this.code = code;
	}
	public UnitType(Type ut) {
		super();
		this.name = ut.name;
		this.code = ut.code;
	}
	public UnitType(String name, int code, String unitCode) {
		super();
		this.name = name;
		this.code = code;
		this.unitCode = unitCode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((unitCode == null) ? 0 : unitCode.hashCode());
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
		UnitType other = (UnitType) obj;
		if (code != other.code)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (unitCode == null) {
			if (other.unitCode != null)
				return false;
		} else if (!unitCode.equals(other.unitCode))
			return false;
		return true;
	}
	public String getName() {
		return name;
	}
	public int getCode() {
		return code;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public enum Type {
		COUNTRY("国", 1000),
		PROVINCE("省", 2000),
		CITY("市", 3000),
		AREA("区县", 4000),
		SCHOOL("校", 5000),
		GRADE("年级", 6000),
		CLASS("班级", 7000),
		STUDENT("学生", 8000);
		public String name;
		public int code;
		private Type(String name, int code) {
			this.name = name;
			this.code = code;
		}
		public static boolean isInclude(int code) {
			boolean flag = false;
			for (Type t : values()) {
				if (t.code == code) {
					flag = true;
					break;
				}
			}
			return flag;
		}
		public static String getCodeList() {
			StringBuilder sb = new StringBuilder("(");
			for (Type t : values()) {
				sb.append(t.code);
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			sb.append(")");
			return sb.toString();
		}
	}
}
