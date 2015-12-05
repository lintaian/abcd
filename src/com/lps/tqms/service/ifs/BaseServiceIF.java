package com.lps.tqms.service.ifs;

import java.util.List;
import java.util.Set;

import org.nutz.dao.entity.Record;

import com.lps.tqms.cache.NA;
import com.lps.tqms.db.pojo.ExamQuestionInfo;
import com.lps.tqms.db.pojo.ExamStudentInfo;
import com.lps.tqms.db.pojo.ExamUnitInfo;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.rs.CourseGroupResult;
import com.lps.tqms.rs.CourseResult;
import com.lps.tqms.rs.MarkingOnResult;
import com.lps.tqms.rs.Rank;
import com.lps.tqms.rs.StudentGroupResult;
import com.lps.tqms.rs.StudentResult;
import com.lps.tqms.user.QueryInfo;
import com.lps.tqms.user.User;

public interface BaseServiceIF {
	String getImagePath();
	String getImagePath2();
	ExamStudentInfo getStudentInfo(String studentId, String examCode);
	ExamStudentInfo getStudentInfoByExamNo(String examNo, String examCode);
	List<ExamQuestionInfo> getExamQuestionInfos(String examCode, String name);
	ExamQuestionInfo getExamQuestionInfo(String examCode, String title);
	Record getExamQuestionScores(String examNo, String examCode);
	
	CourseResult getCourse(String examCode, String unitCode, Set<String> unitCodes, int childType, QueryInfo queryInfo, String myUnitCode);
	CourseGroupResult getCourseGroup(String batchCode, ExamTypeGroup group, String unitCode, Set<String> unitCodes, 
			int childType, QueryInfo queryInfo, Set<ExamType> exams, String myUnitCode);
	StudentResult getStudentCourse(String examCode, String studentId, QueryInfo queryInfo);
	StudentGroupResult getStudentCourseGroup(String batchCode, ExamTypeGroup group, String studentId, QueryInfo queryInfo, Set<ExamType> exams);
	
	CourseResult getCourseDirection(String examCode, String unitCode, User user);
	CourseGroupResult getCourseGroupDirection(String batchCode, ExamTypeGroup group, String unitCode, User user);
	CourseResult getStudentCourseDirection(String examCode, String studentId, User user);
	CourseGroupResult getStudentCourseGroupDirection(String batchCode, ExamTypeGroup group, String studentId, User user);
	
	Rank getRank(String examCode, NA na, String unitCode, int type, User user);
	Rank getRankGroup(String batchCode, ExamTypeGroup group, String unitCode, int type, User user);
	
	User getUser(String userName);
	User getUserWithAuth(String userName);
	User getUser(String userName, String unitCode);
	User getUserWithAuth(String userName, String unitCode);
	boolean updateUserPwd(String id, String pwd);
	List<ExamUnitInfo> getUnitInfos();
	MarkingOnResult getMarkingOn(QueryInfo queryInfo, String type, String value, String unitCode, int childTypeCode, 
			String batchCode, String examName);
	
	//讲评卷
	List<Record> getDifficulty(List<String> unitCodes, String examCode);
	List<Record> getQuestionScore(List<String> unitCodes, String examCode);
	List<Record> getStuQuestionScore(List<String> unitCodes, String examCode, String questionTitle);
}
