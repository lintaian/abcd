import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;

import com.alibaba.druid.pool.DruidDataSource;
import com.lps.tqms.db.pojo.ExamCourseInfo;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDB {
//	static String connString = "jdbc:jtds:sqlserver://121.201.7.201:21433/diagnose";
//	static String connUser = "sa";
//	static String connPass = "ansec_888";
//	static String dbDriver = "net.sourceforge.jtds.jdbc.Driver";
	static String connString = "jdbc:mysql://127.0.0.1:3309/diagnose?characterEncoding=UTF-8";
	static String connUser = "sa";
	static String connPass = "13980439852";
	static String dbDriver = "com.mysql.jdbc.Driver";
	
	static String mongoIp = "127.0.0.1";
	static int mongoPort = 27017;
	static String mongoDb = "diagnose";
	
	static Dao dao;
	static Connection conn;
	static DB db;
	static String queryId;
	static String examCode;
	static String unitCode;
	
	static Map<String, Record> units = new HashMap<String, Record>();
	static Map<String, String> stuMcls = new HashMap<String, String>();
	static Record examBatch;
	
	static final int MAX = 50000;
	
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
		Mongo mongo = new Mongo(mongoIp, mongoPort);
		db = mongo.getDB(mongoDb);
		long s = System.currentTimeMillis();
		loadExamBatch();
		loadUnit();
		loadStuMCls();
		
		fullScore();
		groupUnit();
		groupStu();
		unitCourse();
		stuCourse();
		conn.close();
		long e = System.currentTimeMillis();
		System.out.println("this statistics total time: " + (e - s));
	}
	
	public static void groupUnit() throws Exception {
		Map<String, List<Map<String, Object>>> map = sort(getUnitCourse(), "unitCode", "examCode", true);
		System.out.println("start group unit");
		long s = System.currentTimeMillis();
		DBCollection table = db.getCollection("course_group");
		List<DBObject> dbObjects = new ArrayList<DBObject>();
		for (Map.Entry<String, List<Map<String, Object>>> entry : map.entrySet()) {
			Map<String, Object> unit = entry.getValue().get(0);
			ExamTypeGroup group = ExamTypeGroup.ALL;
//			for (ExamTypeGroup group : ExamTypeGroup.getExamTypeGroups(unit.get("unitCode").toString())) {
				DBObject dbObject = new BasicDBObject();
				dbObject.put("unitCode", unit.get("unitCode"));
				dbObject.put("unitName", unit.get("unitName"));
				dbObject.put("parentCode", units.get(unit.get("unitCode").toString()).get("parentCode"));
				dbObject.put("examDate", Date.valueOf(examBatch.get("examDate").toString()));
				if (unit.get("unitCode").toString().length() == 16) {
					dbObject.put("schoolName", units.get(units.get(unit.get("unitCode").toString()).getString("parentCode")).getString("unitName"));
				}
				double score = 0d;
				List<DBObject> courses = new ArrayList<DBObject>();
				for (Map<String, Object> map2 : entry.getValue()) {
					DBObject course = new BasicDBObject();
					String examCode = map2.get("examCode").toString();
					boolean flag = false;
					for (ExamType type : group.exams) {
						if (examCode.endsWith(type.code)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						course.put("name", map2.get("examName"));
						course.put("val", fixed(map2.get("score")));
						course.put("code", map2.get("examCode"));
						courses.add(course);
						score += Double.valueOf(map2.get("score").toString());
					}
				}
				dbObject.put("course", new BasicDBObject("name", group.name).append("val", fixed(score)).
						append("code", group.code));
				dbObject.put("courses", courses);
				dbObjects.add(dbObject);
				if (dbObjects.size() > MAX) {
					table.insert(dbObjects);
					dbObjects = new ArrayList<DBObject>();
				}
//			}
		}
		if (dbObjects.size() > 0) {
			table.insert(dbObjects);
		}
		long e = System.currentTimeMillis();
		System.out.println("end group unit --> " + (e - s));
	}
	public static void groupStu() throws Exception {
		Map<String, List<Map<String, Object>>> map = sort(getStuCourse(), "studentId", "examCode", true);
		System.out.println("start group stu");
		long s = System.currentTimeMillis();
		DBCollection table = db.getCollection("course_group");
		List<DBObject> dbObjects = new ArrayList<DBObject>();
		for (Map.Entry<String, List<Map<String, Object>>> entry : map.entrySet()) {
			Map<String, Object> unit = entry.getValue().get(0);
			ExamTypeGroup group = ExamTypeGroup.ALL;
//			for (ExamTypeGroup group : ExamTypeGroup.getExamTypeGroups(unit.get("unitCode").toString())) {
				DBObject dbObject = new BasicDBObject();
				dbObject.put("unitCode", unit.get("studentId"));
				dbObject.put("unitName", unit.get("unitName"));
				dbObject.put("parentCode", unit.get("unitCode"));
				dbObject.put("examDate", Date.valueOf(examBatch.get("examDate").toString()));
				dbObject.put("className", stuMcls.get(unit.get("studentId").toString()));
				dbObject.put("schoolName", units.get(units.get(unit.get("unitCode").toString()).getString("parentCode")).getString("unitName"));
				double score = 0d;
				List<DBObject> courses = new ArrayList<DBObject>();
				for (Map<String, Object> map2 : entry.getValue()) {
					DBObject course = new BasicDBObject();
					String examCode = map2.get("examCode").toString();
					boolean flag = false;
					for (ExamType type : group.exams) {
						if (examCode.endsWith(type.code)) {
							flag = true;
							break;
						}
					}
					if (flag) {
						course.put("name", map2.get("examName"));
						course.put("val", Math.round(Double.valueOf(map2.get("score").toString())));
						course.put("code", map2.get("examCode"));
						courses.add(course);
						score += Double.valueOf(map2.get("score").toString());
					}
				}
				dbObject.put("course", new BasicDBObject("name", group.name).append("val", Math.round(score)).
						append("code", group.code));
				dbObject.put("courses", courses);
				dbObjects.add(dbObject);
				if (dbObjects.size() > MAX) {
					table.insert(dbObjects);
					dbObjects = new ArrayList<DBObject>();
				}
//			}
		}
		if (dbObjects.size() > 0) {
			table.insert(dbObjects);
		}
		long e = System.currentTimeMillis();
		System.out.println("end group stu --> " + (e - s));
	}
	public static void unitCourse() throws Exception {
		Map<String, List<Map<String, Object>>> unitCourses = sort(getUnitCourse(), "unitCode", "examCode", false);
		Map<String, List<Map<String, Object>>> unitAbilities = sort(getUnitAbility(), "unitCode", "examCode", false);
		Map<String, List<Map<String, Object>>> unitKnowledges = sort(getUnitKnowledge(), "unitCode", "examCode", false);
		System.out.println("start unit");
		long s = System.currentTimeMillis();
		DBCollection table = db.getCollection("course");
		List<DBObject> dbObjects = new ArrayList<DBObject>();
		for (Map.Entry<String, List<Map<String, Object>>> entry : unitCourses.entrySet()) {
			Map<String, Object> unit = entry.getValue().get(0);
			DBObject dbObject = new BasicDBObject();
			dbObject.put("unitCode", unit.get("unitCode"));
			dbObject.put("unitName", unit.get("unitName"));
			dbObject.put("parentCode", units.get(unit.get("unitCode").toString()).get("parentCode"));
			dbObject.put("examDate", Date.valueOf(examBatch.get("examDate").toString()));
			dbObject.put("course", new BasicDBObject("name", unit.get("examName")).
					append("val", fixed(unit.get("score"))).
					append("code", unit.get("examCode")));
			if (unit.get("unitCode").toString().length() == 16) {
				dbObject.put("schoolName", units.get(units.get(unit.get("unitCode").toString()).getString("parentCode")).getString("unitName"));
			}
			List<DBObject> ability = new ArrayList<DBObject>();
			for (Map<String, Object> map : unitAbilities.get(entry.getKey())) {
				ability.add(new BasicDBObject("name", map.get("name")).append("val", fixed(map.get("score"))).append("code", map.get("examCode")));
			}
			dbObject.put("ability", ability);
			List<DBObject> knowledge = new ArrayList<DBObject>();
			for (Map<String, Object> map : unitKnowledges.get(entry.getKey())) {
				knowledge.add(new BasicDBObject("name", map.get("name")).append("val", fixed(map.get("score"))).append("code", map.get("examCode")));
			}
			dbObject.put("knowledge", knowledge);
			dbObjects.add(dbObject);
			if (dbObjects.size() > MAX) {
				table.insert(dbObjects);
				dbObjects = new ArrayList<DBObject>();
			}
		}
		if (dbObjects.size() > 0) {
			table.insert(dbObjects);
		}
		long e = System.currentTimeMillis();
		System.out.println("end unit --> " + (e - s));
	}
	
	public static void stuCourse() throws Exception {
		Map<String, List<Map<String, Object>>> unitCourses = sort(getStuCourse(), "studentId", "examCode", false);
		Map<String, List<Map<String, Object>>> unitAbilities = sort(getStuAbility(), "studentId", "examCode", false);
		Map<String, List<Map<String, Object>>> unitKnowledges = sort(getStuKnowledge(), "studentId", "examCode", false);
		System.out.println("start stu");
		long s = System.currentTimeMillis();
		DBCollection table = db.getCollection("course");
		List<DBObject> dbObjects = new ArrayList<DBObject>();
		for (Map.Entry<String, List<Map<String, Object>>> entry : unitCourses.entrySet()) {
			Map<String, Object> unit = entry.getValue().get(0);
			DBObject dbObject = new BasicDBObject();
			dbObject.put("unitCode", unit.get("studentId"));
			dbObject.put("unitName", unit.get("unitName"));
			dbObject.put("parentCode", unit.get("unitCode"));
			dbObject.put("examDate", Date.valueOf(examBatch.get("examDate").toString()));
			dbObject.put("course", new BasicDBObject("name", unit.get("examName")).
					append("val", Math.round(Double.valueOf(unit.get("score").toString()))).
					append("code", unit.get("examCode")));
			dbObject.put("className", stuMcls.get(unit.get("studentId").toString()));
			dbObject.put("schoolName", units.get(units.get(unit.get("unitCode").toString()).getString("parentCode")).getString("unitName"));
			List<DBObject> ability = new ArrayList<DBObject>();
			for (Map<String, Object> map : unitAbilities.get(entry.getKey())) {
				ability.add(new BasicDBObject("name", map.get("name")).
						append("val", Math.round(Double.valueOf(map.get("score").toString()))).
						append("code", map.get("examCode")));
			}
			dbObject.put("ability", ability);
			List<DBObject> knowledge = new ArrayList<DBObject>();
			for (Map<String, Object> map : unitKnowledges.get(entry.getKey())) {
				knowledge.add(new BasicDBObject("name", map.get("name")).
						append("val", Math.round(Double.valueOf(map.get("score").toString()))).
						append("code", map.get("examCode")));
			}
			dbObject.put("knowledge", knowledge);
			dbObjects.add(dbObject);
			if (dbObjects.size() > MAX) {
				table.insert(dbObjects);
				dbObjects = new ArrayList<DBObject>();
			}
		}
		if (dbObjects.size() > 0) {
			table.insert(dbObjects);
		}
		long e = System.currentTimeMillis();
		System.out.println("end stu --> " + (e - s));
	}
	
	public static List<Map<String, Object>> getUnitCourse() throws Exception {
		System.out.println("start get unit course");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select unitCode,examCode,score,examName,unitName,point"
				+ " from st_unit_course where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("unitCode", rs.getObject(1));
			map.put("examCode", rs.getObject(2));
			map.put("score", rs.getObject(3));
			map.put("examName", rs.getObject(4));
			map.put("unitName", rs.getObject(5));
			map.put("point", rs.getObject(6));
			list.add(map);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get unit course --> " + (e - s));
		return list;
	}
	public static List<Map<String, Object>> getUnitAbility() throws Exception {
		System.out.println("start get unit ability");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select unitCode,examCode,score,name,unitName,point"
				+ " from st_unit_ability where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("unitCode", rs.getObject(1));
			map.put("examCode", rs.getObject(2));
			map.put("score", rs.getObject(3));
			map.put("name", rs.getObject(4));
			map.put("unitName", rs.getObject(5));
			map.put("point", rs.getObject(6));
			list.add(map);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get unit ability --> " + (e - s));
		return list;
	}
	public static List<Map<String, Object>> getUnitKnowledge() throws Exception {
		System.out.println("start get unit knowledge");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select unitCode,examCode,score,name,unitName,point"
				+ " from st_unit_knowledge where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("unitCode", rs.getObject(1));
			map.put("examCode", rs.getObject(2));
			map.put("score", rs.getObject(3));
			map.put("name", rs.getObject(4));
			map.put("unitName", rs.getObject(5));
			map.put("point", rs.getObject(6));
			list.add(map);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get unit knowledge --> " + (e - s));
		return list;
	}
	
	
	public static List<Map<String, Object>> getStuCourse() throws Exception {
		System.out.println("start get stu course");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select studentId,examCode,score,classCode,studentName,examName,point"
				+ " from st_stu_course where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("studentId", rs.getObject(1));
			map.put("examCode", rs.getObject(2));
			map.put("score", rs.getObject(3));
			map.put("unitCode", rs.getObject(4));
			map.put("unitName", rs.getObject(5));
			map.put("examName", rs.getObject(6));
			map.put("point", rs.getObject(7));
			list.add(map);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get stu course --> " + (e - s));
		return list;
	}
	public static List<Map<String, Object>> getStuAbility() throws Exception {
		System.out.println("start get stu ability");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select studentId,examCode,score,classCode,studentName,name,point"
				+ " from st_stu_ability where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("studentId", rs.getObject(1));
			map.put("examCode", rs.getObject(2));
			map.put("score", rs.getObject(3));
			map.put("unitCode", rs.getObject(4));
			map.put("unitName", rs.getObject(5));
			map.put("name", rs.getObject(6));
			map.put("point", rs.getObject(7));
			list.add(map);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get stu ability --> " + (e - s));
		return list;
	}
	public static List<Map<String, Object>> getStuKnowledge() throws Exception {
		System.out.println("start get stu knowledge");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select studentId,examCode,score,classCode,studentName,name,point"
				+ " from st_stu_knowledge where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("studentId", rs.getObject(1));
			map.put("examCode", rs.getObject(2));
			map.put("score", rs.getObject(3));
			map.put("unitCode", rs.getObject(4));
			map.put("unitName", rs.getObject(5));
			map.put("name", rs.getObject(6));
			map.put("point", rs.getObject(7));
			list.add(map);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get stu knowledge --> " + (e - s));
		return list;
	}
	
	public static Map<String, List<Map<String, Object>>> sort(List<Map<String, Object>> list, String k1, String k2, boolean isSub) {
		System.out.println("start sort");
		long s = System.currentTimeMillis();
		Map<String, List<Map<String, Object>>> rs = new HashMap<String, List<Map<String,Object>>>();
		for (Map<String, Object> map : list) {
			String key = map.get(k1).toString() + "-" + (isSub ? subString(map.get(k2).toString()) : map.get(k2).toString());
			if (rs.get(key) == null) {
				rs.put(key, new ArrayList<Map<String,Object>>());
			}
			rs.get(key).add(map);
		}
		long e = System.currentTimeMillis();
		System.out.println("end sort --> " + (e - s));
		return rs;
	}
	
	public static String subString(String examCode) {
		return examCode.substring(0, 14);
	}
	public static double fixed(Object o) {
		DecimalFormat df = new DecimalFormat("0.0");
		return Double.valueOf(df.format(o));
	}
	
	public static void loadExamBatch() {
		System.out.println("load exam start");
		long start = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from t_exam_batch_info where examCode like @examCode");
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.records());
		sql.params().set("examCode", examCode);
		dao.execute(sql);
		examBatch = sql.getList(Record.class).get(0);
		String date = examBatch.getString("examDate");
		examBatch.set("examDate", date.substring(0, date.indexOf(' ')));
		long end = System.currentTimeMillis();
		System.out.println("load examBatch end -> " + (end - start));
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
	public static void loadStuMCls() {
		StringBuilder sb = new StringBuilder();
		sb.append("select studentId,classCode from t_exam_student_info where examCode like @examCode");
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.records());
		sql.params().set("examCode", examCode);
		dao.execute(sql);
		List<Record> records = sql.getList(Record.class);
		for (Record record : records) {
			try {
				stuMcls.put(record.getString("studentId"), units.get(record.getString("classCode")).getString("unitName"));
			} catch (Exception e) {
				System.out.println(record.getString("classCode"));
			}
		}
	}
	
	public static void fullScore() {
		Map<String, Full> rs = new HashMap<String, MongoDB.Full>();
		DBCollection coll = db.getCollection("full_score");
		List<ExamCourseInfo> infos = dao.query(ExamCourseInfo.class, 
				Cnd.where("examCode", "LIKE", examCode));
		for (ExamCourseInfo info : infos) {
			Full full = new Full();
			full.code = info.getExamCode();
			full.name = info.getExamName();
			full.val = info.getExamPoint();
			rs.put(info.getExamCode(), full);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("select examCode,knowledgeName as name,SUM(questionPoint) as score from t_exam_question_info "
				+ "where examCode like @examCode group by examCode,knowledgeName");
		Sql sql = Sqls.create(sb.toString());
		sql.setCallback(Sqls.callback.records());
		sql.params().set("examCode", examCode);
		dao.execute(sql);
		List<Record> records = sql.getList(Record.class);
		for (Record record : records) {
			Full full = new Full();
			full.code = record.getString("examCode");
			full.name = record.getString("name");
			full.val = Double.valueOf(record.getString("score"));
			rs.get(record.getString("examCode")).knowledge.add(full);
		}
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("select examCode,abilityName as name,SUM(questionPoint) as score from t_exam_question_info "
				+ "where examCode like @examCode group by examCode,abilityName");
		Sql sql2 = Sqls.create(sb2.toString());
		sql2.setCallback(Sqls.callback.records());
		sql2.params().set("examCode", examCode);
		dao.execute(sql2);
		List<Record> records2 = sql2.getList(Record.class);
		for (Record record : records2) {
			Full full = new Full();
			full.code = record.getString("examCode");
			full.name = record.getString("name");
			full.val = Double.valueOf(record.getString("score"));
			rs.get(record.getString("examCode")).ability.add(full);
		}
		for (Map.Entry<String, Full> entry : rs.entrySet()) {
			Full full = entry.getValue();
			coll.insert(full.toDbObject());
		}
	}
	
	static class Full {
		public String code;
		public String name;
		public double val;
		public List<Full> ability = new ArrayList<MongoDB.Full>();
		public List<Full> knowledge = new ArrayList<MongoDB.Full>();
		public DBObject toDbObject() {
			return BasicDBObjectBuilder.start("name", name).
					add("code", code).add("val", val).
					add("ability", parse(ability)).add("knowledge", parse(knowledge)).get();
		}
		List<DBObject> parse(List<Full> fulls) {
			List<DBObject> rs = new ArrayList<DBObject>();
			for (Full full : fulls) {
				rs.add(BasicDBObjectBuilder.start("code", full.code).
						add("name", full.name).add("val", full.val).get());
			}
			return rs;
		}
	}
}
