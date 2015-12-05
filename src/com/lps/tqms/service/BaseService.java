package com.lps.tqms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.lps.tqms.cache.CacheUtil;
import com.lps.tqms.cache.Ka;
import com.lps.tqms.cache.NA;
import com.lps.tqms.dao.ifs.ExamDaoIF;
import com.lps.tqms.dao.ifs.ReviewDaoIF;
import com.lps.tqms.dao.ifs.StDaoIF;
import com.lps.tqms.db.pojo.ExamBatchInfo;
import com.lps.tqms.db.pojo.ExamCourseInfo;
import com.lps.tqms.db.pojo.ExamQuestionInfo;
import com.lps.tqms.db.pojo.ExamStudentInfo;
import com.lps.tqms.db.pojo.ExamUnitInfo;
import com.lps.tqms.db.pojo.Subject;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.pojo.UnitType.Type;
import com.lps.tqms.rs.CourseGroupResult;
import com.lps.tqms.rs.CourseResult;
import com.lps.tqms.rs.MarkingOnResult;
import com.lps.tqms.rs.MarkingOnTemp;
import com.lps.tqms.rs.Rank;
import com.lps.tqms.rs.RankScore;
import com.lps.tqms.rs.RankTemp;
import com.lps.tqms.rs.StudentGroupResult;
import com.lps.tqms.rs.StudentResult;
import com.lps.tqms.service.ifs.BaseServiceIF;
import com.lps.tqms.user.BatchAuth;
import com.lps.tqms.user.QueryInfo;
import com.lps.tqms.user.Role;
import com.lps.tqms.user.UnitQueryInfo;
import com.lps.tqms.user.User;
import com.lps.tqms.user.UserAuthHelper;
import com.lps.tqms.util.Util;

@IocBean
public class BaseService implements BaseServiceIF {
	@Inject
	private ExamDaoIF examDao;
	@Inject
	private StDaoIF stDao;
	@Inject
	private ReviewDaoIF reviewDao;
	
	@Override
	public String getImagePath() {
		return examDao.getImagePath();
	}
	@Override
	public String getImagePath2() {
		return examDao.getImagePath2();
	}
	@Override
	public List<ExamQuestionInfo> getExamQuestionInfos(String examCode,
			String name) {
		return examDao.getExamQuestionInfos(examCode, name);
	}
	@Override
	public ExamQuestionInfo getExamQuestionInfo(String examCode, String title) {
		return examDao.getExamQuestionInfo(examCode, title);
	}
	@Override
	public ExamStudentInfo getStudentInfo(String studentId, String examCode) {
		return examDao.getStudentInfo(studentId, examCode);
	}
	@Override
	public ExamStudentInfo getStudentInfoByExamNo(String examNo, String examCode) {
		return examDao.getStudentInfoByExamNo(examNo, examCode);
	}
	@Override
	public Record getExamQuestionScores(String examNo, String examCode) {
		return examDao.getExamQuestionScores(examNo, examCode);
	}
	
