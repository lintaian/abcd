package com.lps.tqms.user;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.pojo.UnitType.Type;
import com.lps.tqms.util.Util;

public class UnitQueryInfo implements Cloneable {
	private String unitCode;
	private String unitName;
	private String unitTypeName;
	private int unitTypeCode;
	private String parentCode;
	private boolean unShow = true;
	private boolean showStudent = false;
	private boolean compare = true;
	private Set<UnitQueryInfo> childs = new TreeSet<UnitQueryInfo>(new Comparator<UnitQueryInfo>() {
		@Override
		public int compare(UnitQueryInfo o1, UnitQueryInfo o2) {
			return o1.unitCode.compareTo(o2.unitCode);
		}
	});
	private Set<ExamType> exams = new TreeSet<ExamType>(new Comparator<ExamType>() {
		@Override
		public int compare(ExamType o1, ExamType o2) {
			return Integer.parseInt(o1.code) - Integer.parseInt(o2.code);
		}
	});
	private Set<ExamTypeGroup> examGroups = new TreeSet<ExamTypeGroup>(new Comparator<ExamTypeGroup>() {
		@Override
		public int compare(ExamTypeGroup o1, ExamTypeGroup o2) {
			return Integer.parseInt(o1.code) - Integer.parseInt(o2.code);
		}
	});
	
	public UnitQueryInfo() {
	}
	
	public UnitQueryInfo(String unitCode, String unitName,
			String unitTypeName, int unitTypeCode, String parentCode, boolean unShow,
			boolean showStudent, boolean compare) {
		super();
		this.unitCode = unitCode;
		this.unitName = unitName;
		this.unitTypeName = unitTypeName;
		this.unitTypeCode = unitTypeCode;
		this.parentCode = parentCode;
		this.unShow = unShow;
		this.showStudent = showStudent;
		this.compare = compare;
	}

