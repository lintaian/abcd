import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AA {
	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\lta\\Desktop\\0001.png");
		BufferedImage image = ImageIO.read(file);
		ImageIO.write(image, "jpg", new File("C:\\Users\\lta\\Desktop\\aa.jpg"));
		System.out.println(Integer.parseInt("01"));
	}
	
}
