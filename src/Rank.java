import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;


public class Rank {
	public String unitCode = "";
	public String unitName = "";
	public String examBatchName = "";
	public String examBatchCode = "";
	public Date examDate = new Date(System.currentTimeMillis());
	public String examCode = "";
	public String examName = "";
	public String name = "";
	public List<RankChild> units = new ArrayList<RankChild>();
	
	public DBObject toDbObject() {
		return BasicDBObjectBuilder.start("unitCode", unitCode).
				append("unitName", unitName).
				append("examBatchName", examBatchName).
				append("examBatchCode", examBatchCode).
				append("examDate", examDate).
				append("examCode", examCode).
				append("examName", examName).
				append("name", name).
				append("units", parse(units)).get();
	}
	private List<DBObject> parse(List<RankChild> units) {
		List<DBObject> rs = new ArrayList<DBObject>();
		for (RankChild rankChild : units) {
			rs.add(rankChild.toDbObject());
		}
		return rs;
	}
}