	@Override
	public UnitQueryInfo clone() {
		UnitQueryInfo rs = new UnitQueryInfo();
		rs.unitCode = unitCode;
		rs.unitName = unitName;
		rs.unitTypeCode = unitTypeCode;
		rs.unitTypeName = unitTypeName;
		rs.unShow = unShow;
		rs.showStudent = showStudent;
		rs.compare = compare;
		rs.parentCode = parentCode;
		for (UnitQueryInfo child : childs) {
			rs.childs.add(child.clone());
		}
		for (ExamType examType : exams) {
			rs.exams.add(examType);
		}
		for (ExamTypeGroup examTypeGroup : examGroups) {
			rs.examGroups.add(examTypeGroup);
		}
		return rs;
	}
	public UnitQueryInfo getUnitInfo(String unitCode) {
		UnitQueryInfoWrap rs = new UnitQueryInfoWrap();
		getUnitInfo(this, unitCode, rs);
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
	public Set<String> getUnitCodeChilds(int childTypeCode, String examCode, String groupCode) {
		Set<String> rs = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		getUnitInfoChilds(this, childTypeCode, examCode, groupCode, rs);
		return rs;
	}
	private void getUnitInfoChilds(UnitQueryInfo queryInfo, int childTypeCode, String examCode, String groupCode, Set<String> unitCodes) {
		if (childTypeCode == 0) {
			if (queryInfo.getUnitTypeCode() == Type.SCHOOL.code) {
				getUnitInfoChilds(queryInfo, Type.CLASS.code, examCode, groupCode, unitCodes);
			} else {
				for (UnitQueryInfo info : queryInfo.childs) {
					if (examCode != null && !"".equals(examCode)) {
						examCode = examCode.substring(examCode.length() - 2, examCode.length());
						if (info.getExams().contains(ExamType.get(examCode))) {
							unitCodes.add(info.getUnitCode());
						}
					} else {
						if (info.getExamGroups().contains(ExamTypeGroup.get(groupCode))) {
							unitCodes.add(info.getUnitCode());
						}
					}
				}
			}
		} else {
			if (childTypeCode == Type.GRADE.code) {
				getUnitInfoChilds(queryInfo, Type.CLASS.code, examCode, groupCode, unitCodes);
			} else {
				if (queryInfo.getUnitTypeCode() == childTypeCode) {
					if (examCode != null && !"".equals(examCode)) {
						examCode = examCode.substring(examCode.length() - 2, examCode.length());
						if (queryInfo.getExams().contains(ExamType.get(examCode))) {
							unitCodes.add(queryInfo.getUnitCode());
						}
					} else {
						if (queryInfo.getExamGroups().contains(ExamTypeGroup.get(groupCode))) {
							unitCodes.add(queryInfo.getUnitCode());
						}
					}
				} else {
					for (UnitQueryInfo unitQueryInfo : queryInfo.childs) {
						getUnitInfoChilds(unitQueryInfo, childTypeCode, examCode, groupCode, unitCodes);
					}
				}
			}
		}
	}
	public Set<String> getUnitInfoChilds(int childTypeCode, int artSci, boolean unShow) {
		Set<String> rs = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		getUnitInfoChilds(this, childTypeCode, artSci, rs, unShow);
		return rs;
	}
	private void getUnitInfoChilds(UnitQueryInfo queryInfo, int childTypeCode, int artSci, Set<String> unitCodes, boolean unShow) {
		if (childTypeCode == 0) {
			if (queryInfo.getUnitTypeCode() == Type.SCHOOL.code) {
				getUnitInfoChilds(queryInfo, Type.CLASS.code, artSci, unitCodes, unShow);
			} else {
				for (UnitQueryInfo info : queryInfo.childs) {
					if (unShow || !info.isUnShow()) {
						if (info.getUnitTypeCode() == Type.CLASS.code) {
							if (Util.matchArtSci(info.getUnitCode(), artSci)) {
								unitCodes.add(info.getUnitCode());
							}
						} else {
							unitCodes.add(info.getUnitCode());
						}
					}
				}
			}
		} else {
			if (childTypeCode == Type.GRADE.code) {
				getUnitInfoChilds(queryInfo, Type.CLASS.code, artSci, unitCodes, unShow);
			} else {
				if (queryInfo.getUnitTypeCode() == childTypeCode) {
					if (unShow || !queryInfo.isUnShow()) {
						if (childTypeCode == Type.CLASS.code) {
							if (Util.matchArtSci(queryInfo.getUnitCode(), artSci)) {
								unitCodes.add(queryInfo.getUnitCode());
							}
						} else {
							unitCodes.add(queryInfo.getUnitCode());
						}
					}
				} else {
					for (UnitQueryInfo unitQueryInfo : queryInfo.childs) {
						getUnitInfoChilds(unitQueryInfo, childTypeCode, artSci, unitCodes, unShow);
					}
				}
			}
		}
	}
	public String getParentCode() {
		return parentCode;
	}
//	public List<String> getParentCodes() {
//		List<String> rs = new ArrayList<String>();
//		UnitQueryInfo info = parent;
//		while (info != null) {
//			rs.add(info.getUnitCode());
//			info = info.parent;
//		}
//		return rs;
//	}
//	public List<UnitQueryInfo> getParents(String unitCode) {
//		List<UnitQueryInfo> rs = new ArrayList<UnitQueryInfo>();
//		UnitQueryInfo info = parent;
//		while (info != null) {
//			rs.add(info);
//			info = info.parent;
//		}
//		return rs;
//	}
	public Set<UnitQueryInfo> getTreeInfos() {
		Set<UnitQueryInfo> rs = new TreeSet<UnitQueryInfo>(new Comparator<UnitQueryInfo>() {
			@Override
			public int compare(UnitQueryInfo o1, UnitQueryInfo o2) {
				return o1.getUnitCode().compareTo(o2.getUnitCode());
			}
		});
		getTreeInfos(this, rs);
		return rs;
	}
	private void getTreeInfos(UnitQueryInfo info, Set<UnitQueryInfo> rs) {
		if (!info.isUnShow()) {
			rs.add(info);
		}
		for (UnitQueryInfo unitQueryInfo2 : info.childs) {
			getTreeInfos(unitQueryInfo2, rs);
		}
	}
	public void rmClassCourse() {
		if (unitTypeCode == Type.CLASS.code) {
			String as = unitCode.substring(12, 14);
			if ("01".equals(as)) {
				exams.remove(ExamType.MATH_SCIENCE);
				exams.removeAll(Arrays.asList(ExamTypeGroup.SCIENCE_MAJOR.exams));
				examGroups.remove(ExamTypeGroup.SCIENCE);
				examGroups.remove(ExamTypeGroup.SCIENCE_MAJOR);
			} else if ("02".equals(as)) {
				exams.remove(ExamType.MATH_ARTS);
				exams.removeAll(Arrays.asList(ExamTypeGroup.ARTS_MAJOR.exams));
				examGroups.remove(ExamTypeGroup.ARTS);
				examGroups.remove(ExamTypeGroup.ARTS_MAJOR);
			}
		} else {
			for (UnitQueryInfo child : childs) {
				child.rmClassCourse();
			}
		}
	}
	public void rmGradeNode() {
		rmGradeNode(this);
	}
	private void rmGradeNode(UnitQueryInfo info) {
		if (info.getUnitTypeCode() < Type.GRADE.code) {
			for (UnitQueryInfo unitQueryInfo : info.getChilds()) {
				rmGradeNode(unitQueryInfo);
			}
		} else if (info.getUnitTypeCode() == Type.GRADE.code) {
			UnitQueryInfo parent = Util.findUnitInfo(this, info.getParentCode());
			String parentCode = info.getParentCode();
			for (UnitQueryInfo unitQueryInfo : info.getChilds()) {
				unitQueryInfo.setParentCode(parentCode);
			}
			parent.setChilds(info.getChilds());
		}
	}
	public void addExamType(ExamType type) {
		this.exams.add(type);
		for (UnitQueryInfo child : childs) {
			child.addExamType(type);
		}
	}
	public void addExamGroupType(ExamTypeGroup type) {
		this.examGroups.add(type);
		for (UnitQueryInfo child : childs) {
			child.addExamGroupType(type);
		}
	}
	public void addExamType(Set<ExamType> types) {
		this.exams.addAll(types);
		for (UnitQueryInfo child : childs) {
			child.addExamType(types);
		}
	}
	public void addExamGroupType(Set<ExamTypeGroup> types) {
		this.examGroups.addAll(types);
		for (UnitQueryInfo child : childs) {
			child.addExamGroupType(types);
		}
	}
	public void rmExamType(ExamType type) {
		this.exams.remove(type);
		for (UnitQueryInfo child : childs) {
			child.rmExamType(type);
		}
	}
	public void rmExamGroupType(ExamTypeGroup type) {
		this.examGroups.remove(type);
		for (UnitQueryInfo child : childs) {
			child.rmExamGroupType(type);
		}
	}
	public void rmExamType() {
		this.exams.clear();
		for (UnitQueryInfo child : childs) {
			child.rmExamType();
		}
	}
	public void rmExamGroupType() {
		this.examGroups.clear();
		for (UnitQueryInfo child : childs) {
			child.rmExamGroupType();
		}
	}

	public String getUnitCode() {
		return unitCode;
	}
	public String getUnitName() {
		return unitName;
	}
	public String getUnitTypeName() {
		return unitTypeName;
	}
	public int getUnitTypeCode() {
		return unitTypeCode;
	}
	public Set<ExamType> getExams() {
		return exams;
	}
	public Set<ExamTypeGroup> getExamGroups() {
		return examGroups;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public void setUnitTypeName(String unitTypeName) {
		this.unitTypeName = unitTypeName;
	}
	public void setUnitTypeCode(int unitTypeCode) {
		this.unitTypeCode = unitTypeCode;
	}
	public void setExams(Set<ExamType> exams) {
		this.exams = exams;
	}
	public void setExamGroups(Set<ExamTypeGroup> examGroups) {
		this.examGroups = examGroups;
	}
	public void setUnShow(boolean unShow) {
		this.unShow = unShow;
		for (UnitQueryInfo child : childs) {
			child.setUnShow(unShow);
		}
	}
	public boolean isUnShow() {
		return unShow;
	}
	public boolean isShowStudent() {
		return showStudent;
	}
	public void setShowStudent(boolean showStudent) {
		this.showStudent = showStudent;
		for (UnitQueryInfo child : childs) {
			child.setShowStudent(showStudent);
		}
	}
	public boolean isCompare() {
		return compare;
	}
	public void setCompare(boolean compare) {
		this.compare = compare;
		for (UnitQueryInfo child : childs) {
			child.setCompare(compare);
		}
	}
	public Set<UnitQueryInfo> getChilds() {
		return childs;
	}
	public void setChilds(Set<UnitQueryInfo> childs) {
		this.childs = childs;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
}
