import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;

import com.alibaba.druid.pool.DruidDataSource;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDBRank {
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
	static int[] unitTypeCodes = {2001, 3000, 4000, 5000};
	static int stuUnitTypeCode = 6000;

	
	static Map<String, Record> units = new HashMap<String, Record>();
	static Map<String, Record> exams = new HashMap<String, Record>();
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
		loadExam();
		
		rankStuCourse();
		rankStuAbility();
		rankStuKnowledge();
		rankStuGroup();
		rankUnitCourse();
		rankUnitAbility();
		rankUnitKnowledge();
		rankUnitGroup();
		conn.close();
		long e = System.currentTimeMillis();
		System.out.println("this statistics total time: " + (e - s));
	}
	public static void rankUnitGroup() throws Exception {
		System.out.println("start unit course group rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getUnitCourse();
		Map<String, List<MongoDto>> map = sort(source);
		source = new ArrayList<MongoDto>();
		for (Map.Entry<String, List<MongoDto>> entry : map.entrySet()) {
			ExamTypeGroup group = ExamTypeGroup.ALL;
//			for (ExamTypeGroup group : ExamTypeGroup.getExamTypeGroups(entry.getValue().get(0).classCode)) {
				MongoDto dto = new MongoDto();
				dto.classCode = entry.getValue().get(0).classCode;
				dto.examCode = String.valueOf(group.code);
				dto.examName = group.name;
				dto.unitCode = entry.getKey();
				dto.unitName = entry.getValue().get(0).unitName;
				for (ExamType type : group.exams) {
					for (MongoDto mongoDto : entry.getValue()) {
						if (mongoDto.examCode.endsWith(type.code)) {
							dto.score += mongoDto.score;
							dto.point += mongoDto.point;
							break;
						}
					}
				}
				source.add(dto);
//			}
		}
		ranking(source);
		for (int unitType : unitTypeCodes) {
			Map<String, Rank> rs = new HashMap<String, Rank>();
			rankStu(unitCode, source, rs, unitType);
			insertRank(rs);
		}
		long e = System.currentTimeMillis();
		System.out.println("ent unit course group rank: " + (e - s));
	}
	public static void rankUnitCourse() throws Exception {
		System.out.println("start unit course rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getUnitCourse();
		ranking(source);
		for (int unitType : unitTypeCodes) {
			Map<String, Rank> rs = new HashMap<String, Rank>();
			rankStu(unitCode, source, rs, unitType);
			insertRank(rs);
		}
		long e = System.currentTimeMillis();
		System.out.println("ent unit course rank: " + (e - s));
	}
	public static void rankUnitAbility() throws Exception {
		System.out.println("start unit ability rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getUnitAbility();
		ranking(source);
		for (int unitType : unitTypeCodes) {
			Map<String, Rank> rs = new HashMap<String, Rank>();
			rankStu(unitCode, source, rs, unitType);
			insertRank(rs);
		}
		long e = System.currentTimeMillis();
		System.out.println("ent unit ability rank: " + (e - s));
	}
	public static void rankUnitKnowledge() throws Exception {
		System.out.println("start unit knowledge rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getUnitKnowledge();
		ranking(source);
		for (int unitType : unitTypeCodes) {
			Map<String, Rank> rs = new HashMap<String, Rank>();
			rankStu(unitCode, source, rs, unitType);
			insertRank(rs);
		}
		long e = System.currentTimeMillis();
		System.out.println("ent unit knowledge rank: " + (e - s));
	}
	
	public static void rankStuGroup() throws Exception {
		System.out.println("start student course group rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getStuCourse();
		Map<String, List<MongoDto>> map = sort(source);
		source = new ArrayList<MongoDto>();
		for (Map.Entry<String, List<MongoDto>> entry : map.entrySet()) {
			ExamTypeGroup group = ExamTypeGroup.ALL;
//			for (ExamTypeGroup group : ExamTypeGroup.getExamTypeGroups(entry.getValue().get(0).classCode)) {
				MongoDto dto = new MongoDto();
				dto.classCode = entry.getValue().get(0).classCode;
				dto.examCode = String.valueOf(group.code);
				dto.examName = group.name;
				dto.unitCode = entry.getKey();
				dto.unitName = entry.getValue().get(0).unitName;
				for (ExamType type : group.exams) {
					for (MongoDto mongoDto : entry.getValue()) {
						if (mongoDto.examCode.endsWith(type.code)) {
							dto.score += mongoDto.score;
							dto.point += mongoDto.point;
							break;
						}
					}
				}
				source.add(dto);
//			}
		}
		ranking(source);
		Map<String, Rank> rs = new HashMap<String, Rank>();
		rankStu(unitCode, source, rs, stuUnitTypeCode);
		insertRank(rs);
		long e = System.currentTimeMillis();
		System.out.println("ent student course group rank: " + (e - s));
	}
	public static void rankStuCourse() throws Exception {
		System.out.println("start student course rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getStuCourse();
		ranking(source);
		Map<String, Rank> rs = new HashMap<String, Rank>();
		rankStu(unitCode, source, rs, stuUnitTypeCode);
		insertRank(rs);
		long e = System.currentTimeMillis();
		System.out.println("ent student course rank: " + (e - s));
	}
	public static void rankStuAbility() throws Exception {
		System.out.println("start student ability rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getStuAbility();
		ranking(source);
		Map<String, Rank> rs = new HashMap<String, Rank>();
		rankStu(unitCode, source, rs, stuUnitTypeCode);
		insertRank(rs);
		long e = System.currentTimeMillis();
		System.out.println("ent student ability rank: " + (e - s));
	}
	public static void rankStuKnowledge() throws Exception {
		System.out.println("start student knowledge rank");
		long s = System.currentTimeMillis();
		List<MongoDto> source = getStuKnowledge();
		ranking(source);
		Map<String, Rank> rs = new HashMap<String, Rank>();
		rankStu(unitCode, source, rs, stuUnitTypeCode);
		insertRank(rs);
		long e = System.currentTimeMillis();
		System.out.println("ent student knowledge rank: " + (e - s));
	}
	public static void insertRank(Map<String, Rank> source) {
//		System.out.println("start insert rank");
//		long s = System.currentTimeMillis();
		DBCollection table = db.getCollection("rank");
		List<DBObject> list = new ArrayList<DBObject>();
		for (Map.Entry<String, Rank> entry : source.entrySet()) {
			list.add(entry.getValue().toDbObject());
			if (list.size() > MAX) {
				table.insert(list);
				list = new ArrayList<DBObject>();
			}
		}
		if (list.size() > 0) {
			table.insert(list);
		}
//		long e = System.currentTimeMillis();
//		System.out.println("ent insert rank: " + (e - s));
	}
	public static void rankStu(String unitCode, List<MongoDto> source, Map<String, Rank> rs, int childType) throws Exception {
		List<String> childUnits = new ArrayList<String>();
		getUnitChild(unitCode, childType, childUnits);
		List<MongoDto> courses = getChild(source, childUnits);
		Map<String, Integer> counts = getCount(courses);
		Map<String, Integer> positions = new HashMap<String, Integer>();
		Map<String, Integer> positionsInc = new HashMap<String, Integer>();
		Map<String, Double> scores = new HashMap<String, Double>();
		for (MongoDto dto : courses) {
			String key = dto.unitCode + dto.examCode + dto.name;
			if (!rs.containsKey(key)) {
				Rank rank = new Rank();
				rank.unitCode = dto.unitCode;
				rank.unitName = dto.unitName;
				rank.examCode = dto.examCode;
				rank.examName = dto.examName;
				rank.name = dto.name;
				rank.examBatchName = examBatch.getString("examName");
				rank.examBatchCode = examBatch.getString("examCode");
				rank.examDate = Date.valueOf(examBatch.get("examDate").toString());
				rs.put(key, rank);
			}
			String pKey = dto.examCode + dto.name;
			if (!positions.containsKey(pKey)) {
				positions.put(pKey, 1);
				positionsInc.put(pKey, 1);
				scores.put(pKey, dto.score);
			} else {
				positionsInc.put(pKey, positionsInc.get(pKey) + 1);
				if (dto.score < scores.get(pKey)) {
					positions.put(pKey, positionsInc.get(pKey));
					scores.put(pKey, dto.score);
				}
			}
			RankChild child = new RankChild();
			child.position = positions.get(pKey) + "/" + counts.get(pKey);
			child.unitCode = unitCode;
			child.unitName = units.get(unitCode).getString("unitName");
			child.ranking = fixed((1 - positions.get(pKey) * 1.0 / counts.get(pKey)) * 100, "0.00");
//			if (counts.get(pKey) > 1) {
//				child.ranking = fixed((counts.get(pKey) - positions.get(pKey)) * 1.0 / (counts.get(pKey) - 1) * 100, "0.00");
//			} else {
//				child.ranking = 100;
//			}
			rs.get(key).units.add(child);
		}
		List<String> childs = getChild(unitCode, childType);
		for (String string : childs) {
			rankStu(string, courses, rs, childType);
		}
	}
	
	public static List<MongoDto> getChild(List<MongoDto> source, List<String> classes) {
//		System.out.println("start get child");
//		long s = System.currentTimeMillis();
		List<MongoDto> rs = new ArrayList<MongoDto>();
		for (MongoDto map : source) {
			for (String cls : classes) {
				if (cls.equals(map.unitCode) || cls.equals(map.classCode)) {
					rs.add(map);
					break;
				}
			}
		}
//		long e = System.currentTimeMillis();
//		System.out.println("end get child --> " + (e - s));
		return rs;
	}
	public static Map<String, Integer> getCount(List<MongoDto> dtos) {
//		System.out.println("start get count");
//		long s = System.currentTimeMillis();
		Map<String, Integer> rs = new HashMap<String, Integer>();
		for (MongoDto dto : dtos) {
			String key = dto.examCode + dto.name;
			if (!rs.containsKey(key)) {
				rs.put(key, 0);
			}
			rs.put(key, rs.get(key) + 1);
		}
//		long e = System.currentTimeMillis();
//		System.out.println("end get count--> " + (e - s));
		return rs;
	}
	
	public static void ranking(List<MongoDto> list) {
//		System.out.println("start ranking");
//		long s = System.currentTimeMillis();
		Collections.sort(list, new Comparator<MongoDto>() {
			@Override
			public int compare(MongoDto o1, MongoDto o2) {
				return o2.score > o1.score ? 1 : o2.score == o1.score ? 0 : -1;
			}
		});
//		long e = System.currentTimeMillis();
//		System.out.println("end ranking --> " + (e - s));
	}
	
	public static String subString(String examCode) {
		return examCode.substring(0, 14);
	}
	public static double fixed(Object o, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return Double.valueOf(df.format(o));
	}
	public static void getClasses(String unitCode, List<String> rs) {
		if (unitCode.length() == 16) {
			rs.add(unitCode);
			return;
		}
		for (Map.Entry<String, Record> entry : units.entrySet()) {
			if (unitCode.equals(entry.getValue().get("parentCode"))) {
				if (Integer.parseInt(entry.getValue().get("unitTypeCode").toString()) == 5000) {
					rs.add(entry.getKey());
				} else {
					getClasses(entry.getKey(), rs);
				}
			}
		}
	}
	public static void getUnitChild(String unitCode, int childType, List<String> rs) {
		if (childType == stuUnitTypeCode && unitCode.length() == 16) {
			rs.add(unitCode);
			return;
		}
		for (Map.Entry<String, Record> entry : units.entrySet()) {
			if (unitCode.equals(entry.getValue().get("parentCode"))) {
				if (entry.getValue().getInt("unitTypeCode") == childType) {
					rs.add(entry.getKey());
				} else {
					getUnitChild(entry.getKey(), childType, rs);
				}
			}
		}
	}
	public static List<String> getChild(String unitCode, int childType) {
		List<String> rs = new ArrayList<String>();
		for (Map.Entry<String, Record> entry : units.entrySet()) {
			if (unitCode.equals(entry.getValue().get("parentCode"))) {
				if (entry.getValue().getInt("unitTypeCode") != childType) {
					rs.add(entry.getKey());
				}
			}
		}
		return rs;
	}
	public static Map<String, List<MongoDto>> sort(List<MongoDto> list) {
//		System.out.println("start sort");
//		long s = System.currentTimeMillis();
		Map<String, List<MongoDto>> rs = new HashMap<String, List<MongoDto>>();
		for (MongoDto dto : list) {
			String key = dto.unitCode;
			if (rs.get(key) == null) {
				rs.put(key, new ArrayList<MongoDto>());
			}
			rs.get(key).add(dto);
		}
//		long e = System.currentTimeMillis();
//		System.out.println("end sort --> " + (e - s));
		return rs;
	}
	
	public static List<MongoDto> getUnitCourse() throws Exception {
		System.out.println("start get unit course");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select unitCode,examCode,score,examName,unitName,point"
				+ " from st_unit_course where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<MongoDto> list = new ArrayList<MongoDto>();
		while (rs.next()) {
			MongoDto dto = new MongoDto();
			dto.unitCode = rs.getString(1);
			dto.examCode = rs.getString(2);
			dto.score = rs.getDouble(3);
			dto.examName = rs.getString(4);
			dto.unitName = rs.getString(5);
			dto.point = rs.getDouble(6);
			list.add(dto);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get unit course --> " + (e - s));
		return list;
	}
	public static List<MongoDto> getUnitAbility() throws Exception {
		System.out.println("start get unit ability");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select unitCode,examCode,score,name,unitName,point"
				+ " from st_unit_ability where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<MongoDto> list = new ArrayList<MongoDto>();
		while (rs.next()) {
			MongoDto dto = new MongoDto();
			dto.unitCode = rs.getString(1);
			dto.examCode = rs.getString(2);
			dto.score = rs.getDouble(3);
			dto.name = rs.getString(4);
			dto.unitName = rs.getString(5);
			dto.point = rs.getDouble(6);
			dto.examName = exams.get(rs.getString(2)).getString("examName");
			list.add(dto);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get unit ability --> " + (e - s));
		return list;
	}
	public static List<MongoDto> getUnitKnowledge() throws Exception {
		System.out.println("start get unit knowledge");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select unitCode,examCode,score,name,unitName,point"
				+ " from st_unit_knowledge where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<MongoDto> list = new ArrayList<MongoDto>();
		while (rs.next()) {
			MongoDto dto = new MongoDto();
			dto.unitCode = rs.getString(1);
			dto.examCode = rs.getString(2);
			dto.score = rs.getDouble(3);
			dto.name = rs.getString(4);
			dto.unitName = rs.getString(5);
			dto.point = rs.getDouble(6);
			dto.examName = exams.get(rs.getString(2)).getString("examName");
			list.add(dto);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get unit knowledge --> " + (e - s));
		return list;
	}
	
	
	public static List<MongoDto> getStuCourse() throws Exception {
		System.out.println("start get stu course");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select studentId,examCode,score,classCode,studentName,examName,point"
				+ " from st_stu_course where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<MongoDto> list = new ArrayList<MongoDto>();
		while (rs.next()) {
			MongoDto dto = new MongoDto();
			dto.unitCode = rs.getString(1);
			dto.examCode = rs.getString(2);
			dto.score = rs.getDouble(3);
			dto.classCode = rs.getString(4);
			dto.unitName = rs.getString(5);
			dto.examName = rs.getString(6);
			dto.point = rs.getDouble(7);
			list.add(dto);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get stu course --> " + (e - s));
		return list;
	}
	public static List<MongoDto> getStuAbility() throws Exception {
		System.out.println("start get stu ability");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select studentId,examCode,score,classCode,studentName,name,point"
				+ " from st_stu_ability where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<MongoDto> list = new ArrayList<MongoDto>();
		while (rs.next()) {
			MongoDto dto = new MongoDto();
			dto.unitCode = rs.getString(1);
			dto.examCode = rs.getString(2);
			dto.score = rs.getDouble(3);
			dto.classCode = rs.getString(4);
			dto.unitName = rs.getString(5);
			dto.name = rs.getString(6);
			dto.point = rs.getDouble(7);
			dto.examName = exams.get(rs.getString(2)).getString("examName");
			list.add(dto);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get stu ability --> " + (e - s));
		return list;
	}
	public static List<MongoDto> getStuKnowledge() throws Exception {
		System.out.println("start get stu knowledge");
		long s = System.currentTimeMillis();
		PreparedStatement ps = conn.prepareStatement("select studentId,examCode,score,classCode,studentName,name,point"
				+ " from st_stu_knowledge where examCode like ?");
		ps.setString(1, examCode);
		ResultSet rs = ps.executeQuery();
		List<MongoDto> list = new ArrayList<MongoDto>();
		while (rs.next()) {
			MongoDto dto = new MongoDto();
			dto.unitCode = rs.getString(1);
			dto.examCode = rs.getString(2);
			dto.score = rs.getDouble(3);
			dto.classCode = rs.getString(4);
			dto.unitName = rs.getString(5);
			dto.name = rs.getString(6);
			dto.point = rs.getDouble(7);
			dto.examName = exams.get(rs.getString(2)).getString("examName");
			list.add(dto);
		}
		long e = System.currentTimeMillis();
		System.out.println("end get stu knowledge --> " + (e - s));
		return list;
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
}
