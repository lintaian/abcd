package com.lps.tqms.dao.ifs;

import java.util.List;
import java.util.Set;

import org.nutz.dao.entity.Record;

import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.rs.RankScore;

public interface StDaoIF {
	Record getCourse(String examCode, Set<String> classCodes);
	List<Record> getCourseCls(String examCode, Set<String> classCodes);
	List<Record> getCourseStu(String examCode, Set<String> classCodes);
	Record getCourseGroup(String batchCode, ExamTypeGroup group, Set<String> classCodes, Set<ExamType> exams);
	List<Record> getCourseGroupCls(String batchCode, ExamTypeGroup group, Set<String> classCodes, Set<ExamType> exams);
	List<Record> getCourseGroupStu(String batchCode, ExamTypeGroup group, Set<String> classCodes, Set<ExamType> exams);
	
	Record getCourseStu(String examCode, String examNo);
	Record getCourseGroupStu(String batchCode, ExamTypeGroup group, String examNo, Set<ExamType> exams);
	
	RankScore getRankCourse(String examCode, String name, Set<String> classCodes);
	List<RankScore> getRankCourseCls(String examCode, String name, Set<String> classCodes);
	List<RankScore> getRankCourseStu(String examCode, String name, Set<String> classCodes);
	RankScore getRankCourseGroup(String batchCode, ExamTypeGroup group, Set<String> classCodes);
	List<RankScore> getRankCourseGroupCls(String batchCode, ExamTypeGroup group, Set<String> classCodes);
	List<RankScore> getRankCourseGroupStu(String batchCode, ExamTypeGroup group, Set<String> classCodes);
	
	RankScore getRankCourseStu(String examCode, String name, String examNo);
	RankScore getRankCourseGroupStu(String batchCode, ExamTypeGroup group, String examNo);
}
