package com.lps.tqms.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.entity.Record;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;

import com.lps.tqms.cache.CacheUtil;
import com.lps.tqms.cache.NA;
import com.lps.tqms.db.pojo.ExamBatchInfo;
import com.lps.tqms.db.pojo.ExamQuestionInfo;
import com.lps.tqms.db.pojo.ExamStudentInfo;
import com.lps.tqms.filter.LoginFilter;
import com.lps.tqms.filter.LoginJsonFilter;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.pojo.UnitType;
import com.lps.tqms.pojo.UnitType.Type;
import com.lps.tqms.service.ifs.BaseServiceIF;
import com.lps.tqms.user.BatchAuth;
import com.lps.tqms.user.QueryInfo;
import com.lps.tqms.user.UnitQueryInfo;
import com.lps.tqms.user.User;
import com.lps.tqms.util.SessionHelper;
import com.lps.tqms.util.Util;

@IocBean
@InjectName
@At("/")
@Fail("json")
public class ChartModule {
	@Inject
	BaseServiceIF baseService;
	
	@At("examBatch/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object getExamBatch(HttpServletRequest req) throws Exception {
		User user = SessionHelper.getUser(req);
		Map<String, Object> rs = new HashMap<String, Object>();
		Set<ExamBatchInfo> data = user.getExamBatches(null, true);
		if (data.size() > 0) {
			rs.put("status", true);
			rs.put("data", data);
		} else {
			rs.put("status", false);
			rs.put("msg", "用户权限错误");
		}
		return rs;
	}
	
	@At("unitType/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object getUnitTypeInfo(String unitCode, String examBatchCode, HttpServletRequest req) throws Exception {
		User user = SessionHelper.getUser(req);
		QueryInfo queryInfo = user.getBatchAuth(examBatchCode).getQueryInfo();
		UnitQueryInfo unitInfo = queryInfo.getUnitInfo(unitCode);
		UnitQueryInfo parent = queryInfo.getUnitInfo(unitInfo.getParentCode());
		Set<UnitType> rs = queryInfo.getUnitTypes(unitInfo.getUnitTypeCode(), unitInfo.isCompare() && parent != null && parent.isUnShow());
		if (unitInfo.isShowStudent()) {
			rs.add(new UnitType(Type.STUDENT));
		}
		return rs;
	}
	@At("exam/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object getExam(String unitCode, String examBatchCode, HttpServletRequest req) throws Exception {
		User user = SessionHelper.getUser(req);
		Set<ExamType> examTypes= user.getBatchAuth(examBatchCode).getQueryInfo().getUnitInfo(unitCode).getExams();
		Set<ExamTypeGroup> typeGroups = user.getBatchAuth(examBatchCode).getQueryInfo().getUnitInfo(unitCode).getExamGroups();
		Map<String, Object> rs = new HashMap<String, Object>();
		List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
		for (ExamType examType : examTypes) {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("name", examType.name);
			temp.put("code", examType.code);
			list1.add(temp);
		}
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		for (ExamTypeGroup examTypeGroup : typeGroups) {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("name", examTypeGroup.name);
			temp.put("code", examTypeGroup.code);
			list2.add(temp);
		}
		rs.put("exams", list1);
		rs.put("examGroups", list2);
		return rs;
	}
	@At("unit/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object getUnitInfo(String examBatchCode, int maxTypeCode, int minTypeCode, HttpServletRequest req) throws Exception {
		User user = SessionHelper.getUser(req);
		Set<UnitQueryInfo> rs = user.getBatchAuth(examBatchCode).getQueryInfo().getUnitQueryInfo().getTreeInfos();
		Util.rmNode(rs, Type.GRADE);
		if (rs.size() == 1 && maxTypeCode == Type.SCHOOL.code) {
			for (UnitQueryInfo unitQueryInfo : rs) {
				if (unitQueryInfo.getUnitTypeCode() == Type.CLASS.code) {
					String unitCode = unitQueryInfo.getParentCode().substring(0, 8);
					UnitQueryInfo school = user.getBatchAuth(examBatchCode).getQueryInfo().getUnitInfo(unitCode);
					rs.add(school);
				}
			}
		}
		if (maxTypeCode > 0) {
			Util.rmNodeMax(rs, maxTypeCode);
		}
		if (minTypeCode > 0) {
			Util.rmNodeMin(rs, minTypeCode);
		}
		return rs;
	}
	@At("unit/class/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object getClassInfo(String examBatchCode, String schoolCode, String examCode, HttpServletRequest req) throws Exception {
		User user = SessionHelper.getUser(req);
		Set<String> rs = user.getBatchAuth(examBatchCode).getQueryInfo().getUnitInfo(schoolCode).
				getUnitInfoChilds(Type.CLASS.code, Util.parseArtSci(examCode), false);
		return rs;
	}
	
