package com.lps.tqms.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.lps.tqms.db.pojo.ExamBatchInfo;
import com.lps.tqms.db.pojo.Subject;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;

public class UserAuthHelper {
	public Map<String, List<Role>> groupRoleByBatch(List<Role> roles, Map<String, List<String>> batches) {
		Map<String, List<Role>> rs = new HashMap<String, List<Role>>();
		for (Role role : roles) {
			if (role.getExam_code().equals("0")) {
				for (String batchCode : batches.get(role.getOrgi_code())) {
					if (!rs.containsKey(batchCode)) {
						rs.put(batchCode, new ArrayList<Role>());
					}
					rs.get(batchCode).add(new Role(role.getUser_id(), role.getOrgi_code(), batchCode, 
							role.getSub_code(), role.getMarking(), role.getShow_student(), role.getUn_compare()));
				}
			} else {
				String batchCode = role.getExam_code().substring(0, 14) + "00";
				if (!rs.containsKey(batchCode)) {
					rs.put(batchCode, new ArrayList<Role>());
				}
				rs.get(batchCode).add(role);
			}
		}
		return rs;
	}
	public ExamBatchInfo findExamBatchInfo(List<ExamBatchInfo> infos, String examCode) {
		ExamBatchInfo rs = new ExamBatchInfo();
		for (ExamBatchInfo examBatchInfo : infos) {
			if (examBatchInfo.getExamCode().equals(examCode)) {
				rs = examBatchInfo;
				break;
			}
		}
		return rs;
	}
	public UnitQueryInfo find(List<UnitQueryInfo> infos, String unitCode) {
		for (UnitQueryInfo unitQueryInfo : infos) {
			if (unitQueryInfo.getUnitCode().equals(unitCode)) {
				return unitQueryInfo;
			}
		}
		return null;
	}
	public UnitQueryInfo findTop(List<UnitQueryInfo> infos) {
		UnitQueryInfo rs = infos.size() > 0 ? infos.get(0) : null;
		while (rs != null) {
			UnitQueryInfo temp = find(infos, rs.getParentCode());
			if (temp == null) {
				break;
			} else {
				rs = temp;
			}
		}
		return rs;
	}
	public List<UnitQueryInfo> findChild(List<UnitQueryInfo> infos, String unitCode) {
		List<UnitQueryInfo> rs = new ArrayList<UnitQueryInfo>();
		for (UnitQueryInfo unitQueryInfo : infos) {
			if (unitQueryInfo.getParentCode().equals(unitCode)) {
				rs.add(unitQueryInfo);
			}
		}
		return rs;
	}
	public Set<ExamType> parseExamType(List<Subject> subjects) {
		Set<ExamType> rs = new TreeSet<ExamType>(new Comparator<ExamType>() {
			@Override
			public int compare(ExamType o1, ExamType o2) {
				return Integer.parseInt(o1.code) - Integer.parseInt(o2.code);
			}
		});
		for (Subject subject : subjects) {
			if (Integer.parseInt(subject.getId()) < 100) {
				rs.add(ExamType.get(subject.getId()));
			}
		}
		return rs;
	} 
	public Set<ExamTypeGroup> parseExamTypeGroup(List<Subject> subjects) {
		Set<ExamTypeGroup> rs = new TreeSet<ExamTypeGroup>(new Comparator<ExamTypeGroup>() {
			@Override
			public int compare(ExamTypeGroup o1, ExamTypeGroup o2) {
				return Integer.parseInt(o1.code) - Integer.parseInt(o2.code);
			}
		});
		for (Subject subject : subjects) {
			if (Integer.parseInt(subject.getId()) > 100) {
				rs.add(ExamTypeGroup.get(subject.getId()));
			}
		}
		return rs;
	} 
}
