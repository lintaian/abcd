package com.lps.tqms.dao;

import java.util.List;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import com.lps.tqms.cache.CacheUtil;
import com.lps.tqms.cache.Ka;
import com.lps.tqms.cache.NA;
import com.lps.tqms.dao.ifs.StDaoIF;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.rs.RankScore;
@IocBean
public class StDao extends BaseDao implements StDaoIF {
	@Override
	public Record getCourse(String examCode, Set<String> classCodes) {
		Ka ka = CacheUtil.getInstance().getKa(examCode);
		StringBuilder sb = new StringBuilder("select round(avg(score)/100, 1) as score,");
		for (NA na : ka.knowledge) {
			sb.append(String.format("round(avg(`k_%s`)/100, 1) as '%s',", na.code, na.name));
		}
		for (NA na : ka.ability) {
			sb.append(String.format("round(avg(`a_%s`)/100, 1) as '%s',", na.code, na.name));
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s_ka $condition", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.record());
		dao.execute(sql);
		return sql.getObject(Record.class);
	}
	@Override
	public List<Record> getCourseCls(String examCode, Set<String> classCodes) {
		Ka ka = CacheUtil.getInstance().getKa(examCode);
		StringBuilder sb = new StringBuilder("select class_code,round(avg(score)/100, 1) as score,");
		for (NA na : ka.knowledge) {
			sb.append(String.format("round(avg(`k_%s`)/100, 1) as '%s',", na.code, na.name));
		}
		for (NA na : ka.ability) {
			sb.append(String.format("round(avg(`a_%s`)/100, 1) as '%s',", na.code, na.name));
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s_ka $condition", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).groupBy("class_code"));
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public List<Record> getCourseStu(String examCode, Set<String> classCodes) {
		Ka ka = CacheUtil.getInstance().getKa(examCode);
		StringBuilder sb = new StringBuilder("select b.studentId,b.studentName,a.class_code,round(a.score/100, 1) as score,");
		for (NA na : ka.knowledge) {
			sb.append(String.format("round(a.`k_%s`/100, 1) as 'k_%s',", na.code, na.name));
		}
		for (NA na : ka.ability) {
			sb.append(String.format("round(a.`a_%s`/100, 1) as 'a_%s',", na.code, na.name));
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s_ka a,t_exam_student_info b $condition and a.exam_no = b.examNo", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("b.examCode", "=", examCode.substring(0, 14) + "00").
				and("a.class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public Record getCourseGroup(String batchCode, ExamTypeGroup group, Set<String> classCodes, Set<ExamType> exams) {
		StringBuilder sb = new StringBuilder("select ");
		sb.append(String.format("round(avg(%s)/100, 1) as score,", group.queryName));
		for (ExamType exam : group.exams) {
			if (exams.contains(exam)) {
				sb.append(String.format("round(avg(%s)/100, 1) as %s,", exam.queryName, exam.queryName));
			}
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s $condition", batchCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.record());
		dao.execute(sql);
		return sql.getObject(Record.class);
	}
	@Override
	public List<Record> getCourseGroupCls(String batchCode, ExamTypeGroup group, Set<String> classCodes, Set<ExamType> exams) {
		StringBuilder sb = new StringBuilder("select class_code,");
		sb.append(String.format("round(avg(%s)/100, 1) as score,", group.queryName));
		for (ExamType exam : group.exams) {
			if (exams.contains(exam)) {
				sb.append(String.format("round(avg(%s)/100, 1) as %s,", exam.queryName, exam.queryName));
			}
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s $condition", batchCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).groupBy("class_code"));
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public List<Record> getCourseGroupStu(String batchCode, ExamTypeGroup group, Set<String> classCodes, Set<ExamType> exams) {
		StringBuilder sb = new StringBuilder("select b.studentId,b.studentName,a.class_code,");
		sb.append(String.format("round(a.%s/100, 1) as score,", group.queryName));
		for (ExamType exam : group.exams) {
			if (exams.contains(exam)) {
				sb.append(String.format("round(a.%s/100, 1) as %s,", exam.queryName, exam.queryName));
			}
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s a,t_exam_student_info b $condition and a.exam_no = b.examNo", batchCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("b.examCode", "=", batchCode).and("a.class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public Record getCourseStu(String examCode, String examNo) {
		Ka ka = CacheUtil.getInstance().getKa(examCode);
		StringBuilder sb = new StringBuilder("select round(score/100, 1) as score,");
		for (NA na : ka.knowledge) {
			sb.append(String.format("round(`k_%s`/100, 1) as 'k_%s',", na.code, na.name));
		}
		for (NA na : ka.ability) {
			sb.append(String.format("round(`a_%s`/100, 1) as 'a_%s',", na.code, na.name));
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s_ka where exam_no = '%s'", examCode, examNo));
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.record());
		dao.execute(sql);
		return sql.getObject(Record.class);
	}
	@Override
	public Record getCourseGroupStu(String batchCode, ExamTypeGroup group, String examNo, Set<ExamType> exams) {
		StringBuilder sb = new StringBuilder("select ");
		sb.append(String.format("round(%s/100, 1) as score,", group.queryName));
		for (ExamType exam : group.exams) {
			if (exams.contains(exam)) {
				sb.append(String.format("round(%s/100, 1) as %s,", exam.queryName, exam.queryName));
			}
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s where exam_no = '%s'", batchCode, examNo));
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.record());
		dao.execute(sql);
		return sql.getObject(Record.class);
	}
	@Override
	public RankScore getRankCourse(String examCode, String name, Set<String> classCodes) {
		StringBuilder sb = new StringBuilder();
		if (name == null || "".equals(name)) {
			sb.append("select avg(score) as score");
		} else {
			sb.append(String.format("select avg(`%s`) as score", name));
		}
		sb.append(String.format(" from t_%s_ka $condition", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getObject(RankScore.class);
	}
	@Override
	public List<RankScore> getRankCourseCls(String examCode, String name, Set<String> classCodes) {
		StringBuilder sb = new StringBuilder();
		if (name == null || "".equals(name)) {
			sb.append("select avg(score) as score");
		} else {
			sb.append(String.format("select avg(`%s`) as score", name));
		}
		sb.append(String.format(" from t_%s_ka $condition", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).groupBy("class_code"));
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getList(RankScore.class);
	}
	@Override
	public List<RankScore> getRankCourseStu(String examCode, String name, Set<String> classCodes) {
		StringBuilder sb = new StringBuilder();
		if (name == null || "".equals(name)) {
			sb.append("select score");
		} else {
			sb.append(String.format("select `%s` as score", name));
		}
		sb.append(String.format(" from t_%s_ka $condition", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getList(RankScore.class);
	}
	@Override
	public RankScore getRankCourseGroup(String batchCode, ExamTypeGroup group, Set<String> classCodes) {
		StringBuilder sb = new StringBuilder("select ");
		sb.append(String.format("avg(%s) as score", group.queryName));
		sb.append(String.format(" from t_%s $condition", batchCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getObject(RankScore.class);
	}
	@Override
	public List<RankScore> getRankCourseGroupCls(String batchCode, ExamTypeGroup group, Set<String> classCodes) {
		StringBuilder sb = new StringBuilder("select ");
		sb.append(String.format("avg(%s) as score", group.queryName));
		sb.append(String.format(" from t_%s $condition", batchCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).groupBy("class_code"));
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getList(RankScore.class);
	}
	@Override
	public List<RankScore> getRankCourseGroupStu(String batchCode, ExamTypeGroup group, Set<String> classCodes) {
		StringBuilder sb = new StringBuilder("select ");
		sb.append(String.format(" %s as score", group.queryName));
		sb.append(String.format(" from t_%s $condition", batchCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", classCodes));
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getList(RankScore.class);
	}
	@Override
	public RankScore getRankCourseStu(String examCode, String name, String examNo) {
		StringBuilder sb = new StringBuilder();
		if (name == null || "".equals(name)) {
			sb.append("select avg(score) as score");
		} else {
			sb.append(String.format("select avg(`%s`) as score", name));
		}
		sb.append(String.format(" from t_%s_ka $condition", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("exam_no", "=", examNo));
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getObject(RankScore.class);
	}
	@Override
	public RankScore getRankCourseGroupStu(String batchCode, ExamTypeGroup group, String examNo) {
		StringBuilder sb = new StringBuilder("select ");
		sb.append(String.format("avg(%s) as score", group.queryName));
		sb.append(String.format(" from t_%s $condition", batchCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("exam_no", "=", examNo));
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao.getEntity(RankScore.class));
		dao.execute(sql);
		return sql.getObject(RankScore.class);
	}
}