	@At("chart/unit")
	@Ok("jsp:/tpl/mfs/chart-unit.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object chartUnit(String unitCode, int childTypeCode, String examCode, String examBatch,
			String examGroupCode, String myUnitCode, int back, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		QueryInfo qi = SessionHelper.getUser(req).getBatchAuth(examBatch).getQueryInfo();
		UnitQueryInfo info = qi.getUnitInfo(unitCode);
		rs.put("showStudent", info.isShowStudent());
		String unitTypeName = "";
		if (childTypeCode == 0) {
			if (info.getUnitTypeCode() == Type.CLASS.code) {
				unitTypeName = Type.STUDENT.name;
			} else if (info.getUnitTypeCode() == Type.SCHOOL.code) {
				unitTypeName = Type.CLASS.name;
			} else {
				Set<UnitQueryInfo> infos = info.getChilds();
				if (infos.size() > 0) {
					unitTypeName = infos.iterator().next().getUnitTypeName();
				}
			}
		} else {
			if (childTypeCode == info.getUnitTypeCode()) {
				myUnitCode = unitCode;
				unitCode = info.getParentCode();
				unitTypeName = info.getUnitTypeName();
			} else {
				if (childTypeCode == Type.STUDENT.code) {
					unitTypeName = Type.STUDENT.name;
				} else if (childTypeCode == Type.GRADE.code) {
					unitTypeName = Type.CLASS.name;
				} else {
					Set<String> unitCodes = info.getUnitCodeChilds(childTypeCode, examCode, examGroupCode);
					if (unitCodes.size() > 0) {
						unitTypeName = qi.getUnitInfo(unitCodes.iterator().next()).getUnitTypeName();
					}
				}
			}
		}
		rs.put("unitCode", unitCode);
		rs.put("unitTypeName", unitTypeName);
		if (back > 0) {
			info = qi.getUnitInfo(info.getParentCode());
			if (info != null) {
				rs.put("unitCode", info.getUnitCode());
			}
		}
		rs.put("unitType", childTypeCode);
		rs.put("examCode", examCode);
		rs.put("examGroupCode", examGroupCode);
		rs.put("examBatch", examBatch);
		rs.put("myUnitCode", myUnitCode);
		return rs;
	}
	
	@At("data/chart/unit")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object chartUnitData(String unitCode, int childTypeCode, String examCode, String examBatch, 
			String examGroupCode, String myUnitCode, HttpServletRequest req) throws Exception {
		User user = SessionHelper.getUser(req);
		BatchAuth auth = user.getBatchAuth(examBatch);
		QueryInfo queryInfo = auth.getQueryInfo();
		Set<String> unitCodes = queryInfo.getUnitInfo(unitCode).
				getUnitCodeChilds(childTypeCode == Type.STUDENT.code ? Type.CLASS.code : childTypeCode, examCode, examGroupCode);
		if (unitCodes.size() > 0) {
			if (examCode != null && !"".equals(examCode)) {
				examCode = examBatch.substring(0, 14) + examCode.substring(examCode.length() - 2);
				return baseService.getCourse(examCode, unitCode, unitCodes, childTypeCode, queryInfo, myUnitCode);
			} else {
				return baseService.getCourseGroup(examBatch, ExamTypeGroup.get(examGroupCode),
						unitCode, unitCodes, childTypeCode, queryInfo, auth.exams, myUnitCode);
			}
		}
		return null;
	}
	
	@At("chart/student/*")
	@Ok("jsp:/tpl/mfs/chart-student.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object chartStudent(String studentId, String examCode, 
			String examGroupCode, String examBatch, int backable, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		rs.put("studentId", studentId);
		rs.put("examCode", examCode);
		rs.put("examGroupCode", examGroupCode);
		rs.put("examBatch", examBatch);
		rs.put("backable", backable);
		return rs;
	}
	
	@At("data/chart/student/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object chartStudentData(String studentId, String examCode, 
			String examGroupCode, String examBatch, HttpServletRequest req) throws Exception {
		User user = SessionHelper.getUser(req);
		if (examCode != null && !"".equalsIgnoreCase(examCode)) {
			examCode = examBatch.substring(0, 14) + examCode.substring(examCode.length() - 2);
			return baseService.getStudentCourse(examCode, studentId, 
					user.getBatchAuth(examBatch).getQueryInfo());
		} else {
			BatchAuth auth = user.getBatchAuth(examBatch);
			return baseService.getStudentCourseGroup(examBatch, ExamTypeGroup.get(examGroupCode),
					studentId, auth.getQueryInfo(), auth.exams);
		}
	}
	
