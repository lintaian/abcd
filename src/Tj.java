import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.sql.Sql;

public class Tj {
	public static Dao dao;
	public static List<Temp> query = new ArrayList<Temp>();
	public static List<Temp2> units = new ArrayList<Temp2>();
	public static String[] colors = {"darkRedIcon","redIcon","lightRedIcon","lightGreenIcon","greenIcon","darkGreenIcon"};
//	public static String[] colors2 = {"#008000", "#00FF00", "#D8FFD8", "#FFD8D8", "#FF8C8C", "#FF0000"};
	public static String[] colors2 = {"#880000","#A60000","#C40000","#E20000","#FF0000","#FF1E1E","#FF3C3C","#FF5A5A","#FF7878","#FF9696","#FFB4B4",		
		"#B4FFB4","#96FF96","#78FF78","#5AFF5A","#3CFF3C","#1EFF1E","#00FF00" ,"#00E200","#00C400","#00A600","#008800"};
	public static int countColor = colors.length;
	public static void main(String args[]) {
		SimpleDataSource ds = new SimpleDataSource();
		ds.setJdbcUrl("jdbc:mysql://192.168.0.210:3306/pixian");
		ds.setUsername("sa");
		ds.setPassword("13980439852");
		dao = new NutDao(ds);
		buildQuery();
		buildUnit();
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		try{
		     BufferedWriter writer = new BufferedWriter(new FileWriter(new File("D:\\Result.txt")));
		     for(Temp2 u : units) {
		    	 for(Temp q : query) {
		    		 List<Score> scores = getScore(q.queryName, q.artSci, u.unit, u.groupLength, q.tableName, q.key);
		    		 rank(scores, u.isArea);
		    		 for(Score s : scores) {
		    			 writer.write(String.format("'%s_%s_%s': [%.1f,%d,%d,'%s'],\r\n", s.unitCode, s.name, u.parent, s.score, s.position, s.count, s.color));
		    		 }
		    	 }
		     }
		     writer.close();
		}catch(Exception e){
			e.printStackTrace();
	    }
	}
	public static void rank(List<Score> scores, boolean isArea) {
		Collections.sort(scores, new Comparator<Score>() {
			@Override
			public int compare(Score o1, Score o2) {
				return (int) (o2.score - o1.score);
			}
		});
		int count = scores.size();
		double derta = (scores.get(0).score - scores.get(count - 1).score) / countColor;
		double max = scores.get(0).score;
		int position = 1;
		for(Score score : scores) {
			score.count = count;
			score.position = position;
			if (isArea) {
				score.color = colors2[position - 1];
			} else {
				int p = countColor - 1;
				double s = score.score;
				for (int j = 0; j < countColor; j++) {
					if (s >= (max - derta * (j + 1))) {
						p = j;
						break;
					}
				}
				score.color = colors[p];
			}
			position++;
		}
	}
	public static List<Score> getScore(String queryName, int artSci, String unitCode, int groupLength, String tableName, String name) {
		StringBuilder sb = new StringBuilder("select SUBSTRING(class_code,1,");
		sb.append(groupLength);
		sb.append(") as unitCode, '");
		sb.append(name);
		sb.append("' as name, ROUND(avg(");
		sb.append(queryName);
		sb.append(") / 100,1) as score from ");
		sb.append(tableName);
		sb.append(" where class_code like @unitCode");
		if(artSci != 0) {
			sb.append(" and artsci=");
			sb.append(artSci);
		}
		sb.append(" group by unitCode order by score desc");
		Sql sql = Sqls.create(sb.toString());
		sql.params().set("unitCode", unitCode + "%");
		sql.setCallback(Sqls.callback.entities());
		sql.setEntity(dao.getEntity(Score.class));
		dao.execute(sql);
		return sql.getList(Score.class);
	}
	public static void buildQuery() {
		query.add(new Temp("t_5101000015070300", "score", 1, "art"));
		query.add(new Temp("t_5101000015070300", "score", 2, "sci"));
		query.add(new Temp("t_5101000015070300", "s_1", 0, "5101000015070301"));
		query.add(new Temp("t_5101000015070300", "s_3", 1, "5101000015070303"));
		query.add(new Temp("t_5101000015070300", "s_4", 2, "5101000015070304"));
		query.add(new Temp("t_5101000015070300", "s_5", 0, "5101000015070305"));
		query.add(new Temp("t_5101000015070300", "s_6", 1, "5101000015070306"));
		query.add(new Temp("t_5101000015070300", "s_7", 1, "5101000015070307"));
		query.add(new Temp("t_5101000015070300", "s_8", 1, "5101000015070308"));
		query.add(new Temp("t_5101000015070300", "s_9", 2, "5101000015070309"));
		query.add(new Temp("t_5101000015070300", "s_10", 2, "5101000015070310"));
		query.add(new Temp("t_5101000015070300", "s_11", 2, "5101000015070311"));
		
		query.add(new Temp("t_5101000015070301_ka", "k_030101", 0, "5101000015070301_k_030101"));
		query.add(new Temp("t_5101000015070301_ka", "k_030102", 0, "5101000015070301_k_030102"));
		query.add(new Temp("t_5101000015070301_ka", "k_030103", 0, "5101000015070301_k_030103"));
		query.add(new Temp("t_5101000015070301_ka", "k_030104", 0, "5101000015070301_k_030104"));
		query.add(new Temp("t_5101000015070303_ka", "k_030301", 1, "5101000015070303_k_030301"));
		query.add(new Temp("t_5101000015070303_ka", "k_030302", 1, "5101000015070303_k_030302"));
		query.add(new Temp("t_5101000015070303_ka", "k_030303", 1, "5101000015070303_k_030303"));
		query.add(new Temp("t_5101000015070303_ka", "k_030304", 1, "5101000015070303_k_030304"));
		query.add(new Temp("t_5101000015070304_ka", "k_030401", 2, "5101000015070304_k_030401"));
		query.add(new Temp("t_5101000015070304_ka", "k_030402", 2, "5101000015070304_k_030402"));
		query.add(new Temp("t_5101000015070304_ka", "k_030403", 2, "5101000015070304_k_030403"));
		query.add(new Temp("t_5101000015070304_ka", "k_030404", 2, "5101000015070304_k_030404"));
		query.add(new Temp("t_5101000015070305_ka", "k_030501", 0, "5101000015070305_k_030501"));
		query.add(new Temp("t_5101000015070305_ka", "k_030502", 0, "5101000015070305_k_030502"));
		query.add(new Temp("t_5101000015070305_ka", "k_030503", 0, "5101000015070305_k_030503"));
		query.add(new Temp("t_5101000015070305_ka", "k_030505", 0, "5101000015070305_k_030505"));
		query.add(new Temp("t_5101000015070306_ka", "k_030603", 1, "5101000015070306_k_030603"));
		query.add(new Temp("t_5101000015070306_ka", "k_030604", 1, "5101000015070306_k_030604"));
		query.add(new Temp("t_5101000015070307_ka", "k_030701", 1, "5101000015070307_k_030701"));
		query.add(new Temp("t_5101000015070307_ka", "k_030702", 1, "5101000015070307_k_030702"));
		query.add(new Temp("t_5101000015070307_ka", "k_030703", 1, "5101000015070307_k_030703"));
		query.add(new Temp("t_5101000015070307_ka", "k_030704", 1, "5101000015070307_k_030704"));
		query.add(new Temp("t_5101000015070307_ka", "k_030705", 1, "5101000015070307_k_030705"));
		query.add(new Temp("t_5101000015070308_ka", "k_030801", 1, "5101000015070308_k_030801"));
		query.add(new Temp("t_5101000015070308_ka", "k_030802", 1, "5101000015070308_k_030802"));
		query.add(new Temp("t_5101000015070308_ka", "k_030806", 1, "5101000015070308_k_030806"));
		query.add(new Temp("t_5101000015070309_ka", "k_030901", 2, "5101000015070309_k_030901"));
		query.add(new Temp("t_5101000015070309_ka", "k_030902", 2, "5101000015070309_k_030902"));
		query.add(new Temp("t_5101000015070309_ka", "k_030903", 2, "5101000015070309_k_030903"));
		query.add(new Temp("t_5101000015070309_ka", "k_030906", 2, "5101000015070309_k_030906"));
		query.add(new Temp("t_5101000015070310_ka", "k_031002", 2, "5101000015070310_k_031002"));
		query.add(new Temp("t_5101000015070310_ka", "k_031004", 2, "5101000015070310_k_031004"));
		query.add(new Temp("t_5101000015070311_ka", "k_031101", 2, "5101000015070311_k_031101"));
		query.add(new Temp("t_5101000015070311_ka", "k_031103", 2, "5101000015070311_k_031103"));
		query.add(new Temp("t_5101000015070311_ka", "k_031104", 2, "5101000015070311_k_031104"));
		query.add(new Temp("t_5101000015070311_ka", "k_031106", 2, "5101000015070311_k_031106"));
		
		query.add(new Temp("t_5101000015070301_ka", "a_030101", 0, "5101000015070301_a_030101"));
		query.add(new Temp("t_5101000015070301_ka", "a_030102", 0, "5101000015070301_a_030102"));
		query.add(new Temp("t_5101000015070301_ka", "a_030103", 0, "5101000015070301_a_030103"));
		query.add(new Temp("t_5101000015070301_ka", "a_030104", 0, "5101000015070301_a_030104"));
		query.add(new Temp("t_5101000015070301_ka", "a_030105", 0, "5101000015070301_a_030105"));
		query.add(new Temp("t_5101000015070301_ka", "a_030106", 0, "5101000015070301_a_030106"));
		query.add(new Temp("t_5101000015070303_ka", "a_030301", 1, "5101000015070303_a_030301"));
		query.add(new Temp("t_5101000015070303_ka", "a_030302", 1, "5101000015070303_a_030302"));
		query.add(new Temp("t_5101000015070303_ka", "a_030303", 1, "5101000015070303_a_030303"));
		query.add(new Temp("t_5101000015070304_ka", "a_030401", 2, "5101000015070304_a_030401"));
		query.add(new Temp("t_5101000015070304_ka", "a_030402", 2, "5101000015070304_a_030402"));
		query.add(new Temp("t_5101000015070304_ka", "a_030403", 2, "5101000015070304_a_030403"));
		query.add(new Temp("t_5101000015070305_ka", "a_030502", 0, "5101000015070305_a_030502"));
		query.add(new Temp("t_5101000015070305_ka", "a_030503", 0, "5101000015070305_a_030503"));
		query.add(new Temp("t_5101000015070306_ka", "a_030601", 1, "5101000015070306_a_030601"));
		query.add(new Temp("t_5101000015070306_ka", "a_030602", 1, "5101000015070306_a_030602"));
		query.add(new Temp("t_5101000015070306_ka", "a_030603", 1, "5101000015070306_a_030603"));
		query.add(new Temp("t_5101000015070307_ka", "a_030701", 1, "5101000015070307_a_030701"));
		query.add(new Temp("t_5101000015070307_ka", "a_030702", 1, "5101000015070307_a_030702"));
		query.add(new Temp("t_5101000015070307_ka", "a_030703", 1, "5101000015070307_a_030703"));
		query.add(new Temp("t_5101000015070308_ka", "a_030801", 1, "5101000015070308_a_030801"));
		query.add(new Temp("t_5101000015070308_ka", "a_030802", 1, "5101000015070308_a_030802"));
		query.add(new Temp("t_5101000015070308_ka", "a_030803", 1, "5101000015070308_a_030803"));
		query.add(new Temp("t_5101000015070309_ka", "a_030901", 2, "5101000015070309_a_030901"));
		query.add(new Temp("t_5101000015070309_ka", "a_030902", 2, "5101000015070309_a_030902"));
		query.add(new Temp("t_5101000015070309_ka", "a_030904", 2, "5101000015070309_a_030904"));
		query.add(new Temp("t_5101000015070310_ka", "a_031001", 2, "5101000015070310_a_031001"));
		query.add(new Temp("t_5101000015070310_ka", "a_031002", 2, "5101000015070310_a_031002"));
		query.add(new Temp("t_5101000015070310_ka", "a_031003", 2, "5101000015070310_a_031003"));
		query.add(new Temp("t_5101000015070310_ka", "a_031004", 2, "5101000015070310_a_031004"));
		query.add(new Temp("t_5101000015070311_ka", "a_031102", 2, "5101000015070311_a_031102"));
		query.add(new Temp("t_5101000015070311_ka", "a_031103", 2, "5101000015070311_a_031103"));
		query.add(new Temp("t_5101000015070311_ka", "a_031104", 2, "5101000015070311_a_031104"));
	}
	public static void buildUnit() {
		units.add(new Temp2("5101", 6, 1, true));
		units.add(new Temp2("5101", 8, 2, false));
		units.add(new Temp2("510101", 8, 1, false));
		units.add(new Temp2("510104", 8, 1, false));
		units.add(new Temp2("510105", 8, 1, false));
		units.add(new Temp2("510106", 8, 1, false));
		units.add(new Temp2("510107", 8, 1, false));
		units.add(new Temp2("510108", 8, 1, false));
		units.add(new Temp2("510109", 8, 1, false));
		units.add(new Temp2("510110", 8, 1, false));
		units.add(new Temp2("510112", 8, 1, false));
		units.add(new Temp2("510113", 8, 1, false));
		units.add(new Temp2("510114", 8, 1, false));
		units.add(new Temp2("510115", 8, 1, false));
		units.add(new Temp2("510122", 8, 1, false));
		units.add(new Temp2("510124", 8, 1, false));
		units.add(new Temp2("510121", 8, 1, false));
		units.add(new Temp2("510129", 8, 1, false));
		units.add(new Temp2("510131", 8, 1, false));
		units.add(new Temp2("510132", 8, 1, false));
		units.add(new Temp2("510181", 8, 1, false));
		units.add(new Temp2("510182", 8, 1, false));
		units.add(new Temp2("510183", 8, 1, false));
		units.add(new Temp2("510184", 8, 1, false));
	}
	static class Temp {
		String tableName;
		String queryName;
		int artSci;
		String key;
		public Temp(String tableName, String queryName, int artSci, String key) {
			super();
			this.tableName = tableName;
			this.queryName = queryName;
			this.artSci = artSci;
			this.key = key;
		}
	}
	static class Temp2 {
		String unit;
		int groupLength;
		int parent;
		boolean isArea;
		public Temp2() {
			super();
		}
		public Temp2(String unit, int groupLength, int parent, boolean isArea) {
			super();
			this.unit = unit;
			this.groupLength = groupLength;
			this.parent = parent;
			this.isArea = isArea;
		}
	}
	static class Score {
		String unitCode;
		double score;
		int position;
		int count;
		String color;
		String name;
		public Score() {
			super();
		}
		public Score(String unitCode, double score) {
			super();
			this.unitCode = unitCode;
			this.score = score;
		}
		public Score(String unitCode, double score, int position, int count,
				String color, String name) {
			super();
			this.unitCode = unitCode;
			this.score = score;
			this.position = position;
			this.count = count;
			this.color = color;
			this.name = name;
		}
		public void print() {
			System.out.printf("[%s][%f][%d][%d][%s][%s]", unitCode, score, position, count, color, name);
			System.out.println();
		}
	}
}
