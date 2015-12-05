package com.lps.tqms.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageHelper {

	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}
		if (str.length() == 0) {
			return true;
		}
		return false;
	}

	public static List<Rectangle> parseRectangle(String area) {
		List<Rectangle> list = new ArrayList<Rectangle>();
		String[] rectItem = area.split(";");
		for (String item : rectItem) {
			String[] ptItem = item.split(",");
			if (ptItem.length >= 4) {
				int x = Integer.parseInt(ptItem[0]);
				int y = Integer.parseInt(ptItem[1]);
				int w = Integer.parseInt(ptItem[2]);
				int h = Integer.parseInt(ptItem[3]);
				list.add(new Rectangle(x, y, w, h));
			}
		}
		return list;
	}

	public static byte[] readStream(File file) {
		byte[] data = new byte[0];
		byte[] buffer = new byte[1024];
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				InputStream inputStream = new FileInputStream(file);
				try {
					int len = inputStream.read(buffer);
					while (len > 0) {
						output.write(buffer, 0, len);
						len = inputStream.read(buffer);
					}
				} finally {
					inputStream.close();
				}
			} finally {
				output.close();
			}
			data = output.toByteArray();
		} catch (Exception e) {
		}
		return data;
	}

	public static ByteArrayOutputStream getImage(String url, int limit) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(false);
			conn.setDoInput(true);
			InputStream input = conn.getInputStream();
			try {
				byte[] buffer = new byte[4096];
				int recv = input.read(buffer);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				while (recv > 0) {
					stream.write(buffer, 0, recv);
					recv = input.read(buffer);
				}

				if (limit > 0) {
					ByteArrayInputStream inputStream = new ByteArrayInputStream(
							stream.toByteArray());
					BufferedImage src = ImageIO.read(inputStream);
					if (limit < src.getHeight()) {
						BufferedImage dst = src.getSubimage(0, 0,
								src.getWidth(), limit);
						stream = new ByteArrayOutputStream();
						ImageIO.write(dst, "png", stream);
						output.write(stream.toByteArray());
					}
				} else {
					output = stream;
				}
			} finally {
				input.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}

	public static ByteArrayOutputStream getImage(String url, String area,
			int limit) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(false);
			conn.setDoInput(true);
			InputStream input = conn.getInputStream();
			try {
				byte[] buffer = new byte[4096];
				int recv = input.read(buffer);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				while (recv > 0) {
					stream.write(buffer, 0, recv);
					recv = input.read(buffer);
				}

				byte[] imageData = stream.toByteArray();
				if ((imageData == null) || (imageData.length == 0)) {
					return output;
				}

				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						imageData);
				BufferedImage src = ImageIO.read(inputStream);

				int width = 0;
				int height = 0;
				List<Rectangle> rectList = parseRectangle(area);
				for (Rectangle rect : rectList) {
					if (rect.width > width) {
						width = rect.width;
					}
					height += rect.height;
				}
				if ((limit > 0) && (limit < height)) {
					height = limit;
				}

				BufferedImage dst = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics g = dst.getGraphics();
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, width, height);

				height = 0;
				for (Rectangle rect : rectList) {
					BufferedImage tmp = src.getSubimage(rect.x, rect.y,
							rect.width, rect.height);
					g.drawImage(tmp, 0, height, null);
					height += rect.height;
				}

				ImageIO.write(dst, "png", output);
			} finally {
				input.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output;
	}

}