	@At("direction/unit/*")
	@Ok("jsp:/tpl/mfs/direction-unit.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object directionUnit(String unitCode, String examCode, String examBatch,
			String examGroupCode, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		rs.put("unitCode", unitCode);
		rs.put("examCode", examCode);
		rs.put("examGroupCode", examGroupCode);
		rs.put("examBatch", examBatch);
		return rs;
	}
	@At("direction/data/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object directionUnitData(String unitCode, String examCode, String examGroupCode, 
			String examBatch, boolean isStudent, HttpServletRequest req) {
		User user = SessionHelper.getUser(req);
		if (examCode != null && !"".equals(examCode)) {
			examCode = examBatch.substring(0, 14) + examCode.substring(examCode.length() - 2);
			if (isStudent) {
				return baseService.getStudentCourseDirection(examCode, unitCode, user);
			} else {
				return baseService.getCourseDirection(examCode, unitCode, user);
			}
		} else {
			if (isStudent) {
				return baseService.getStudentCourseGroupDirection(examBatch, 
						ExamTypeGroup.get(examGroupCode), unitCode, user);
			} else {
				return baseService.getCourseGroupDirection(examBatch, ExamTypeGroup.get(examGroupCode), unitCode, user);
			}
		}
	}
	
	@At("direction/student/*")
	@Ok("jsp:/tpl/mfs/direction-student.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object directionStu(String unitCode, String examCode, String examBatch,
			String examGroupCode, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		rs.put("unitCode", unitCode);
		rs.put("examCode", examCode);
		rs.put("examGroupCode", examGroupCode);
		rs.put("examBatch", examBatch);
		return rs;
	}
	
