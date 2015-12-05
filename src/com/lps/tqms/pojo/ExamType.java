package com.lps.tqms.pojo;

public enum ExamType {
	CHINESE("语文", "01", "s_1"),
	MATH("数学", "02", "s_2"),
	MATH_ARTS("数学文", "03", "s_3"),
	MATH_SCIENCE("数学理", "04", "s_4"),
	ENGLISH("英语", "05", "s_5"),
	POLITICS("政治", "06", "s_6"),
	HISTORY("历史", "07", "s_7"),
	GEOGRAPHY("地理", "08", "s_8"),
	PHYSICS("物理", "09", "s_9"),
	CHEMISTRY("化学", "10", "s_10"),
	BIOLOGY("生物", "11", "s_11");
  	public String name;
  	public String code;
  	public String queryName;
  	private ExamType(String name, String code, String queryName) {
  		this.name = name;
  		this.code = code;
  		this.queryName = queryName;
  	}
  	public static ExamType get(String code) {
  		for (ExamType et : values()) {
			if (et.code.equalsIgnoreCase(code)) {
				return et;
			}
		}
  		return null;
  	}
}
