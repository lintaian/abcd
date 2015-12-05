package com.lps.tqms.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.pojo.UnitType.Type;
import com.lps.tqms.rs.RankScore;
import com.lps.tqms.user.Role;
import com.lps.tqms.user.UnitQueryInfo;
import com.lps.tqms.user.UnitQueryInfoWrap;

public class Util {
	public static double fixed(Object o) {
		return fixed(o, "0.00");
	}
	public static double fixed(Object o, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return Double.valueOf(df.format(o));
	}
	public static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	public static int getIndex(List<String> list, String obj) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(obj)) {
				return i;
			}
		}
		return -1;
	}
	public static List<Object> buildValues(List<Object> vals, List<String> all, List<String> own) {
		List<Object> rs = new ArrayList<Object>();
		for (int i = 0; i < all.size(); i++) {
			int index = getIndex(own, all.get(i));
			if (index > -1) {
				rs.add(i, vals.get(index + 1));
			} else {
				rs.add(i, "-");
			}
		}
		return rs;
	}
	public static void buildRank(RankScore score, List<RankScore> scores, List<Object> rs) {
		int position = 1;
		for (RankScore rankScore : scores) {
			if (rankScore.score > score.score) {
				position += 1;
			}
		}
		rs.add(String.format("%d/%d", position, scores.size()));
		rs.add(fixed((scores.size() - position) * 1.0 / (scores.size() - 1) * 100, "0.00"));
	}
	public static boolean matchArtSci(String classCode, int artSci) {
		if (artSci == 0) {
			return true;
		}
		int as = Integer.parseInt(classCode.substring(12, 14));
		if (as == 0 || as == artSci) {
			return true;
		}
		return false;
	}
	public static int parseArtSci(String examCode) {
		int as = 0;
		switch (examCode) {
		case "03":
		case "06":
		case "07":
		case "08":
			as = 1;
			break;
		case "04":
		case "09":
		case "10":
		case "11":
			as = 2;
			break;
		default:
			break;
		}
		return as;
	}
	public static int parseArtSci(ExamTypeGroup group) {
		int as = 0;
		if (group == ExamTypeGroup.ARTS || group == ExamTypeGroup.ARTS_MAJOR) {
			as = 1;
		} else if (group == ExamTypeGroup.SCIENCE || group == ExamTypeGroup.SCIENCE_MAJOR) {
			as = 2;
		}
		return as;
	}
	public static List<UnitQueryInfo> cloneUnitQueryInfoList(List<UnitQueryInfo> infos) {
		List<UnitQueryInfo> rs = new ArrayList<UnitQueryInfo>();
		for (UnitQueryInfo unitQueryInfo : infos) {
			rs.add(unitQueryInfo.clone());
		}
		return rs;
	}
	public static void rmNode(Set<UnitQueryInfo> infos, Type type) {
		Set<UnitQueryInfo> rm = new HashSet<UnitQueryInfo>();
		for (UnitQueryInfo unitQueryInfo : infos) {
			if (unitQueryInfo.getUnitTypeCode() == type.code) {
				rm.add(unitQueryInfo);
			}
		}
		infos.removeAll(rm);
		for (UnitQueryInfo unitQueryInfo : rm) {
			for (UnitQueryInfo qi : infos) {
				if (qi.getParentCode().equals(unitQueryInfo.getUnitCode())) {
					qi.setParentCode(unitQueryInfo.getParentCode());
				}
			}
		}
	}
	public static void rmNodeMax(Set<UnitQueryInfo> infos, int max) {
		Set<UnitQueryInfo> rm = new HashSet<UnitQueryInfo>();
		for (UnitQueryInfo unitQueryInfo : infos) {
			if (unitQueryInfo.getUnitTypeCode() > max) {
				rm.add(unitQueryInfo);
			}
		}
		infos.removeAll(rm);
	}
	public static void rmNodeMin(Set<UnitQueryInfo> infos, int min) {
		Set<UnitQueryInfo> rm = new HashSet<UnitQueryInfo>();
		for (UnitQueryInfo unitQueryInfo : infos) {
			if (unitQueryInfo.getUnitTypeCode() < min) {
				rm.add(unitQueryInfo);
			}
		}
		infos.removeAll(rm);
	}
	public static String formatTitle(String title) {
		title = title.replaceAll("\\.", "_");
		title = title.replaceAll("-", "_");
		return title;
	}
	public static String decodeCH_ZN(String str) {
		try {
			str = new String(str.getBytes("iso8859-1"), "utf-8");
//			str = new String(new String(str.getBytes(), "ISO-8859-1").getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	public static UnitQueryInfo transListToUnitQueryInfo(List<UnitQueryInfo> infos) {
		UnitQueryInfo rs = findTop(infos);
		temp(infos, rs);
		return rs;
	}
	private static void temp(List<UnitQueryInfo> infos, UnitQueryInfo rs) {
		for (UnitQueryInfo unitQueryInfo : infos) {
			if (unitQueryInfo.getParentCode().equals(rs.getUnitCode())) {
				unitQueryInfo.setParentCode(rs.getUnitCode());
				rs.getChilds().add(unitQueryInfo);
			}
		}
		infos.removeAll(rs.getChilds());
		if (rs.getUnitTypeCode() < Type.GRADE.code) {
			for (UnitQueryInfo unitQueryInfo : rs.getChilds()) {
				temp(infos, unitQueryInfo);
			}
		}
	}
	private static UnitQueryInfo findTop(List<UnitQueryInfo> infos) {
		UnitQueryInfo rs = infos.get(0);
		for (UnitQueryInfo unitQueryInfo : infos) {
			if (unitQueryInfo.getUnitTypeCode() < rs.getUnitTypeCode()) {
				rs = unitQueryInfo;
			}
		}
		return rs;
	}
	public static UnitQueryInfo findTop(UnitQueryInfo info, List<Role> roles) {
		UnitQueryInfo rs = null;
		for (Role role : roles) {
			UnitQueryInfo temp = info.getUnitInfo(role.getOrgi_code());
			if (rs == null || (temp != null && temp.getUnitTypeCode() < rs.getUnitTypeCode())) {
				rs = temp;
			}
		}
		return rs;
	}
	public static UnitQueryInfo findUnitInfo(UnitQueryInfo unitQueryInfo, String unitCode) {
		UnitQueryInfoWrap rs = new UnitQueryInfoWrap();
		findUnitInfo(unitQueryInfo, unitCode, rs);
		return rs.info;
	}
	private static void findUnitInfo(UnitQueryInfo info, String unitCode, UnitQueryInfoWrap rs) {
		if (info.getUnitCode().equals(unitCode)) {
			rs.info = info;
		} else {
			for (UnitQueryInfo queryInfo : info.getChilds()) {
				findUnitInfo(queryInfo, unitCode, rs);
			}
		}
	}
}