	@At("rank/unit/*")
	@Ok("jsp:/tpl/mfs/rank-unit.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object rank(String unitCode, String examCode, String groupCode,
			String examBatch, String name, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		rs.put("unitCode", unitCode);
		rs.put("examCode", examCode);
		rs.put("groupCode", groupCode);
		rs.put("examBatch", examBatch);
		rs.put("name", name);
		return rs;
	}
	@At("rank/data/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object rankData(String unitCode, String examCode, String groupCode,
			String examBatch, String name, int unitType, HttpServletRequest req) {
		User user = SessionHelper.getUser(req);
		if (examCode != null && !"".equals(examCode)) {
			examCode = examBatch.substring(0, 14) + examCode.substring(examCode.length() - 2);
			NA na = CacheUtil.getInstance().getKa(examCode).getCode(name);
			return baseService.getRank(examCode, na, unitCode, unitType, user);
		} else {
			return baseService.getRankGroup(examBatch, ExamTypeGroup.get(groupCode), unitCode, unitType, user);
		}
	}
	@At("rank/student/*")
	@Ok("jsp:/tpl/mfs/rank-student.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object rankStu(String unitCode, String examCode, String groupCode,
			String examBatch, String name, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		rs.put("unitCode", unitCode);
		rs.put("examCode", examCode);
		rs.put("groupCode", groupCode);
		rs.put("examBatch", examBatch);
		rs.put("name", name);
		return rs;
	}
	@At("question/img/*")
	@Ok("jsp:/tpl/mfs/question-img.jsp")
	@Fail("redirect:/no-image.html")
	@Filters({@By(type=LoginFilter.class)})
	public Object question(String studentId, String examCode, String name, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		ExamStudentInfo examStudentInfo = baseService.getStudentInfo(studentId, examCode.substring(0, 14) + "00");
		rs.put("examNo", examStudentInfo.getExamNo());
		rs.put("examCode", examCode);
		List<ExamQuestionInfo> infos = baseService.getExamQuestionInfos(examCode, name);
		rs.put("questions", infos);
		Record scores = baseService.getExamQuestionScores(examStudentInfo.getExamNo(), examCode);
		Map<String, Double> studentScores = new HashMap<String, Double>();
		for (ExamQuestionInfo info : infos) {
			String title = info.getQuestionTitle();
			title = title.replaceAll("-", "_");
			title = title.replaceAll("\\.", "_");
			studentScores.put(info.getQuestionTitle(), Double.valueOf(scores.getString("q_" + title)) / 100);
			info.setQuestionPoint(info.getQuestionPoint() / 100);
		}
		rs.put("scores", studentScores);
		
//		ImageHelper helper = new ImageHelper();
//		String param = String.format("examNo=%s&examCode=%s&title=%s", examStudentInfo.getExamNo(), examCode, titles.get(0));
//        byte[] aa = helper.post(baseService.getImagePath(), param).toByteArray();
//		if (aa.length == 0) {
//			throw new Exception();
//		}
		return rs;
	}
	@At("question")
	@Ok("jsp:jsp.question")
	@Filters({@By(type=LoginFilter.class)})
	public Object questionImg(String studentId, String examCode, String name, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		rs.put("studentId", studentId);
		rs.put("examCode", examCode);
		rs.put("name", name);
		return rs;
	}
	
//	@At("image")
//	@Ok("raw:png")
//	@Filters({@By(type=LoginFilter.class)})
//	public Object imageStu(String examNo, String examCode, String title, HttpServletRequest req) throws Exception {
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("examNo", examNo);
//		params.put("examCode", examCode);
//		params.put("title", title);
//		Request request = Request.create(baseService.getImagePath(), Request.METHOD.GET, params);
//		request.setEnc("utf-8");
//		Response resp = Sender.create(request, 30000).send();
//		return resp.getStream();
//	}
	@At("question/original")
	@Ok("jsp:/tpl/mfs/question-img-original.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object questionImgOriginal(String examCode, String title, String examNo, HttpServletRequest req) throws Exception {
		HashMap<String, Object> rs = new HashMap<String, Object>();
		rs.put("examCode", examCode);
		rs.put("title", title);
		rs.put("examNo", examNo);
		return rs;
	}
	@At("search")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object search(String examBatch, String examNo, HttpServletRequest req) {
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("suc", false);
		ExamStudentInfo stu = baseService.getStudentInfoByExamNo(examNo, examBatch);
		if (stu != null) {
			boolean find = false;
			User user = SessionHelper.getUser(req);
			QueryInfo queryInfo = user.getBatchAuth(examBatch).getQueryInfo();
			UnitQueryInfo info = queryInfo.getUnitInfo(stu.getClassCode());
			if (info.isShowStudent()) {
				while (info != null) {
					if (info.getUnitCode().equals(user.getUnitCode())) {
						find = true;
						break;
					}
					info = queryInfo.getUnitInfo(info.getParentCode());
				}
			}
			if (find) {
				rs.put("suc", true);
				rs.put("classCode", stu.getClassCode());
				rs.put("studentId", stu.getStudentId());
				rs.put("examGroupCode", ExamTypeGroup.getByClassCode(stu.getClassCode()).code);
			} else {
				rs.put("msg", "对不起，你没有权限查看该考生的信息");
			}
		} else {
			rs.put("msg", "你输入的考号错误,没有找到对应考生");
		}
		return rs;
	}
	@At("markingOn")
	@Ok("jsp:jsp.marking-on")
	@Filters({@By(type=LoginFilter.class)})
	public Object markingOn(String type, String value, String unitCode, int childTypeCode,
			String batchCode, String examName, HttpServletRequest req) throws Exception {
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("type", type);
		rs.put("value", value);
		rs.put("unitCode", unitCode);
		rs.put("childTypeCode", childTypeCode);
		rs.put("batchCode", batchCode);
		rs.put("examName", examName);
		return rs;
	}
	@At("markingOnTpl")
	@Ok("jsp:/tpl/mfs/marking-on.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public Object markingOnTpl(String type, String value, String unitCode, int childTypeCode,
			String batchCode, String examName, HttpServletRequest req) throws Exception {
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("type", type);
		rs.put("value", value);
		rs.put("unitCode", unitCode);
		QueryInfo queryInfo = SessionHelper.getUser(req).getBatchAuth(batchCode).getQueryInfo();
		String unitTypeName = "";
		UnitQueryInfo info = queryInfo.getUnitInfo(unitCode);
		if (childTypeCode == 0) {
			if (info.getUnitTypeCode() == Type.SCHOOL.code) {
				unitTypeName = Type.CLASS.name;
				childTypeCode = Type.CLASS.code;
			} else {
				Set<UnitQueryInfo> infos = info.getChilds();
				if (infos.size() > 0) {
					UnitQueryInfo inf = infos.iterator().next();
					unitTypeName = inf.getUnitTypeName();
					childTypeCode = inf.getUnitTypeCode();
				}
			}
		} else {
			Set<UnitType> types = queryInfo.getUnitTypes(info.getUnitTypeCode(), false);
			for (UnitType unitType : types) {
				if (unitType.getCode() == childTypeCode) {
					unitTypeName = unitType.getName();
				}
			}
		}
		rs.put("unitTypeName", unitTypeName);
		rs.put("childTypeCode", childTypeCode);
		rs.put("batchCode", batchCode);
		rs.put("examName", examName);
		return rs;
	}
	@At("data/markingOn")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object markingOnData(String type, String value, String unitCode, int childTypeCode, 
			String batchCode, String examName, HttpServletRequest req) throws Exception {
		QueryInfo queryInfo = SessionHelper.getUser(req).getBatchAuth(batchCode).getQueryInfo();
		return baseService.getMarkingOn(queryInfo, type, value, unitCode, childTypeCode, batchCode, examName);
	}
}
