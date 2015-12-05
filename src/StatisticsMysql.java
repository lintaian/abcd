import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;

import com.alibaba.druid.pool.DruidDataSource;

public class StatisticsMysql {
	static String connString = "jdbc:mysql://121.201.7.201:3309/diagnose?characterEncoding=UTF-8";
	static String connUser = "sa";
	static String connPass = "13980439852";
	static String dbDriver = "com.mysql.jdbc.Driver";
	static Dao dao;
	static Connection conn;
	static String queryId;
	static String examCode;
	static String unitCode;
	
	static Map<String, Record> students = new HashMap<String, Record>();
	static Map<String, Record> exams = new HashMap<String, Record>();
	static Map<String, Record> units = new HashMap<String, Record>();
	
	static final int PAGE_SIZE = 5000;
	
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("请带上参数!");
			System.out.println("按顺序依次是: unitCode examBatchCode queryId");
			return;
		}
		unitCode = args[0];
		examCode = args[1].substring(0, 14) + "%";
		queryId = args[2];
		DruidDataSource dds = new DruidDataSource();
		dds.setDriverClassName(dbDriver);
		dds.setUrl(connString);
		dds.setUsername(connUser);
		dds.setPassword(connPass);
		dao = new NutDao(dds);
		Class.forName(dbDriver);
		conn = DriverManager.getConnection(connString, connUser, connPass);
		long s = System.currentTimeMillis();
		loadStudent();
		loadExam();
		loadUnit();
		studentCourse();
		studentAbility();
		studentKnowledge();
		unitCourse(unitCode);
		unitAbility(unitCode);
		unitKnowledge(unitCode);
		conn.close();
		long e = System.currentTimeMillis();
		System.out.println("this statistics total time: " + (e - s));
	}
	
	public static void studentCourse() throws Exception {
		System.out.println("statistics student course start");
		long start = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select examCode, classCode,studentId,sum(questionScore) as score"
				+ " from t_exam_question_score where examCode like ?"
				+ " group by examCode,classCode,studentId");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int pageNumber = 0;
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("examCode", rs.getObject(1));
			map.put("classCode", rs.getObject(2));
			map.put("studentId", rs.getObject(3));
			map.put("score", rs.getObject(4));
			list.add(map);
			if (list.size() == PAGE_SIZE) {
				insertStudentCourses(list, ++pageNumber);
				list = new ArrayList<Map<String,Object>>();
			}
		}
		if (list.size() > 0) {
			insertStudentCourses(list, ++pageNumber);
		}
		rs.close();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("statistics student course end -> " + (end - start));
	}
	public static void insertStudentCourses(List<Map<String, Object>> list, int pageNumber) throws Exception {
		System.out.println("insertStudentCourses " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		long s = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder("insert into st_stu_course(examCode,classCode,studentId,examName,studentName,score,point) values");
		for (Map<String, Object> map : list) {
			sql.append(String.format("('%s','%s','%s','%s','%s',%f,%f),", 
					map.get("examCode").toString(),
					map.get("classCode").toString(),
					map.get("studentId").toString(),
					exams.get(map.get("examCode").toString()).getString("examName"),
					students.get(map.get("studentId").toString()).getString("studentName"),
					Double.valueOf(map.get("score").toString()),
					Double.valueOf(exams.get(map.get("examCode").toString()).getString("examPoint"))));
		}
		sql.setLength(sql.length() - 1);
		Statement st = conn.createStatement();
		st.execute(sql.toString());
		st.close();
		long e = System.currentTimeMillis();
		System.out.println("insert end : " + (e - s));
	}
	
	
	public static void studentAbility() throws Exception {
		long start = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select a.examCode,a.abilityName as name,"
				+ "b.studentId, b.classCode,sum(b.questionScore) as score,sum(a.questionPoint) as point"
				+ "	from t_exam_question_info a, t_exam_question_score b"
				+ " where a.examCode like ? and a.examCode=b.examCode and a.questionTitle = b.questionTitle"
				+ "	group by b.classCode,a.examCode,a.abilityName,b.studentId");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int pageNumber = 0;
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("examCode", rs.getObject(1));
			map.put("name", rs.getObject(2));
			map.put("studentId", rs.getObject(3));
			map.put("classCode", rs.getObject(4));
			map.put("score", rs.getObject(5));
			map.put("point", rs.getObject(6));
			list.add(map);
			if (list.size() == PAGE_SIZE) {
				insertStudentAbility(list, ++pageNumber);
				list = new ArrayList<Map<String,Object>>();
			}
		}
		if (list.size() > 0) {
			insertStudentAbility(list, ++pageNumber);
		}
		rs.close();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("statistics student ability end -> " + (end - start));
	}
	public static void insertStudentAbility(List<Map<String, Object>> list, int pageNumber) throws Exception {
		System.out.println("insertStudentAbility " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		long s = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder("insert into st_stu_ability(examCode,name,studentId,classCode,studentName,score,point) values");
		for (Map<String, Object> map : list) {
			sql.append(String.format("('%s','%s','%s','%s','%s',%f,%f),", 
					map.get("examCode").toString(),
					map.get("name").toString(),
					map.get("studentId").toString(),
					map.get("classCode").toString(),
					students.get(map.get("studentId").toString()).getString("studentName"),
					Double.valueOf(map.get("score").toString()),
					Double.valueOf(map.get("point").toString())));
		}
		sql.setLength(sql.length() - 1);
		Statement st = conn.createStatement();
		st.execute(sql.toString());
		st.close();
		long e = System.currentTimeMillis();
		System.out.println("insert end : " + (e - s));
	}
	
	
	public static void studentKnowledge() throws Exception {
		System.out.println("statistics student knowledge start");
		long start = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select a.examCode,a.knowledgeName as name,b.studentId, "
				+ "b.classCode,sum(b.questionScore) as score,sum(a.questionPoint) as point"
				+ "	from t_exam_question_info a, t_exam_question_score b"
				+ " where a.examCode like ? and a.examCode=b.examCode and a.questionTitle = b.questionTitle"
				+ "	group by b.classCode,a.examCode,a.knowledgeName,b.studentId");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int pageNumber = 0;
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("examCode", rs.getObject(1));
			map.put("name", rs.getObject(2));
			map.put("studentId", rs.getObject(3));
			map.put("classCode", rs.getObject(4));
			map.put("score", rs.getObject(5));
			map.put("point", rs.getObject(6));
			list.add(map);
			if (list.size() == PAGE_SIZE) {
				insertStudentKnowledge(list, ++pageNumber);
				list = new ArrayList<Map<String,Object>>();
			}
		}
		if (list.size() > 0) {
			insertStudentKnowledge(list, ++pageNumber);
		}
		rs.close();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("statistics student knowledge end -> " + (end - start));
	}
	public static void insertStudentKnowledge(List<Map<String, Object>> list, int pageNumber) throws Exception {
		System.out.println("insertStudentKnowledge " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		long s = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder("insert into st_stu_knowledge(examCode,name,studentId,classCode,studentName,score,point) values");
		for (Map<String, Object> map : list) {
			sql.append(String.format("('%s','%s','%s','%s','%s',%f,%f),", 
					map.get("examCode").toString(),
					map.get("name").toString(),
					map.get("studentId").toString(),
					map.get("classCode").toString(),
					students.get(map.get("studentId").toString()).getString("studentName"),
					Double.valueOf(map.get("score").toString()),
					Double.valueOf(map.get("point").toString())));
		}
		sql.setLength(sql.length() - 1);
		Statement st = conn.createStatement();
		st.execute(sql.toString());
		st.close();
		long e = System.currentTimeMillis();
		System.out.println("insert end : " + (e - s));
	}
	
	
	public static void unitCourse(String unitCode) throws Exception {
		if (unitCode.length() > 16) {
			return;
		}
		System.out.println("statistics unit course start : " + unitCode);
		long start = System.currentTimeMillis();
		List<String> temp = new ArrayList<String>();
		if (unitCode.length() == 16) {
			temp.add(unitCode);
		} else {
			getChildClass(unitCode, temp);
		}
		StringBuilder sb = new StringBuilder("select examCode, avg(score) as score from st_stu_course where classCode in (");
		for (int i = 0; i < temp.size(); i++) {
			sb.append(String.format("'%s',", temp.get(i)));
		}
		sb.setLength(sb.length() - 1);
		sb.append(") and examCode like ? group by examCode");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		ps.setString(1, examCode);
		System.out.println(ps.toString());
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int pageNumber = 0;
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("examCode", rs.getObject(1));
			map.put("score", rs.getObject(2));
			list.add(map);
			if (list.size() == PAGE_SIZE) {
				insertUnitCourse(list, unitCode, ++pageNumber);
				list = new ArrayList<Map<String,Object>>();
			}
		}
		if (list.size() > 0) {
			insertUnitCourse(list, unitCode, ++pageNumber);
		}
		rs.close();
		ps.close();
		
		long end = System.currentTimeMillis();
		System.out.println("statistics unit course end : " + unitCode + "	-->" + (end - start));
		
		List<String> child = getChild(unitCode);
		for (String str : child) {
			unitCourse(str);
		}
	}
	public static void insertUnitCourse(List<Map<String, Object>> list, String unitCode, int pageNumber) throws Exception {
		System.out.println("insertUnitCourse " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		long s = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder("insert into st_unit_course(examCode,unitCode,examName,unitName,score,point) values");
		Record unit = units.get(unitCode);
		for (Map<String, Object> map : list) {
			sql.append(String.format("('%s','%s','%s','%s',%f,%f),", 
						map.get("examCode").toString(),
						unit.getString("unitCode"),
						exams.get(map.get("examCode").toString()).getString("examName"),
						unit.getString("unitName"),
						Double.valueOf(map.get("score").toString()),
						Double.valueOf(exams.get(map.get("examCode").toString()).getString("examPoint"))
					));
		}
		sql.setLength(sql.length() - 1);
		Statement st = conn.createStatement();
		st.execute(sql.toString());
		st.close();
		long e = System.currentTimeMillis();
		System.out.println("insert end : " + (e - s));
	}
	
	public static void unitAbility(String unitCode) throws Exception {
		if (unitCode.length() > 16) {
			return;
		}
		System.out.println("statistics unit ability start : " + unitCode);
		long start = System.currentTimeMillis();
		List<String> temp = new ArrayList<String>();
		if (unitCode.length() == 16) {
			temp.add(unitCode);
		} else {
			getChildClass(unitCode, temp);
		}
		StringBuilder sb = new StringBuilder("select examCode, name, point, avg(score) as score from st_stu_ability where classCode in (");
		for (int i = 0; i < temp.size(); i++) {
			sb.append(String.format("'%s',", temp.get(i)));
		}
		sb.setLength(sb.length() - 1);
		sb.append(") and examCode like ? group by examCode,name,point");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int pageNumber = 0;
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("examCode", rs.getObject(1));
			map.put("name", rs.getObject(2));
			map.put("point", rs.getObject(3));
			map.put("score", rs.getObject(4));
			list.add(map);
			if (list.size() == PAGE_SIZE) {
				insertUnitAbility(list, unitCode, ++pageNumber);
				list = new ArrayList<Map<String,Object>>();
			}
		}
		if (list.size() > 0) {
			insertUnitAbility(list, unitCode, ++pageNumber);
		}
		rs.close();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("statistics unit ability end : " + unitCode + "	-->" + (end - start));
		List<String> child = getChild(unitCode);
		for (String str : child) {
			unitAbility(str);
		}
	}
	public static void insertUnitAbility(List<Map<String, Object>> list, String unitCode, int pageNumber) throws Exception {
		System.out.println("insertUnitAbility " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		long s = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder("insert into st_unit_ability(examCode,unitCode,name,unitName,score,point) values");
		Record unit = units.get(unitCode);
		for (Map<String, Object> map : list) {
			sql.append(String.format("('%s','%s','%s','%s',%f,%f),", 
						map.get("examCode").toString(),
						unit.getString("unitCode"),
						map.get("name").toString(),
						unit.getString("unitName"),
						Double.valueOf(map.get("score").toString()),
						Double.valueOf(map.get("point").toString())
					));
		}
		sql.setLength(sql.length() - 1);
		Statement st = conn.createStatement();
		st.execute(sql.toString());
		st.close();
		long e = System.currentTimeMillis();
		System.out.println("insert end : " + (e - s));
	}
	

	public static void unitKnowledge(String unitCode) throws Exception {
		if (unitCode.length() > 16) {
			return;
		}
		System.out.println("statistics unit knowledge start : " + unitCode);
		long start = System.currentTimeMillis();
		List<String> temp = new ArrayList<String>();
		if (unitCode.length() == 16) {
			temp.add(unitCode);
		} else {
			getChildClass(unitCode, temp);
		}
		StringBuilder sb = new StringBuilder("select examCode, name, point, avg(score) as score from st_stu_knowledge where classCode in (");
		for (int i = 0; i < temp.size(); i++) {
			sb.append(String.format("'%s',", temp.get(i)));
		}
		sb.setLength(sb.length() - 1);
		sb.append(") and examCode like ? group by examCode,name,point");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int pageNumber = 0;
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("examCode", rs.getObject(1));
			map.put("name", rs.getObject(2));
			map.put("point", rs.getObject(3));
			map.put("score", rs.getObject(4));
			list.add(map);
			if (list.size() == PAGE_SIZE) {
				insertUnitKnowledge(list, unitCode, ++pageNumber);
				list = new ArrayList<Map<String,Object>>();
			}
		}
		if (list.size() > 0) {
			insertUnitKnowledge(list, unitCode, ++pageNumber);
		}
		rs.close();
		ps.close();
		long end = System.currentTimeMillis();
		System.out.println("statistics unit knowledge end : " + unitCode + "	-->" + (end - start));
		List<String> child = getChild(unitCode);
		for (String str : child) {
			unitKnowledge(str);
		}
	}
	public static void insertUnitKnowledge(List<Map<String, Object>> list, String unitCode, int pageNumber) throws Exception {
		System.out.println("insertUnitKnowledge " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		long s = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder("insert into st_unit_knowledge(examCode,unitCode,name,unitName,score,point) values");
		Record unit = units.get(unitCode);
		for (Map<String, Object> map : list) {
			sql.append(String.format("('%s','%s','%s','%s',%f,%f),", 
						map.get("examCode").toString(),
						unit.getString("unitCode"),
						map.get("name").toString(),
						unit.getString("unitName"),
						Double.valueOf(map.get("score").toString()),
						Double.valueOf(map.get("point").toString())
					));
		}
		sql.setLength(sql.length() - 1);
		Statement st = conn.createStatement();
		st.execute(sql.toString());
		st.close();
		long e = System.currentTimeMillis();
		System.out.println("insert end : " + (e - s));
	}
	
	
	public static List<String> getChild(String unitCode) {
		List<String> rs = new ArrayList<String>();
		for (Map.Entry<String, Record> entry : units.entrySet()) {
			if (unitCode.equals(entry.getValue().getString("parentCode"))) {
				rs.add(entry.getKey());
			}
		}
		return rs;
	}
	
	public static void getChildClass(String unitCode, List<String> list) {
		for (Map.Entry<String, Record> entry : units.entrySet()) {
			if (unitCode.equals(entry.getValue().getString("parentCode"))) {
				int temp = entry.getValue().getInt("unitTypeCode");
				String temp2 = entry.getValue().getString("unitCode");
				if (temp == 5000) {
					list.add(temp2);
				} else {
					getChildClass(temp2, list);
				}
			}
		}
	}
	
	public static void loadStudent() {
		System.out.println("load student start");
		long start = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct studentId,studentName from t_exam_student_info where examCode like @examCode");
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.records());
		sql.params().set("examCode", examCode);
		dao.execute(sql);
		List<Record> records = sql.getList(Record.class);
		for (Record record : records) {
			students.put(record.getString("studentId"), record);
		}
		long end = System.currentTimeMillis();
		System.out.println("load student end -> " + (end - start));
	}
	public static void loadExam() {
		System.out.println("load exam start");
		long start = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from t_exam_course_info where examCode like @examCode");
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.records());
		sql.params().set("examCode", examCode);
		dao.execute(sql);
		List<Record> records = sql.getList(Record.class);
		for (Record record : records) {
			exams.put(record.getString("examCode"), record);
		}
		long end = System.currentTimeMillis();
		System.out.println("load exam end -> " + (end - start));
	}
	public static void loadUnit() {
		System.out.println("load unit start");
		long start = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from t_exam_unit_query_info where queryId = @queryId");
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.records());
		sql.params().set("queryId", queryId);
		dao.execute(sql);
		List<Record> records = sql.getList(Record.class);
		for (Record record : records) {
			units.put(record.getString("unitCode"), record);
		}
		long end = System.currentTimeMillis();
		System.out.println("load unit end -> " + (end - start));
	}
}
