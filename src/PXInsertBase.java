import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;

import com.alibaba.druid.pool.DruidDataSource;


public class PXInsertBase {
	static String connString = "jdbc:jtds:sqlserver://192.168.0.200/px_chart";
	static String connUser = "sa";
	static String connPass = "13980439852";
	static String dbDriver = "net.sourceforge.jtds.jdbc.Driver";
	static Dao dao;
	static Connection conn;
	static String queryId = "C9FDC906-427A-413E-805E-34EB405A365F";
	
	static Map<String, UUID> uuids = new HashMap<String, UUID>();
	
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
//		insertBatch();
//		insertCourse();
//		insertUnitQuery();
//		insertStudent();
//		insertScore();
		conn.close();
		long e = System.currentTimeMillis();
		System.out.println("this insert total time: " + (e - s));
	}
	
	public static void clear() {
		String[] arr = {
				"truncate table t_exam_batch_info",
				"truncate table t_exam_course_info",
				"truncate table t_exam_student_info",
				"truncate table t_exam_unit_query_info",
				"truncate table st_stu_course",
				"truncate table st_stu_ability",
				"truncate table st_stu_knowledge"
		};
		System.out.println("start truncate table");
		long start = System.currentTimeMillis();
		for (String string : arr) {
			Sql sql = Sqls.create(string);
			dao.execute(sql);
		}
		long end = System.currentTimeMillis();
		System.out.println("clear end : " + "	-->" + (end - start));
	}
	public static void insertBatch() {
		List<List<Object>> lists = readTxtFile("e:/px/batch.txt");
		Sql sql = Sqls.create("insert into t_exam_batch_info(examCode,examName) "
				+ "values(@examCode, @examName)");
		for (List<Object> list : lists) {
			sql.params().set("examCode", list.get(0));
			sql.params().set("examName", list.get(1));
			sql.addBatch();
		}
		dao.execute(sql);
	}
	public static void insertCourse() {
		List<List<Object>> lists = readTxtFile("e:/px/course.txt");
		Sql sql = Sqls.create("insert into t_exam_course_info(examCode,examName, examPoint) "
				+ "values(@examCode, @examName, @examPoint)");
		for (List<Object> list : lists) {
			sql.params().set("examCode", list.get(0));
			sql.params().set("examName", list.get(1));
			sql.params().set("examPoint", 120);
			sql.addBatch();
		}
		dao.execute(sql);
	}
	public static void insertStudent() {
		List<List<Object>> lists = readTxtFile("e:/px/student.txt");
		Sql sql = Sqls.create("insert into t_exam_student_info(studentId, studentName, classCode, examCode, examNo) "
				+ "values(@studentId, @studentName, @classCode, @examCode, @examNo)");
		for (List<Object> list : lists) {
			UUID studentId = UUID.randomUUID();
			uuids.put(list.get(3).toString(), studentId);
			sql.params().set("studentId", studentId);
			sql.params().set("studentName", list.get(0));
			sql.params().set("classCode", list.get(1));
			sql.params().set("examCode", list.get(2));
			sql.params().set("examNo", list.get(3));
			sql.addBatch();
		}
		dao.execute(sql);
	}
	
	public static void insertUnitQuery() {
		List<List<Object>> lists = readTxtFile("e:/px/unit_query.txt");
		Sql sql = Sqls.create("insert into t_exam_unit_query_info(queryId, unitCode, unitName, parentCode, unitTypeCode, unitTypeName) "
				+ "values(@queryId, @unitCode, @unitName, @parentCode, @unitTypeCode, @unitTypeName)");
		for (List<Object> list : lists) {
			sql.params().set("queryId", queryId);
			sql.params().set("unitCode", list.get(0));
			sql.params().set("unitName", list.get(1));
			sql.params().set("parentCode", list.get(2));
			sql.params().set("unitTypeCode", list.get(3));
			sql.params().set("unitTypeName", list.get(4));
			sql.addBatch();
		}
		dao.execute(sql);
	}
	public static void insertScore() {
		List<List<Object>> lists = readTxtFile("e:/px/score.txt");
		Sql sql = Sqls.create("insert into st_stu_course(studentId, examCode, score, classCode, studentName, examName, point) "
				+ "values(@studentId, @examCode, @score, @classCode, @studentName, @examName, @point)");
		Sql sql2 = Sqls.create("insert into st_stu_ability(studentId, examCode, score, classCode, studentName, name, point) "
				+ "values(@studentId, @examCode, @score, @classCode, @studentName, @name, @point)");
		Sql sql3 = Sqls.create("insert into st_stu_knowledge(studentId, examCode, score, classCode, studentName, name, point) "
				+ "values(@studentId, @examCode, @score, @classCode, @studentName, @name, @point)");
		for (List<Object> list : lists) {
			String examCode = list.get(11).toString().substring(0, 14);
			sql.params().set("studentId", uuids.get(list.get(0).toString()));
			sql.params().set("examCode", examCode + "01");
			sql.params().set("score", list.get(6));
			sql.params().set("classCode", list.get(10));
			sql.params().set("studentName", list.get(1));
			sql.params().set("examName", "语文");
			sql.params().set("point", 120);
			sql.addBatch();
			sql.params().set("studentId", uuids.get(list.get(0).toString()));
			sql.params().set("examCode", examCode + "02");
			sql.params().set("score", list.get(9));
			sql.params().set("classCode", list.get(10));
			sql.params().set("studentName", list.get(1));
			sql.params().set("examName", "数学");
			sql.params().set("point", 120);
			sql.addBatch();
			
			sql2.params().set("studentId", uuids.get(list.get(0).toString()));
			sql2.params().set("examCode", examCode + "01");
			sql2.params().set("score", list.get(2));
			sql2.params().set("classCode", list.get(10));
			sql2.params().set("studentName", list.get(1));
			sql2.params().set("name", "表现性评价得分");
			sql2.params().set("point", 30);
			sql2.addBatch();
			sql2.params().set("studentId", uuids.get(list.get(0).toString()));
			sql2.params().set("examCode", examCode + "01");
			sql2.params().set("score", list.get(3));
			sql2.params().set("classCode", list.get(10));
			sql2.params().set("studentName", list.get(1));
			sql2.params().set("name", "纸笔测试得分");
			sql2.params().set("point", 70);
			sql2.addBatch();
			sql2.params().set("studentId", uuids.get(list.get(0).toString()));
			sql2.params().set("examCode", examCode + "02");
			sql2.params().set("score", list.get(7));
			sql2.params().set("classCode", list.get(10));
			sql2.params().set("studentName", list.get(1));
			sql2.params().set("name", "A卷");
			sql2.params().set("point", 100);
			sql2.addBatch();
			
			sql3.params().set("studentId", uuids.get(list.get(0).toString()));
			sql3.params().set("examCode", examCode + "01");
			sql3.params().set("score", list.get(5));
			sql3.params().set("classCode", list.get(10));
			sql3.params().set("studentName", list.get(1));
			sql3.params().set("name", "B卷");
			sql3.params().set("point", 20);
			sql3.addBatch();
			sql3.params().set("studentId", uuids.get(list.get(0).toString()));
			sql3.params().set("examCode", examCode + "02");
			sql3.params().set("score", list.get(8));
			sql3.params().set("classCode", list.get(10));
			sql3.params().set("studentName", list.get(1));
			sql3.params().set("name", "B卷");
			sql3.params().set("point", 20);
			sql3.addBatch();
		}
		dao.execute(sql);
		dao.execute(sql2);
		dao.execute(sql3);
	}
	
	
	private static List<List<Object>> readTxtFile(String filePath){
		List<List<Object>> rs = new ArrayList<List<Object>>();
        try {
            String encoding="utf-8";
            File file = new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	if (lineTxt != null && !"".equals(lineTxt)) {
                		String[] arr = lineTxt.split(",");
            			List<Object> list = new ArrayList<Object>();
            			for (int i = 0; i < arr.length; i++) {
							list.add(arr[i]);
						}
            			rs.add(list);
					}
                }
                read.close();
	        }else{
	            System.out.println("找不到指定的文件");
	        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return rs;
    }
}
