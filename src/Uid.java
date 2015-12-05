import java.util.Date;
import java.util.UUID;


public class Uid {
	public static void main(String[] args) {
		for (int i = 0; i < 11; i++) {
			System.out.println(UUID.randomUUID().toString().toUpperCase());
		}
	}
}
