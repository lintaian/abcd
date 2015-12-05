import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


public class PXStatistics {
	static String connString = "jdbc:jtds:sqlserver://192.168.0.200/px_chart";
	static String connUser = "sa";
	static String connPass = "13980439852";
	static String dbDriver = "net.sourceforge.jtds.jdbc.Driver";
	static Dao dao;
	static Connection conn;
	static String queryId = "C9FDC906-427A-413E-805E-34EB405A365F";
	static String examCode = "51011001150105%";
	static String unitCode = "51011001";
	
	static Map<String, Record> students = new HashMap<String, Record>();
	static Map<String, Record> exams = new HashMap<String, Record>();
	static Map<String, Record> units = new HashMap<String, Record>();
	
	static final int PAGE_SIZE = 50000;
	
	public static void main(String[] args) throws Exception {
		DruidDataSource dds = new DruidDataSource();
		dds.setDriverClassName(dbDriver);
		dds.setUrl(connString);
		dds.setUsername(connUser);
		dds.setPassword(connPass);
		dao = new NutDao(dds);
		Class.forName(dbDriver);
		conn = DriverManager.getConnection(connString, connUser, connPass);
		long s = System.currentTimeMillis();
//		clear();
//		loadStudent();
//		loadExam();
//		loadUnit();
//		unitCourse(unitCode);
//		unitAbility(unitCode);
//		unitKnowledge(unitCode);
		conn.close();
		long e = System.currentTimeMillis();
		System.out.println("this statistics total time: " + (e - s));
	}
	
	public static void clear() {
		String[] arr = {
			"truncate table st_unit_course",
			"truncate table st_unit_ability",
			"truncate table st_unit_knowledge"
		};
		System.out.println("start truncate table");
		long start = System.currentTimeMillis();
		for (String string : arr) {
			Sql sql = Sqls.create(string);
			dao.execute(sql);
		}
		long end = System.currentTimeMillis();
		System.out.println("clear end : " + unitCode + "	-->" + (end - start));
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
			sb.append(temp.get(i));
			if (i < temp.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(") and examCode like ? group by examCode");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		ps.setString(1, examCode);
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
	public static void insertUnitCourse(List<Map<String, Object>> list, String unitCode, int pageNumber) {
		System.out.println("insertUnitCourse " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		Sql sql2 = Sqls.create("insert into st_unit_course(examCode,unitCode,examName,unitName,score,point) "
				+ "values(@examCode, @unitCode, @examName, @unitName, @score,@point)");
		Record unit = units.get(unitCode);
		for (Map<String, Object> record : list) {
			sql2.params().set("examCode", record.get("examCode"));
			sql2.params().set("unitCode", unit.get("unitCode"));
			sql2.params().set("examName", exams.get(record.get("examCode")).get("examName"));
			sql2.params().set("unitName", unit.get("unitName"));
			sql2.params().set("score", record.get("score"));
			sql2.params().set("point", exams.get(record.get("examCode")).get("examPoint"));
			sql2.addBatch();
		}
		dao.execute(sql2);
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
			sb.append(temp.get(i));
			if (i < temp.size() - 1) {
				sb.append(",");
			}
		}
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
	public static void insertUnitAbility(List<Map<String, Object>> list, String unitCode, int pageNumber) {
		System.out.println("insertUnitAbility " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		Sql sql2 = Sqls.create("insert into st_unit_ability(examCode,unitCode,name,unitName,score,point) "
				+ "values(@examCode, @unitCode, @name, @unitName, @score,@point)");
		Record unit = units.get(unitCode);
		for (Map<String, Object> record : list) {
			sql2.params().set("examCode", record.get("examCode"));
			sql2.params().set("unitCode", unit.get("unitCode"));
			sql2.params().set("name", record.get("name"));
			sql2.params().set("unitName", unit.get("unitName"));
			sql2.params().set("score", record.get("score"));
			sql2.params().set("point", record.get("point"));
			sql2.addBatch();
		}
		dao.execute(sql2);
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
			sb.append(temp.get(i));
			if (i < temp.size() - 1) {
				sb.append(",");
			}
		}
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
	public static void insertUnitKnowledge(List<Map<String, Object>> list, String unitCode, int pageNumber) {
		System.out.println("insertUnitKnowledge " + (pageNumber - 1) * PAGE_SIZE + "-" + ((pageNumber-1) * PAGE_SIZE + list.size()));
		Sql sql2 = Sqls.create("insert into st_unit_knowledge(examCode,unitCode,name,unitName,score,point) "
				+ "values(@examCode, @unitCode, @name, @unitName, @score,@point)");
		Record unit = units.get(unitCode);
		for (Map<String, Object> record : list) {
			sql2.params().set("examCode", record.get("examCode"));
			sql2.params().set("unitCode", unit.get("unitCode"));
			sql2.params().set("name", record.get("name"));
			sql2.params().set("unitName", unit.get("unitName"));
			sql2.params().set("score", record.get("score"));
			sql2.params().set("point", record.get("point"));
			sql2.addBatch();
		}
		dao.execute(sql2);
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
