package com.lps.tqms.dao.ifs;

import java.util.List;
import java.util.Set;

import org.nutz.dao.entity.Record;

import com.lps.tqms.db.pojo.ExamBatchInfo;
import com.lps.tqms.db.pojo.ExamCourseInfo;
import com.lps.tqms.db.pojo.ExamQuestionInfo;
import com.lps.tqms.db.pojo.ExamStudentInfo;
import com.lps.tqms.db.pojo.ExamUnitInfo;
import com.lps.tqms.db.pojo.Subject;
import com.lps.tqms.user.Module;
import com.lps.tqms.user.Role;
import com.lps.tqms.user.UnitQueryInfo;
import com.lps.tqms.user.User;

public interface ExamDaoIF {
	String getImagePath();
	String getImagePath2();
	List<ExamBatchInfo> getExamBatchInfos(List<String> batchCodes);
	ExamStudentInfo getStudentInfo(String studentId, String examCode);
	ExamStudentInfo getStudentInfoByExamNo(String examNo, String examCode);
	List<ExamQuestionInfo> getExamQuestionInfos(String examCode, String name);
	List<ExamQuestionInfo> getExamQuestionInfos(String examCode);
	ExamQuestionInfo getExamQuestionInfo(String examCode, String title);
	Record getExamQuestionScores(String examNo, String examCode);
	
	User getUser(String userName);
	User getUser(String userName, String unitCode);
	boolean updateUserPwd(String id, String pwd);
	List<UnitQueryInfo> getUnitInfo(String queryId, String examCode);
	List<UnitQueryInfo> getUnitInfo(String examCode);
	List<String> getQueryIds(String userId, String examCode);
	List<Role> getRoles(String userId, int marking);
	List<Subject> getSubjects(String examCode);
	List<String> getExamBatchs(String unitCode);
	/**
	 *	根据用户Id获取用户所有权限的组织编号
	 * @param userId
	 * @return
	 */
	List<String> getUnits(String userId);
	
	boolean existCol(String table, String col);
	boolean existTable(String table);

	List<Record> getKnowledge(String examCode);
	List<Record> getAbility(String examCode);
	ExamBatchInfo getBatchInfo(String batchCode);
	ExamCourseInfo getCourseInfo(String examCode);
	List<Module> getModules(String userId);
	List<ExamUnitInfo> getUnitInfos();
	/**
	 * 获取学生人数
	 * @param batchCode
	 * @param queryName
	 * @param classCodes
	 * @return
	 */
	int getStudentCount(String batchCode, String queryName, Set<String> classCodes);
	/**
	 * 获取学生人数
	 * @param batchCode
	 * @param queryName
	 * @param classCodes
	 * @return
	 */
	int getStudentCount(String batchCode, String queryName, Set<String> classCodes, double score);
	/**
	 * 获取学生人数
	 * @param batchCode
	 * @param queryName
	 * @param classCodes
	 * @return
	 */
	int getStudentCount(String batchCode, String queryName, Set<String> classCodes, double minScore, double maxScore);
	/**
	 * 获取指定位置的学生成绩
	 * @param batchCode
	 * @param queryName
	 * @param studentAmount
	 * @param classCodes
	 * @return
	 */
	int getScore(String batchCode, String queryName, int studentAmount, Set<String> classCodes);
	/**
	 * 获取科目信息
	 * @return
	 */
	List<com.lps.tqms.db.pojo.Subject> getSubjects();
}
