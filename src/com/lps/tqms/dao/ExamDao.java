package com.lps.tqms.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;

import com.lps.tqms.dao.ifs.ExamDaoIF;
import com.lps.tqms.db.pojo.ExamBatchInfo;
import com.lps.tqms.db.pojo.ExamCourseInfo;
import com.lps.tqms.db.pojo.ExamQuestionInfo;
import com.lps.tqms.db.pojo.ExamStudentInfo;
import com.lps.tqms.db.pojo.ExamUnitInfo;
import com.lps.tqms.db.pojo.Subject;
import com.lps.tqms.pojo.UnitType.Type;
import com.lps.tqms.user.Module;
import com.lps.tqms.user.Role;
import com.lps.tqms.user.UnitQueryInfo;
import com.lps.tqms.user.User;

@IocBean
public class ExamDao extends BaseDao implements ExamDaoIF {
	@Override
	public String getImagePath() {
		return imagePath;
	}
	@Override
	public String getImagePath2() {
		return imagePath2;
	}
	@Override
	public List<ExamBatchInfo> getExamBatchInfos(List<String> examBatchCodes) {
		return dao.query(ExamBatchInfo.class, Cnd.where("examCode", "in", examBatchCodes).desc("examDate"));
	}
	@Override
	public List<ExamQuestionInfo> getExamQuestionInfos(String examCode,
			String name) {
		Sql sql = Sqls.create("select a.*,b.abilityName, c.pointName as knowledgeName "
				+ "from t_exam_question_info a, ability b, knowledge c where a.examCode = @examCode "
				+ "and a.abilityCode = b.abilityCode and a.knowledgeCode = c.pointCode "
				+ "and (b.abilityName = @aName or c.pointName = @kName)");
//		Sql sql = Sqls.create("select * "
//				+ "from t_exam_question_info a where examCode = @examCode "
//				+ "and (abilityName = @aName or pointName = @kName)");
		sql.params().set("examCode", examCode);
		sql.params().set("aName", name);
		sql.params().set("kName", name);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(ExamQuestionInfo.class));
		dao.execute(sql);
		return sql.getList(ExamQuestionInfo.class);
	}
	@Override
	public ExamQuestionInfo getExamQuestionInfo(String examCode, String title) {
		Sql sql = Sqls.create("select * "
				+ "from t_exam_question_info a where examCode = @examCode "
				+ "and questionTitle = @title");
		sql.params().set("examCode", examCode);
		sql.params().set("title", title);
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao.getEntity(ExamQuestionInfo.class));
		dao.execute(sql);
		return sql.getObject(ExamQuestionInfo.class);
	}
	@Override
	public List<ExamQuestionInfo> getExamQuestionInfos(String examCode) {
		Sql sql = Sqls.create("select * from t_exam_question_info where examCode = @examCode order by questionOrder");
		sql.params().set("examCode", examCode);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(ExamQuestionInfo.class));
		dao.execute(sql);
		return sql.getList(ExamQuestionInfo.class);
	}
	@Override
	public ExamStudentInfo getStudentInfo(String studentId, String examCode) {
		return dao.fetch(ExamStudentInfo.class, Cnd.where("studentId", "=", studentId).and("examCode", "=", examCode));
	}
	@Override
	public ExamStudentInfo getStudentInfoByExamNo(String examNo, String examCode) {
		return dao.fetch(ExamStudentInfo.class, Cnd.where("examNo", "=", examNo).and("examCode", "=", examCode));
	}
	@Override
	public Record getExamQuestionScores(String examNo, String examCode) {
		Sql sql = Sqls.createf("select * from t_%s where exam_no='%s'", examCode, examNo);
		sql.setCallback(Sqls.callback.record());
		dao.execute(sql);
		return sql.getObject(Record.class); 
	}
	@Override
	public User getUser(String userName) {
		User user = null;
		Sql sql = Sqls.create("select * from base_user where id_name = @name");
		sql.params().set("name", userName);
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao.getEntity(Record.class));
		dao.execute(sql);
		Record u1 = sql.getObject(Record.class);
		if (u1 != null) {
			user = new User();
			user.setName(u1.getString("user_name"));
			user.setLoginName(u1.getString("login_name"));
			user.setIdName(u1.getString("id_name"));
			user.setPwd(u1.getString("login_pwd"));
			user.setUnitCode(u1.getString("attch_code"));
			user.setId(u1.getString("id"));
		}
		return user;
	}
	@Override
	public User getUser(String userName, String unitCode) {
		User user = null;
		Sql sql = Sqls.create("select * from base_user where login_name = @name and attch_code = @unitCode");
		sql.params().set("name", userName);
		sql.params().set("unitCode", unitCode);
		sql.setCallback(Sqls.callback.entity());
		sql.setEntity(dao.getEntity(Record.class));
		dao.execute(sql);
		Record u1 = sql.getObject(Record.class);
		if (u1 != null) {
			user = new User();
			user.setName(u1.getString("user_name"));
			user.setLoginName(u1.getString("login_name"));
			user.setIdName(u1.getString("id_name"));
			user.setPwd(u1.getString("login_pwd"));
			user.setUnitCode(u1.getString("attch_code"));
			user.setId(u1.getString("id"));
		}
		return user;
	}
	@Override
	public boolean updateUserPwd(String id, String pwd) {
		try {
			Sql sql = Sqls.create("update base_user set login_pwd = @pwd");
			sql.params().set("pwd", pwd);
			dao.execute(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public List<UnitQueryInfo> getUnitInfo(String queryId, String examCode) {
		Sql sql = Sqls.create("select queryId,unitCode,unitName,parentCode,unitTypeName,unitTypeCode"
				+ " from t_exam_unit_query_info where queryId = @queryId and (unitTypeCode not in" + Type.getCodeList()
				+ " or unitCode in(select orgi_code from exam_orgi_ref where exam_code = @examCode)) order by unitTypeCode");
		sql.params().set("queryId", queryId);
		sql.params().set("examCode", examCode);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(UnitQueryInfo.class));
		dao.execute(sql);
		List<UnitQueryInfo> rs = sql.getList(UnitQueryInfo.class);
		List<UnitQueryInfo> rm = new ArrayList<UnitQueryInfo>();
		for (UnitQueryInfo info : rs) {
			if (!Type.isInclude(info.getUnitTypeCode())) {
				rm.add(info);
			} else {
				break;
			}
		}
		if (rm.size() > 0) {
			rs.removeAll(rm);
		}
		return rs;
	}
	@Override
	public List<UnitQueryInfo> getUnitInfo(String examCode) {
		Sql sql = Sqls.create("select unitCode,unitName,parentCode,unitTypeName,unitTypeCode"
				+ " from t_exam_unit_info where unitCode in(select orgi_code from exam_orgi_ref where exam_code = @examCode) order by unitTypeCode");
		sql.params().set("examCode", examCode);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(UnitQueryInfo.class));
		dao.execute(sql);
		return sql.getList(UnitQueryInfo.class);
	}
	@Override
	public List<String> getQueryIds(String userId, String examCode) {
		Sql sql = Sqls.create("select distinct query_code from exam_query where (user_id = '0' or user_id = @userId) "
				+ "and (exam_code = '0' or exam_code = @examCode)");
		sql.params().set("userId", userId);
		sql.params().set("examCode", examCode);
		sql.setCallback(Sqls.callback.strList());
		dao.execute(sql);
		return sql.getList(String.class);
	}
	@Override
	public List<Role> getRoles(String userId, int marking) {
		Sql sql = Sqls.create("select user_id,orgi_code,exam_code,sub_code,marking,show_student,un_compare "
				+ "from role where user_id = @userId and marking = @marking order by exam_code,sub_code,show_student desc,un_compare");
		sql.params().set("userId", userId);
		sql.params().set("marking", marking);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(Role.class));
		dao.execute(sql);
		return sql.getList(Role.class);
	}
	@Override
	public List<Subject> getSubjects(String examCode) {
		Sql sql = Sqls.create("select a.id,a.subject_name as name,a.query_name as queryName,a.art_sci as artSci,a.subject_description as description,a.real from subject a,exam_subject_ref b"
				+ " where a.id = b.subject_id and b.exam_code = @examCode");
		sql.params().set("examCode", examCode);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(Subject.class));
		dao.execute(sql);
		return sql.getList(Subject.class);
	}
	@Override
	public List<String> getExamBatchs(String unitCode) {
		Sql sql = Sqls.create("select distinct exam_code from exam_orgi_ref where orgi_code = @unitCode");
		sql.params().set("unitCode", unitCode);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(Record.class));
		dao.execute(sql);
		List<String> rs = new ArrayList<String>();
		for (Record record : sql.getList(Record.class)) {
			rs.add(record.getString("exam_code"));
		}
		return rs;
	}
	@Override
	public List<Record> getKnowledge(String examCode) {
//		Sql sql = Sqls.create("select knowledgeCode as name, round(sum(questionPoint)/100, 1) as val "
//				+ "from t_exam_question_info "
//				+ "where examCode = @examCode group by knowledgeName");
		Sql sql = Sqls.create("select b.pointName as name, a.knowledgeCode as code, round(sum(a.questionPoint)/100, 1) as val "
				+ "from t_exam_question_info a, knowledge b "
				+ "where a.examCode = @examCode and a.knowledgeCode = b.pointCode group by b.pointName");
		sql.params().set("examCode", examCode);
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public List<Record> getAbility(String examCode) {
//		Sql sql = Sqls.create("select abilityName as name, round(sum(questionPoint)/100, 1) as val "
//				+ "from t_exam_question_info "
//				+ "where examCode = @examCode group by abilityName");
		Sql sql = Sqls.create("select b.abilityName as name, a.abilityCode as code, round(sum(a.questionPoint)/100, 1) as val "
				+ "from t_exam_question_info a, ability b "
				+ "where a.examCode = @examCode and a.abilityCode = b.abilityCode group by b.abilityName");
		sql.params().set("examCode", examCode);
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		return sql.getList(Record.class);
	}
	@Override
	public boolean existCol(String table, String col) {
		Statement st = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(String.format("desc %s %s", table, col));
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (st != null) st.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	@Override
	public boolean existTable(String table) {
		Statement st = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(String.format("show tables like '%s'", table));
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (st != null) st.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	@Override
	public ExamBatchInfo getBatchInfo(String batchCode) {
		return dao.fetch(ExamBatchInfo.class, Cnd.where("examCode", "=", batchCode));
	}
	@Override
	public ExamCourseInfo getCourseInfo(String examCode) {
		return dao.fetch(ExamCourseInfo.class, Cnd.where("examCode", "=", examCode));
	}
	@Override
	public List<Module> getModules(String userId) {
		Sql sql = Sqls.create("select a.* from module a, user_module_ref b where a.id = b.module_code and b.user_id = @userId");
		sql.params().set("userId", userId);
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(Module.class));
		dao.execute(sql);
		return sql.getList(Module.class);
	}
	@Override
	public List<ExamUnitInfo> getUnitInfos() {
		return dao.query(ExamUnitInfo.class, Cnd.where("unitTypeCode", "<=", Type.SCHOOL.code));
	}
	@Override
	public int getStudentCount(String batchCode, String queryName, Set<String> classCodes) {
		Sql sql = Sqls.createf("select count(*) as studentCount from t_%s $condition", batchCode);
		sql.setCallback(Sqls.callback.integer());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).and(queryName, ">", 0));
		dao.execute(sql);
		return sql.getInt();
	}
	@Override
	public int getStudentCount(String batchCode, String queryName, Set<String> classCodes, double score) {
		Sql sql = Sqls.createf("select count(*) as studentCount from t_%s $condition", batchCode);
		sql.setCallback(Sqls.callback.integer());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).and(queryName, ">=", score));
		dao.execute(sql);
		return sql.getInt();
	}
	@Override
	public int getStudentCount(String batchCode, String queryName, Set<String> classCodes, double minScore, double maxScore) {
		Sql sql = Sqls.createf("select count(*) as studentCount from t_%s $condition", batchCode);
		sql.setCallback(Sqls.callback.integer());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).and(queryName, ">=", minScore).and(queryName, "<", maxScore));
		dao.execute(sql);
		return sql.getInt();
	}
	@Override
	public int getScore(String batchCode, String queryName, int studentAmount, Set<String> classCodes) {
		Sql sql = Sqls.createf("select %s from t_%s $condition limit %d,1", queryName, batchCode, studentAmount);
		sql.setCallback(Sqls.callback.integer());
		sql.setCondition(Cnd.where("class_code", "in", classCodes).desc(queryName));
		dao.execute(sql);
		return sql.getInt();
	}
	@Override
	public List<Subject> getSubjects() {
		Sql sql = Sqls.create("select id,subject_name as name,query_name as queryName,art_sci as artSci,subject_description as description,`real` from subject");
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(Subject.class));
		dao.execute(sql);
		return sql.getList(Subject.class);
	}
	
	@Override
	public List<String> getUnits(String userId) {
		Sql sql = Sqls.create("select distinct orgi_code from role where user_id = @userId");
		sql.setCallback(Sqls.callback.strList());
		sql.params().set("userId", userId);
		dao.execute(sql);
		return sql.getList(String.class);
	}
}