import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;


public class RankChild {
	public String unitCode = "";
	public String unitName = "";
	public String position = "";
	public double ranking = 0d;
	
	public DBObject toDbObject() {
		return BasicDBObjectBuilder.start("unitCode", unitCode).
				append("unitName", unitName).
				append("position", position).
				append("ranking", ranking).get();
	}
}