	@Override
	public CourseResult getCourse(String examCode, String unitCode, Set<String> unitCodes, int childType, QueryInfo queryInfo, String myUnitCode) {
		CacheUtil cacheUtil = CacheUtil.getInstance();
		ExamCourseInfo courseInfo = cacheUtil.getCourse(examCode);
		Ka ka = cacheUtil.getKa(examCode);
		CourseResult cr = new CourseResult(queryInfo.getUnitInfo(unitCode).getUnitName(), unitCode);
		cr.courseName = courseInfo.getExamName();
		cr.aNames = ka.getAbilityNames();
		cr.kNames = ka.getKnowledgeNames();
		
		Set<String> ccs = queryInfo.getUnitInfo(unitCode).getUnitCodeChilds(Type.CLASS.code, examCode, "");
		Record re = stDao.getCourse(examCode, ccs);
		cr.markLines = new ArrayList<Object>(re.values());
		Map<String, Object> points = cacheUtil.getKa(examCode).getPoints();
		points.put(courseInfo.getExamName(), courseInfo.getExamPoint());
		cr.points = points;
		String batchCode = examCode.substring(0, 14) + "00";
		ExamType examType = ExamType.get(examCode.substring(examCode.length() - 2));
		cr.students = examDao.getStudentCount(batchCode, examType.queryName, ccs);
		if (childType == Type.STUDENT.code) {
			List<Record> childs = stDao.getCourseStu(examCode, unitCodes);
			for (Record record : childs) {
				List<Object> child = new ArrayList<Object>();
				String classCode = record.getString("class_code");
				record.remove("class_code");
				child.addAll(record.values());
				UnitQueryInfo classUnit = queryInfo.getUnitInfo(classCode); 
				child.add(2, classUnit.getUnitName());
				child.add(3, queryInfo.getUnitInfo(queryInfo.getUnitInfo(classCode).getParentCode()).getUnitName());
				cr.childs.add(child);
			}
		} else if (childType == Type.CLASS.code) {
			List<Record> childs = stDao.getCourseCls(examCode, unitCodes);
			for (Record record : childs) {
				List<Object> child = new ArrayList<Object>(record.values());
				String classCode = record.getString("class_code");
				UnitQueryInfo classUnit = queryInfo.getUnitInfo(classCode); 
				child.add(1, classUnit.getUnitName());
				child.add(2, "");
				child.add(3, queryInfo.getUnitInfo(queryInfo.getUnitInfo(classCode).getParentCode()).getUnitName());
				if (record.getString("class_code").equals(myUnitCode)) {
					cr.childs.add(0, child);
				} else {
					cr.childs.add(child);
				}
			}
		} else {
			for (String uc : unitCodes) {
				Set<String> classCodes = queryInfo.getUnitInfo(uc).getUnitCodeChilds(Type.CLASS.code, examCode, "");
				if (classCodes.size() > 0) {
					Record record = stDao.getCourse(examCode, classCodes);
					List<Object> child = new ArrayList<Object>(record.values());
					child.add(0, uc);
					child.add(1, queryInfo.getUnitInfo(uc).getUnitName());
					child.add(2, "");
					child.add(3, "");
					if (uc.equals(myUnitCode)) {
						cr.childs.add(0, child);
					} else {
						cr.childs.add(child);
					}
				}
			}
		}
		return cr;
	}
	@Override
	public CourseGroupResult getCourseGroup(String batchCode, ExamTypeGroup group, String unitCode, 
			Set<String> unitCodes, int childType, QueryInfo queryInfo,  Set<ExamType> exams, String myUnitCode) {
		CourseGroupResult cgr = new CourseGroupResult(queryInfo.getUnitInfo(unitCode).getUnitName(), unitCode);
		cgr.courseName = group.name;
		cgr.cNames = group.getExamNames(exams);
		cgr.cCodes = group.getExamCodes(exams);
		Set<String> ccs = queryInfo.getUnitInfo(unitCode).getUnitCodeChilds(Type.CLASS.code, "", group.code);
		Record re = stDao.getCourseGroup(batchCode, group, ccs, exams);
		cgr.markLines = new ArrayList<Object>(re.values());
		CacheUtil cacheUtil = CacheUtil.getInstance();
		Map<String, Object> points = cacheUtil.getCoursePoints(batchCode, group, exams);
		float point = 0f;
		for (Map.Entry<String, Object> entry : points.entrySet()) {
			point += Float.valueOf(entry.getValue().toString());
		}
		points.put(group.name, point);
		cgr.points = points;
		cgr.students = examDao.getStudentCount(batchCode, group.queryName, ccs);
		if (childType == Type.STUDENT.code) {
			List<Record> childs = stDao.getCourseGroupStu(batchCode, group, unitCodes, exams);
			for (Record record : childs) {
				List<Object> child = new ArrayList<Object>();
				String classCode = record.getString("class_code");
				record.remove("class_code");
				child.addAll(record.values());
				UnitQueryInfo classUnit = queryInfo.getUnitInfo(classCode); 
				child.add(2, classUnit.getUnitName());
				child.add(3, queryInfo.getUnitInfo(queryInfo.getUnitInfo(classCode).getParentCode()).getUnitName());
				cgr.childs.add(child);
			}
		} else if (childType == Type.CLASS.code) {
			List<Record> childs = stDao.getCourseGroupCls(batchCode, group, unitCodes, exams);
			for (Record record : childs) {
				List<Object> child = new ArrayList<Object>(record.values());
				String classCode = record.getString("class_code");
				UnitQueryInfo classUnit = queryInfo.getUnitInfo(classCode); 
				child.add(1, classUnit.getUnitName());
				child.add(2, "");
				child.add(3, queryInfo.getUnitInfo(queryInfo.getUnitInfo(classCode).getParentCode()).getUnitName());
				if (record.getString("class_code").equals(myUnitCode)) {
					cgr.childs.add(0, child);
				} else {
					cgr.childs.add(child);
				}
			}
		} else {
			for (String uc : unitCodes) {
				Set<String> classCodes = queryInfo.getUnitInfo(uc).getUnitCodeChilds(Type.CLASS.code, "", group.code);
				if (classCodes.size() > 0) {
					Record record = stDao.getCourseGroup(batchCode, group, classCodes, exams);
					List<Object> child = new ArrayList<Object>(record.values());
					child.add(0, uc);
					child.add(1, queryInfo.getUnitInfo(uc).getUnitName());
					child.add(2, "");
					child.add(3, "");
					if (uc.equals(myUnitCode)) {
						cgr.childs.add(0, child);
					} else {
						cgr.childs.add(child);
					}
				}
			}
		}
		return cgr;
	}
	@Override
	public StudentResult getStudentCourse(String examCode, String studentId, QueryInfo queryInfo) {
		CacheUtil cacheUtil = CacheUtil.getInstance();
		ExamStudentInfo studentInfo = examDao.getStudentInfo(studentId, examCode.substring(0, 14) + "00");
		ExamCourseInfo courseInfo = cacheUtil.getCourse(examCode);
		Ka ka = cacheUtil.getKa(examCode);
		StudentResult rs = new StudentResult(studentInfo.getStudentName(), courseInfo.getExamName());
		rs.kNames = ka.getKnowledgeNames();
		rs.aNames = ka.getAbilityNames();
		List<Object> full = ka.getVals();
		full.add(0, "满分");
		rs.datas.add(full);
		Record record = stDao.getCourseStu(examCode, studentInfo.getExamNo());
		record.remove("score");
		List<Object> student = new ArrayList<Object>(record.values());
		student.add(0, studentInfo.getStudentName());
		rs.datas.add(student);
		UnitQueryInfo info = queryInfo.getUnitInfo(studentInfo.getClassCode());
		while (info != null) {
			Set<String> classCodes = info.getUnitCodeChilds(Type.CLASS.code, examCode, "");
			Record r = stDao.getCourse(examCode, classCodes);
			List<Object> vals = new ArrayList<Object>(r.values());
			vals.set(0, info.getUnitName());
			rs.datas.add(vals);
			info = queryInfo.getUnitInfo(info.getParentCode());
		}
		return rs;
	}
	@Override
	public StudentGroupResult getStudentCourseGroup(String batchCode, ExamTypeGroup group, 
			String studentId, QueryInfo queryInfo, Set<ExamType> exams) {
		CacheUtil cacheUtil = CacheUtil.getInstance();
		ExamStudentInfo studentInfo = examDao.getStudentInfo(studentId, batchCode);
		StudentGroupResult rs = new StudentGroupResult(studentInfo.getStudentName(), group.name);
		rs.cNames = group.getExamNames(exams);
		rs.cCodes = group.getExamCodes(exams);
		List<Object> full = cacheUtil.getCoursePoint(batchCode, group, exams);
		full.add(0, "满分");
		rs.datas.add(full);
		Record record = stDao.getCourseGroupStu(batchCode, group, studentInfo.getExamNo(), exams);
		record.remove("score");
		List<Object> student = new ArrayList<Object>(record.values());
		student.add(0, studentInfo.getStudentName());
		rs.datas.add(student);
		UnitQueryInfo info = queryInfo.getUnitInfo(studentInfo.getClassCode());
		while (info != null) {
			Set<String> classCodes = info.getUnitCodeChilds(Type.CLASS.code, "", group.code);
			Record r = stDao.getCourseGroup(batchCode, group, classCodes, exams);
			List<Object> vals = new ArrayList<Object>(r.values());
			vals.set(0, info.getUnitName());
			rs.datas.add(vals);
			info = queryInfo.getUnitInfo(info.getParentCode());
		}
		return rs;
	}
	@Override
	public CourseResult getCourseDirection(String examCode, String unitCode, User user) {
		CourseResult rs = new CourseResult();
		CacheUtil cacheUtil = CacheUtil.getInstance();
		String batchCode = examCode.substring(0, 14) + "00";
		String grade = cacheUtil.getBatch(batchCode).getGrade();
		BatchAuth auth = user.getBatchAuth(batchCode);
		QueryInfo queryInfo = auth.getQueryInfo();
		UnitQueryInfo unitQueryInfo = queryInfo.getUnitInfo(unitCode);
		rs.unitCode = unitCode;
		rs.unitName = unitQueryInfo.getUnitName();
		rs.courseName = cacheUtil.getCourse(examCode).getExamName();

		String eCode = examCode.substring(examCode.length() - 2);
		for (String bCode : user.getExamBatchCodes(grade)) {
			Ka ka = cacheUtil.getKa(bCode.substring(0, 14) + eCode);
			for (String aName : ka.getAbilityNames()) {
				if (!rs.aNames.contains(aName)) {
					rs.aNames.add(aName);
				}
			}
			for (String aName : ka.getKnowledgeNames()) {
				if (!rs.kNames.contains(aName)) {
					rs.kNames.add(aName);
				}
			}
		}
		Set<ExamBatchInfo> batchInfos = user.getExamBatches(grade);
		for (ExamBatchInfo batchInfo : batchInfos) {
			Set<String> classCodes = new TreeSet<String>();
			String bCode = batchInfo.getExamCode();
			String examCode2 = bCode.substring(0, 14) + eCode;
			queryInfo = user.getBatchAuth(bCode).getQueryInfo();
			unitQueryInfo = queryInfo.getUnitInfo(unitCode);
			if (unitQueryInfo != null) {
				classCodes = unitQueryInfo.getUnitCodeChilds(Type.CLASS.code, examCode2, "");
				if (classCodes.size() > 0) {
					Record record = stDao.getCourse(examCode2, classCodes);
					Ka ka = cacheUtil.getKa(examCode2);
					List<Object> vals = new ArrayList<Object>(record.values());
					List<String> all = new ArrayList<String>(rs.kNames);
					all.addAll(rs.aNames);
					List<Object> values = Util.buildValues(vals, all, ka.getNames());
					values.add(0, bCode);
					values.add(1, batchInfo.getExamName());
					values.add(2, "");
					values.add(3, "");
					values.add(4, vals.get(0));
					rs.childs.add(values);
				}
			}
		}
		return rs;
	}
	@Override
	public CourseGroupResult getCourseGroupDirection(String batchCode, ExamTypeGroup group, String unitCode, User user) {
		CourseGroupResult rs = new CourseGroupResult();
		BatchAuth auth = user.getBatchAuth(batchCode);
		QueryInfo queryInfo = auth.getQueryInfo();
		UnitQueryInfo unitQueryInfo = queryInfo.getUnitInfo(unitCode);
		rs.unitCode = unitCode;
		rs.unitName = unitQueryInfo.getUnitName();
		rs.courseName = group.name;
		rs.cNames = group.getExamNames(auth.exams);
		rs.cCodes = group.getExamCodes(auth.exams);
		String grade = CacheUtil.getInstance().getBatch(batchCode).getGrade();
		Set<ExamBatchInfo> batchInfos = user.getExamBatches(grade);
		for (ExamBatchInfo batchInfo : batchInfos) {
			Set<String> classCodes = new TreeSet<String>();
			String bCode = batchInfo.getExamCode();
			queryInfo = user.getBatchAuth(bCode).getQueryInfo();
			unitQueryInfo = queryInfo.getUnitInfo(unitCode);
			if (unitQueryInfo != null) {
				classCodes = unitQueryInfo.getUnitCodeChilds(Type.CLASS.code, "", group.code);
				if (classCodes.size()> 0) {
					Record record = stDao.getCourseGroup(bCode, group, classCodes, auth.exams);
					List<Object> values = new ArrayList<Object>(record.values());
					values.add(0, bCode);
					values.add(1, batchInfo.getExamName());
					values.add(2, "");
					values.add(3, "");
					rs.childs.add(values);
				}
			}
		}
		return rs;
	}
	@Override
	public CourseResult getStudentCourseDirection(String examCode, String studentId, User user) {
		CourseResult rs = new CourseResult();
		CacheUtil cacheUtil = CacheUtil.getInstance();
		String batchCode = examCode.substring(0, 14) + "00";
		String grade = cacheUtil.getBatch(batchCode).getGrade();
		ExamStudentInfo studentInfo = examDao.getStudentInfo(studentId, batchCode);
		rs.unitCode = studentId;
		rs.unitName = studentInfo.getStudentName();
		rs.courseName = cacheUtil.getCourse(examCode).getExamName();
		String eCode = examCode.substring(examCode.length() - 2);
		for (String bCode : user.getExamBatchCodes(grade)) {
			Ka ka = cacheUtil.getKa(bCode.substring(0, 14) + eCode);
			for (String aName : ka.getAbilityNames()) {
				if (!rs.aNames.contains(aName)) {
					rs.aNames.add(aName);
				}
			}
			for (String aName : ka.getKnowledgeNames()) {
				if (!rs.kNames.contains(aName)) {
					rs.kNames.add(aName);
				}
			}
		}
		Set<ExamBatchInfo> batchInfos = user.getExamBatches(grade);
		for (ExamBatchInfo batchInfo : batchInfos) {
			String bCode = batchInfo.getExamCode();
			String examCode2 = bCode.substring(0, 14) + eCode;
			studentInfo = examDao.getStudentInfo(studentId, bCode);
			if (studentInfo != null) {
				Record record = stDao.getCourseStu(examCode2, studentInfo.getExamNo());
				Ka ka = cacheUtil.getKa(examCode2);
				List<Object> vals = new ArrayList<Object>(record.values());
				List<String> all = new ArrayList<String>(rs.kNames);
				all.addAll(rs.aNames);
				List<Object> values = Util.buildValues(vals, all, ka.getNames());
				values.add(0, bCode);
				values.add(1, batchInfo.getExamName());
				values.add(2, "");
				values.add(3, "");
				values.add(4, vals.get(0));
				rs.childs.add(values);
			}
		}
		return rs;
	}
	@Override
	public CourseGroupResult getStudentCourseGroupDirection(String batchCode,
			ExamTypeGroup group, String studentId, User user) {
		BatchAuth auth = user.getBatchAuth(batchCode);
		CourseGroupResult rs = new CourseGroupResult();
		ExamStudentInfo studentInfo = examDao.getStudentInfo(studentId, batchCode);
		rs.unitCode = studentId;
		rs.unitName = studentInfo.getStudentName();
		rs.courseName = group.name;
		rs.cNames = group.getExamNames(auth.exams);
		rs.cCodes = group.getExamCodes(auth.exams);
		String grade = CacheUtil.getInstance().getBatch(batchCode).getGrade();
		Set<ExamBatchInfo> batchInfos = user.getExamBatches(grade);
		for (ExamBatchInfo batchInfo : batchInfos) {
			String bCode = batchInfo.getExamCode();
			studentInfo = examDao.getStudentInfo(studentId, bCode);
			if (studentInfo != null) {
				Record record = stDao.getCourseGroupStu(bCode, group, studentInfo.getExamNo(), auth.exams);
				List<Object> values = new ArrayList<Object>(record.values());
				values.add(0, bCode);
				values.add(1, batchInfo.getExamName());
				values.add(2, "");
				values.add(3, "");
				rs.childs.add(values);
			}
		}
		return rs;
	}
	@Override
	public Rank getRank(String examCode, NA na, String unitCode, int type, User user) {
		Rank rs = new Rank();
		CacheUtil cacheUtil = CacheUtil.getInstance();
		String batchCode = examCode.substring(0, 14) + "00";
		String eCode = examCode.substring(examCode.length() - 2);
		rs.unitCode = unitCode;
		BatchAuth batchAuth = user.getBatchAuth(batchCode);
		QueryInfo queryInfo = batchAuth.getQueryInfo();
		if (type == 0) {
			UnitQueryInfo unitQueryInfo = queryInfo.getUnitInfo(unitCode);
			type = unitQueryInfo.getUnitTypeCode();
		}
		rs.examName = cacheUtil.getCourse(batchCode.substring(0, 14) + eCode).getExamName();
		rs.name = na.name;
		String grade = batchAuth.getGrade();
		if (type == Type.STUDENT.code) {
			ExamStudentInfo studentInfo = examDao.getStudentInfo(unitCode, batchCode);
			rs.unitName = studentInfo.getStudentName();
			for (BatchAuth auth : user.getAuths(grade)) {
				batchCode = auth.getExamCode();
				examCode = batchCode.substring(0, 14) + eCode;
				studentInfo = examDao.getStudentInfo(unitCode, batchCode);
				if (studentInfo != null) {
					String table = String.format("t_%s_ka", examCode);
					if (examDao.existTable(table) && 
							(na == null || "".equals(na.code) || examDao.existCol(table, na.code))) {
						RankTemp temp = new RankTemp();
						temp.batchName = auth.getExamName();
						QueryInfo info = new QueryInfo();
						String queryId = queryInfo.getQueryId();
						info.setQueryId(queryId);
						if ("".equals(queryId)) {
							info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode));
//							info.setUnitQueryInfo(examDao.getUnitInfo(batchCode));
						} else {
							info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode, queryId));
//							info.setUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode));
						}
						RankScore score = stDao.getRankCourseStu(examCode, na.code, studentInfo.getExamNo());
						UnitQueryInfo unitQueryInfo = info.getUnitInfo(studentInfo.getClassCode());
						while (unitQueryInfo != null) {
							if (unitQueryInfo.getUnitTypeCode() != Type.GRADE.code) {
								Set<String> classCodes = unitQueryInfo.getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(eCode), true);
								List<RankScore> scores = stDao.getRankCourseStu(examCode, na.code, classCodes);
								List<Object> unit = new ArrayList<Object>();
								unit.add(unitQueryInfo.getUnitCode());
								unit.add(unitQueryInfo.getUnitName());
								Util.buildRank(score, scores, unit);
								temp.units.add(unit);
							}
							unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
						}
						if (temp.units.size() > 0)
							rs.batches.add(temp);
					}
				}
			}
		} else {
			UnitQueryInfo unitQueryInfo = queryInfo.getUnitInfo(unitCode);
			rs.unitName = unitQueryInfo.getUnitName();
			if (type == Type.CLASS.code) {
				for (BatchAuth auth : user.getAuths(grade)) {
					if (auth.getQueryInfo(queryInfo.getQueryId()).getUnitInfo(unitCode) != null) {
						batchCode = auth.getExamCode();
						examCode = batchCode.substring(0, 14) + eCode;
						String table = String.format("t_%s_ka", examCode);
						if (examDao.existTable(table) && 
								(na == null || "".equals(na.code) || examDao.existCol(table, na.code))) {
							RankTemp temp = new RankTemp();
							temp.batchName = auth.getExamName();
							QueryInfo info = new QueryInfo();
							String queryId = queryInfo.getQueryId();
							info.setQueryId(queryId);
							if ("".equals(queryId)) {
//								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode));
								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(batchCode)));
							} else {
//								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode, queryId));
								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode)));
							}
							Set<String> classCodes = new TreeSet<String>();
							classCodes.add(unitCode);
							unitQueryInfo = info.getUnitInfo(unitCode);
							RankScore score = stDao.getRankCourse(examCode, na.code, classCodes);
							unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							while (unitQueryInfo != null) {
								if (unitQueryInfo.getUnitTypeCode() != Type.GRADE.code) {
									classCodes = unitQueryInfo.getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(eCode), true);
									List<RankScore> scores = stDao.getRankCourseCls(examCode, na.code, classCodes);
									List<Object> unit = new ArrayList<Object>();
									unit.add(unitQueryInfo.getUnitCode());
									unit.add(unitQueryInfo.getUnitName());
									Util.buildRank(score, scores, unit);
									temp.units.add(unit);
								}
								unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							}
							if (temp.units.size() > 0)
								rs.batches.add(temp); 
						}
					}
				}
			} else {
				for (BatchAuth auth : user.getAuths(grade)) {
					if (auth.getQueryInfo(queryInfo.getQueryId()).getUnitInfo(unitCode) != null) {
						batchCode = auth.getExamCode();
						examCode = batchCode.substring(0, 14) + eCode;
						String table = String.format("t_%s_ka", examCode);
						if (examDao.existTable(table) && 
							(na == null || "".equals(na.code) || examDao.existCol(table, na.code))) {
							RankTemp temp = new RankTemp();
							temp.batchName = auth.getExamName();
							QueryInfo info = new QueryInfo();
							String queryId = queryInfo.getQueryId();
							info.setQueryId(queryId);
							if ("".equals(queryId)) {
//								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode));
								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(batchCode)));
							} else {
//								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode, queryId));
								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode)));
							}
							unitQueryInfo = info.getUnitInfo(unitCode);
							Set<String> classCodes = unitQueryInfo.getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(eCode), true);
							RankScore score = stDao.getRankCourse(examCode, na.code, classCodes);
							unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							while (unitQueryInfo != null) {
								if (unitQueryInfo.getUnitTypeCode() != Type.GRADE.code) {
									List<RankScore> scores = new ArrayList<RankScore>();
									Set<String> unitCodes = unitQueryInfo.getUnitInfoChilds(type, Util.parseArtSci(eCode), true);
									for (String uc : unitCodes) {
										classCodes = info.getUnitInfo(uc).getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(eCode), true);
										scores.add(stDao.getRankCourse(examCode, na.code, classCodes));
									}
									List<Object> unit = new ArrayList<Object>();
									unit.add(unitQueryInfo.getUnitCode());
									unit.add(unitQueryInfo.getUnitName());
									Util.buildRank(score, scores, unit);
									temp.units.add(unit);
								}
								unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							}
							if (temp.units.size() > 0)
								rs.batches.add(temp); 
						}
					}
				}
			}
		} 
		return rs;
	}
	@Override
	public Rank getRankGroup(String batchCode, ExamTypeGroup group, String unitCode, int type, User user) {
		Rank rs = new Rank();
		CacheUtil cacheUtil = CacheUtil.getInstance();
		rs.unitCode = unitCode;
		BatchAuth batchAuth = user.getBatchAuth(batchCode);
		QueryInfo queryInfo = batchAuth.getQueryInfo();
		if (type == 0) {
			UnitQueryInfo unitQueryInfo = queryInfo.getUnitInfo(unitCode);
			type = unitQueryInfo.getUnitTypeCode();
		}
		rs.examName = group.name;
		String grade = batchAuth.getGrade();
		if (type == Type.STUDENT.code) {
			ExamStudentInfo studentInfo = examDao.getStudentInfo(unitCode, batchCode);
			rs.unitName = studentInfo.getStudentName();
			for (BatchAuth auth : user.getAuths(grade)) {
				batchCode = auth.getExamCode();
				studentInfo = examDao.getStudentInfo(unitCode, batchCode);
				if (studentInfo != null) {
					String table = String.format("t_%s", batchCode);
					if (examDao.existTable(table) && examDao.existCol(table, group.queryName)) {
						RankTemp temp = new RankTemp();
						temp.batchName = auth.getExamName();
						QueryInfo info = new QueryInfo();
						String queryId = queryInfo.getQueryId();
						info.setQueryId(queryId);
						if ("".equals(queryId)) {
							info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode));
//							info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(batchCode)));
						} else {
							info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode, queryId));
//							info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode)));
						}
						RankScore score = stDao.getRankCourseGroupStu(batchCode, group, studentInfo.getExamNo());
						UnitQueryInfo unitQueryInfo = info.getUnitInfo(studentInfo.getClassCode());
						while (unitQueryInfo != null) {
							if (unitQueryInfo.getUnitTypeCode() != Type.GRADE.code) {
								Set<String> classCodes = unitQueryInfo.getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(group), true);
								List<RankScore> scores = stDao.getRankCourseGroupStu(batchCode, group, classCodes);
								List<Object> unit = new ArrayList<Object>();
								unit.add(unitQueryInfo.getUnitCode());
								unit.add(unitQueryInfo.getUnitName());
								Util.buildRank(score, scores, unit);
								temp.units.add(unit);
							}
							unitQueryInfo = info.getUnitInfo(unitQueryInfo.getParentCode());
						}
						if (temp.units.size() > 0)
							rs.batches.add(temp); 
					}
				}
			}
		} else {
			UnitQueryInfo unitQueryInfo = queryInfo.getUnitInfo(unitCode);
			rs.unitName = unitQueryInfo.getUnitName();
			if (type == Type.CLASS.code) {
				for (BatchAuth auth : user.getAuths(grade)) {
					if (auth.getQueryInfo(queryInfo.getQueryId()).getUnitInfo(unitCode) != null) {
						batchCode = auth.getExamCode();
						String table = String.format("t_%s", batchCode);
						if (examDao.existTable(table) && examDao.existCol(table, group.queryName)) {
							RankTemp temp = new RankTemp();
							temp.batchName = auth.getExamName();
							QueryInfo info = new QueryInfo();
							String queryId = queryInfo.getQueryId();
							info.setQueryId(queryId);
							if ("".equals(queryId)) {
								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode));
//								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(batchCode)));
							} else {
								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode, queryId));
								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode)));
							}
							Set<String> classCodes = new TreeSet<String>();
							classCodes.add(unitCode);
							unitQueryInfo = info.getUnitInfo(unitCode);
							RankScore score = stDao.getRankCourseGroup(batchCode, group, classCodes);
							unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							while (unitQueryInfo != null) {
								if (unitQueryInfo.getUnitTypeCode() != Type.GRADE.code) {
									classCodes = unitQueryInfo.getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(group), true);
									List<RankScore> scores = stDao.getRankCourseGroupCls(batchCode, group, classCodes);
									List<Object> unit = new ArrayList<Object>();
									unit.add(unitQueryInfo.getUnitCode());
									unit.add(unitQueryInfo.getUnitName());
									Util.buildRank(score, scores, unit);
									temp.units.add(unit);
								}
								unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							}
							if (temp.units.size() > 0)
								rs.batches.add(temp);  
						}
					}
				}
			} else {
				for (BatchAuth auth : user.getAuths(grade)) {
					if (auth.getQueryInfo(queryInfo.getQueryId()).getUnitInfo(unitCode) != null) {
						batchCode = auth.getExamCode();
						String table = String.format("t_%s", batchCode);
						if (examDao.existTable(table) && examDao.existCol(table, group.queryName)) {
							RankTemp temp = new RankTemp();
							temp.batchName = auth.getExamName();
							QueryInfo info = new QueryInfo();
							String queryId = queryInfo.getQueryId();
							info.setQueryId(queryId);
							if ("".equals(queryId)) {
								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode));
//								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(batchCode)));
							} else {
								info.setUnitQueryInfo(cacheUtil.getUnitQueryInfo(batchCode, queryId));
//								info.setUnitQueryInfo(Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode)));
							}
							unitQueryInfo = info.getUnitInfo(unitCode);
							Set<String> classCodes = unitQueryInfo.getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(group), true);
							RankScore score = stDao.getRankCourseGroup(batchCode, group, classCodes);
							unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							while (unitQueryInfo != null) {
								if (unitQueryInfo.getUnitTypeCode() != Type.GRADE.code) {
									List<RankScore> scores = new ArrayList<RankScore>();
									Set<String> unitCodes = unitQueryInfo.getUnitInfoChilds(type, Util.parseArtSci(group), true);
									for (String uc : unitCodes) {
										classCodes = info.getUnitInfo(uc).getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(group), true);
										scores.add(stDao.getRankCourseGroup(batchCode, group, classCodes));
									}
									List<Object> unit = new ArrayList<Object>();
									unit.add(unitQueryInfo.getUnitCode());
									unit.add(unitQueryInfo.getUnitName());
									Util.buildRank(score, scores, unit);
									temp.units.add(unit);
								}
								unitQueryInfo = queryInfo.getUnitInfo(unitQueryInfo.getParentCode());
							}
							if (temp.units.size() > 0)
								rs.batches.add(temp); 
						}
					}
				}
			}
		} 
		return rs;
	}
	
	@Override
	public User getUser(String userName, String unitCode) {
		return examDao.getUser(userName, unitCode);
	}
	@Override
	public User getUserWithAuth(String userName, String unitCode) {
		User user = examDao.getUser(userName, unitCode);
		user.setModules(examDao.getModules(user.getId()));
		checkAuth(user);
		return user;
	}
	@Override
	public User getUser(String userName) {
		return examDao.getUser(userName);
	}
	@Override
	public User getUserWithAuth(String userName) {
		User user = examDao.getUser(userName);
		user.setModules(examDao.getModules(user.getId()));
		checkAuth(user);
		return user;
	}
	private void checkAuth(User user) {
		CacheUtil cacheUtil = CacheUtil.getInstance();
		UserAuthHelper helper = new UserAuthHelper();
		List<String> units = examDao.getUnits(user.getId());
		Map<String, List<String>> batchs = new HashMap<String, List<String>>();
		Set<String> batchSet = new HashSet<String>();
		for (String unit : units) {
			List<String> bs = examDao.getExamBatchs(unit);
			batchSet.addAll(bs);
			batchs.put(unit, bs);
		}
		List<ExamBatchInfo> batchInfos = examDao.getExamBatchInfos(new ArrayList<String>(batchSet));
		List<Role> baseRoles = examDao.getRoles(user.getId(), 0);
		baseRoles.addAll(examDao.getRoles(user.getId(), 2));
		Map<String, List<Role>> base = helper.groupRoleByBatch(baseRoles, batchs);
		Map<String, List<Role>> forbid = helper.groupRoleByBatch(examDao.getRoles(user.getId(), 1), batchs);
		for (Map.Entry<String, List<Role>> entry : base.entrySet()) {
			String batchCode = entry.getKey();
			List<Subject> subjects = examDao.getSubjects(batchCode);
			Set<ExamType> exams = helper.parseExamType(subjects);
			Set<ExamTypeGroup> groups = helper.parseExamTypeGroup(subjects);
			ExamBatchInfo batchInfo = helper.findExamBatchInfo(batchInfos, batchCode);
			BatchAuth auth = new BatchAuth(batchCode, batchInfo.getExamName(), batchInfo.getGrade(), batchInfo.getExamDate());
			auth.exams.addAll(exams);
			auth.groups.addAll(groups);
			List<String> queryIds = examDao.getQueryIds(user.getId(), batchCode);
			UnitQueryInfo temp;
			int i = 0;
			String queryId = "";
			if (queryIds.size() > 0) {
				queryId = queryIds.get(i);
				temp = cacheUtil.getUnitQueryInfo(batchCode, queryId);
//				temp = Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode));
			} else {
				temp = cacheUtil.getUnitQueryInfo(batchCode);
//				temp = Util.transListToUnitQueryInfo(examDao.getUnitInfo(batchCode));
			}
			do {
				if (i > 0) {
					queryId = queryIds.get(i);
					temp = cacheUtil.getUnitQueryInfo(batchCode, queryId);
//					temp = Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode));
				}
				QueryInfo qi = new QueryInfo("", temp);
				UnitQueryInfo unitQueryInfo = Util.findTop(temp, entry.getValue());
				if (unitQueryInfo != null) {
					UnitQueryInfo parent = qi.getUnitInfo(unitQueryInfo.getParentCode());
					if (parent != null) {
						if (parent.getUnitTypeCode() == Type.GRADE.code) {
							parent = qi.getUnitInfo(parent.getParentCode());
							parent.setUnShow(false);
						} else if (parent.getUnitTypeCode() == Type.SCHOOL.code) {
							parent.setUnShow(false);
						}
						unitQueryInfo = parent.clone();
					} else {
						unitQueryInfo = unitQueryInfo.clone();
					}
//					unitQueryInfo.addExamType(exams);
//					unitQueryInfo.addExamGroupType(groups);
					for (Role role : entry.getValue()) {
						UnitQueryInfo aa = unitQueryInfo.getUnitInfo(role.getOrgi_code());
						if (aa != null) {
							if (role.getSub_code().equals("0")) {
								aa.addExamType(exams);
								aa.addExamGroupType(groups);
							} else {
								String code = role.getSub_code();
								if (cacheUtil.getSubjectCache().getByCode(code).getReal() == 1) {
									if (exams.contains(ExamType.get(code))) {
										aa.addExamType(ExamType.get(code));
									}
								} else {
									if (groups.contains(ExamTypeGroup.get(code))) {
										aa.addExamGroupType(ExamTypeGroup.get(code));
									}
								}
							}
							aa.setShowStudent(role.getShow_student() == 1);
							aa.setCompare(role.getUn_compare() == 0);
							aa.setUnShow(false);
						}
					}
					List<Role> forbids = forbid.get(batchCode);
					if (forbids != null) {
						for (Role role : forbids) {
							UnitQueryInfo aa = unitQueryInfo.getUnitInfo(role.getOrgi_code());
							if (aa != null) {
								if (role.getSub_code().equals("0")) {
									aa.rmExamType();
									aa.rmExamGroupType();
									aa.setUnShow(true);
								} else {
									String code = role.getSub_code();
									if (cacheUtil.getSubjectCache().getByCode(code).getReal() == 1) {
										aa.rmExamType(ExamType.get(code));
									} else {
										aa.rmExamGroupType(ExamTypeGroup.get(code));
									}
									if (aa.getExams().size() == 0 && aa.getExamGroups().size() == 0) {
										aa.setUnShow(true);
									}
								}
							}
						}
					}
					unitQueryInfo.rmClassCourse();
//					unitQueryInfo.rmGradeNode();
					QueryInfo queryInfo = new QueryInfo(queryId, unitQueryInfo);
					queryInfo.buildUnitTypes();
					auth.getInfos().add(queryInfo);
				}
				i++;
			} while (i < queryIds.size());
			if (auth.getInfos().size() > 0) {
				user.getAuths().add(auth);
			}
		}
	}
	@Override
	public boolean updateUserPwd(String id, String pwd) {
		return examDao.updateUserPwd(id, pwd);
	}
	@Override
	public List<ExamUnitInfo> getUnitInfos() {
		return examDao.getUnitInfos();
	}
	@Override
	public List<Record> getDifficulty(List<String> unitCodes, String examCode) {
		return reviewDao.getDifficulty(unitCodes, examCode);
	}
	@Override
	public List<Record> getQuestionScore(List<String> unitCodes, String examCode) {
		return reviewDao.getQuestionScore(unitCodes, examCode);
	}
	@Override
	public List<Record> getStuQuestionScore(List<String> unitCodes,
			String examCode, String questionTitle) {
		return reviewDao.getStuQuestionScore(unitCodes, examCode, questionTitle);
	}
	@Override
	public MarkingOnResult getMarkingOn(QueryInfo queryInfo, String type, String value, String unitCode, int childTypeCode, 
			String batchCode, String examName) {
		MarkingOnResult rs = new MarkingOnResult();
		CacheUtil cacheUtil = CacheUtil.getInstance();
		String[] values = value.split(",");
		int len = values.length;
		double[] scores = new double[len];
		com.lps.tqms.db.pojo.Subject subject = cacheUtil.getSubjectCache().getByName(examName);
		String queryName = subject.getQueryName();
		UnitQueryInfo unitQueryInfo = queryInfo.getUnitInfo(unitCode);
		Set<String> classCodes = unitQueryInfo.getUnitInfoChilds(Type.CLASS.code, subject.getArtSci(), true);
		String[] names = new String[len + 1];
		if ("score".equals(type)) {
			for (int i = 0; i < len; i++) {
				scores[len-i-1] = Float.valueOf(values[i]);
				if (i == 0) {
					names[len-i] = values[i] + "分以下";
				} else {
					names[len-i] = values[i-1] + "-" + values[i] + "分";
				}
			}
			names[0] = values[len - 1] + "分以上";
		} else if ("person".equals(type)) {
			for (int i = 0; i < len; i++) {
				scores[i] = examDao.getScore(batchCode, queryName, Integer.valueOf(values[i]), classCodes) / 100;
				if (i == 0) {
					names[i] = "前" + values[i] + "人";
				} else {
					names[i] = values[i-1] + "-" + values[i] + "人";
				}
			}
			names[len] = "其他";
		} else if ("personPercent".equals(type)) {
			int studentCount = examDao.getStudentCount(batchCode, queryName, classCodes);
			for (int i = 0; i < len; i++) {
				int amount = (int) (studentCount * 1.0 * Float.valueOf(values[i]) / 100);
				scores[i] = examDao.getScore(batchCode, queryName, amount, classCodes) / 100;
				if (i == 0) {
					names[i] = "前" + values[i] + "%人";
				} else {
					names[i] = values[i-1] + "-" + values[i] + "%人";
				}
			}
			names[len] = "后" + (100 - Float.valueOf(values[len-1])) + "%人";
		}
		rs.unitName = unitQueryInfo.getUnitName();
		int[] students = new int[len + 1];
		for (int i = 0; i < len; i++) {
			if (i == 0) {
				int s = examDao.getStudentCount(batchCode, queryName, classCodes, scores[i] * 100);
				students[i] = s;
				rs.students.put(names[i], s);
			} else {
				int s = examDao.getStudentCount(batchCode, queryName, classCodes, scores[i] * 100, scores[i-1] * 100);
				students[i] = s;
				rs.students.put(names[i], s);
			}
		}
		int s = examDao.getStudentCount(batchCode, queryName, classCodes, 1, scores[len-1] * 100);
		students[len] = s;
		rs.students.put(names[len], s);
		Set<String> childCodes = unitQueryInfo.getUnitInfoChilds(childTypeCode, subject.getArtSci(), true);
		for (String code : childCodes) {
			UnitQueryInfo info = queryInfo.getUnitInfo(code);
			Set<String> cCodes = info.getUnitInfoChilds(Type.CLASS.code, subject.getArtSci(), true);
			int studentCount = examDao.getStudentCount(batchCode, queryName, cCodes);
			List<MarkingOnTemp> temps = new ArrayList<MarkingOnTemp>();
			for (int i = 0; i < len + 1; i++) {
				MarkingOnTemp temp = new MarkingOnTemp();
				temp.name = names[i];
				int amount = 0;
				if (i == 0) {
					amount = examDao.getStudentCount(batchCode, queryName, cCodes, scores[i] * 100);
					temp.score = scores[i];
				} else if (i == len) {
					temp.score = 0;
					amount = examDao.getStudentCount(batchCode, queryName, cCodes, 1, scores[i-1] * 100);
				} else {
					temp.score = scores[i];
					amount = examDao.getStudentCount(batchCode, queryName, cCodes, scores[i] * 100, scores[i-1] * 100);
				}
				temp.amount = amount;
				temp.scale = students[i] == 0 ? 0 : Util.fixed(amount * 1.0 / students[i] * 100);
				temp.unitScale = studentCount == 0 ? 0 : Util.fixed(amount * 1.0 / studentCount * 100);
				temps.add(temp);
			}
			String name = info.getUnitCode() + "-" + info.getUnitName();
			if (info.getUnitTypeCode() == Type.CLASS.code && unitQueryInfo.getUnitTypeCode() != Type.SCHOOL.code) {
				name += "-" + queryInfo.getUnitInfo(info.getParentCode()).getUnitName();
			}
			rs.childs.put(name, temps);
		}
		return rs;
	}
}
