package com.lps.tqms.user;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.lps.tqms.db.pojo.ExamBatchInfo;

public class User {
	private String id;
	private String name;
	private String idName;
	private String loginName;
	private String pwd;
	private String unitCode;
	private List<BatchAuth> auths = new ArrayList<BatchAuth>();
	private List<Module> modules = new ArrayList<Module>();
	
	public User() {
	}
	public User(String id, String name, String idName, String loginName,
			String pwd, String unitCode) {
		super();
		this.id = id;
		this.name = name;
		this.idName = idName;
		this.loginName = loginName;
		this.pwd = pwd;
		this.unitCode = unitCode;
	}
	public Set<ExamBatchInfo> getExamBatches(String grade, boolean desc) {
		Set<ExamBatchInfo> examBatches;
		if (desc) {
			examBatches = new TreeSet<ExamBatchInfo>(new Comparator<ExamBatchInfo>() {
				@Override
				public int compare(ExamBatchInfo b, ExamBatchInfo a) {
					int result = a.getExamDate().compareTo(b.getExamDate());
					if (result == 0){
						result = a.getExamCode().compareTo(b.getExamCode());
						if (result ==0){
							result = a.getId() - b.getId();
						}
					}
					return  result;
				}
			});
		} else {
			examBatches = new TreeSet<ExamBatchInfo>(new Comparator<ExamBatchInfo>() {
				@Override
				public int compare(ExamBatchInfo a, ExamBatchInfo b) {
					int result = a.getExamDate().compareTo(b.getExamDate());
					if (result == 0){
						result = a.getExamCode().compareTo(b.getExamCode());
						if (result ==0){
							result = a.getId() - b.getId();
						}
					}
					return  result;
				}
			});
		}
		for (BatchAuth auth : auths) {
			if (grade == null || "".equals(grade) || grade.equals(auth.getGrade())) {
				ExamBatchInfo info = new ExamBatchInfo();
				info.setExamCode(auth.getExamCode());
				info.setExamName(auth.getExamName());
				info.setExamDate(auth.getExamDate());
				info.setGrade(auth.getGrade());
				examBatches.add(info);
			}
		}
		return examBatches;
	}
	public Set<ExamBatchInfo> getExamBatches(String grade) { 
		return getExamBatches(grade, false);
	}
	public Map<String, ExamBatchInfo> getExamBatchMaps() {
		Map<String, ExamBatchInfo> examBatches = new HashMap<String, ExamBatchInfo>();
		for (BatchAuth auth : auths) {
			ExamBatchInfo info = new ExamBatchInfo();
			info.setExamCode(auth.getExamCode());
			info.setExamName(auth.getExamName());
			info.setExamDate(auth.getExamDate());
			info.setGrade(auth.getGrade());
			examBatches.put(auth.getExamCode(), info);
		}
		return examBatches;
	}
	public List<String> getExamBatchCodes(String grade) {
		List<String> rs = new ArrayList<String>();
		for (BatchAuth auth : auths) {
			if (grade == null || "".equals(grade) || auth.getGrade().equals(grade)) {
				rs.add(auth.getExamCode());
			}
		}
		return rs;
	}
	public BatchAuth getBatchAuth(String examBatch) {
		for (BatchAuth auth : auths) {
			if (auth.getExamCode().equals(examBatch)) {
				return auth;
			}
		}
		return null;
	}
	public List<BatchAuth> getAuths(String grade) {
		List<BatchAuth> rs = new ArrayList<BatchAuth>();
		for (BatchAuth auth : auths) {
			if (auth.getGrade().equals(grade)) {
				rs.add(auth);
			}
		}
		return rs;
	}
	public String getName() {
		return name;
	}
	public String getPwd() {
		return pwd;
	}
	public String getUnitCode() {
		return unitCode;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public List<BatchAuth> getAuths() {
		return auths;
	}
	public void setAuths(List<BatchAuth> auths) {
		this.auths = auths;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public List<Module> getModules() {
		return modules;
	}
	public void setModules(List<Module> modules) {
		this.modules = modules;
	}
	public String getIdName() {
		return idName;
	}
	public void setIdName(String idName) {
		this.idName = idName;
	}
}
