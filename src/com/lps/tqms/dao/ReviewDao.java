package com.lps.tqms.dao;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import com.lps.tqms.cache.CacheUtil;
import com.lps.tqms.dao.ifs.ReviewDaoIF;
import com.lps.tqms.db.pojo.ExamQuestionInfo;
import com.lps.tqms.util.Util;
@IocBean
public class ReviewDao extends BaseDao implements ReviewDaoIF {
	@Override
	public List<Record> getDifficulty(List<String> unitCodes, String examCode) {
		List<ExamQuestionInfo> infos =  CacheUtil.getInstance().getQuestionInfos(examCode);
		StringBuilder sb = new StringBuilder("select class_code,round(avg(score)/100, 1) as score,");
		for (ExamQuestionInfo info : infos) {
			String title = "q_" + Util.formatTitle(info.getQuestionTitle());
			sb.append("round(avg(");
			sb.append(title);
			sb.append(")/");
			sb.append(info.getQuestionPoint());
			sb.append(", 1) as ");
			sb.append(title);
			sb.append(",");
		}
		sb.setLength(sb.length() - 1);
		sb.append(String.format(" from t_%s $condition", examCode));
		Sql sql = Sqls.create(sb.toString());
		sql.setCondition(Cnd.where("class_code", "in", unitCodes).groupBy("class_code"));
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public List<Record> getQuestionScore(List<String> unitCodes, String examCode) {
		Sql sql = Sqls.createf("select a.*,b.studentName from t_%s a,t_exam_student_info b $condition "
				+ "and a.class_code = b.classCode and a.exam_no = b.examNo order by a.class_code", examCode);
		sql.setCondition(Cnd.where("a.class_code", "in", unitCodes).
				and("b.examCode", "=", examCode.substring(0, examCode.length() - 2) + "00"));
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public List<Record> getStuQuestionScore(List<String> unitCodes,
			String examCode, String questionTitle) {
		Sql sql = Sqls.createf("select a.class_code, round(a.q_%s/100, 1) as score, a.exam_no, b.studentName from t_%s a,t_exam_student_info b $condition "
				+ "and a.class_code = b.classCode and a.exam_no = b.examNo order by a.class_code", Util.formatTitle(questionTitle), examCode);
		sql.setCondition(Cnd.where("a.class_code", "in", unitCodes).
				and("b.examCode", "=", examCode.substring(0, examCode.length() - 2) + "00"));
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
}
