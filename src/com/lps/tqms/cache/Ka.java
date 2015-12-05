package com.lps.tqms.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ka {
	public List<NA> knowledge = new ArrayList<NA>();
	public List<NA> ability = new ArrayList<NA>();
	public List<String> getAbilityNames() {
		List<String> rs = new ArrayList<String>();
		for (NA na : ability) {
			rs.add(na.name);
		}
		return rs;
	}
	public List<String> getKnowledgeNames() {
		List<String> rs = new ArrayList<String>();
		for (NA na : knowledge) {
			rs.add(na.name);
		}
		return rs;
	}
	public List<String> getAbilityCodes() {
		List<String> rs = new ArrayList<String>();
		for (NA na : ability) {
			rs.add(na.code);
		}
		return rs;
	}
	public List<String> getKnowledgeCodes() {
		List<String> rs = new ArrayList<String>();
		for (NA na : knowledge) {
			rs.add(na.code);
		}
		return rs;
	}
	public List<String> getNames() {
		List<String> rs = new ArrayList<String>();
		for (NA na : knowledge) {
			rs.add(na.name);
		}
		for (NA na : ability) {
			rs.add(na.name);
		}
		return rs;
	}
	public List<String> getCodes() {
		List<String> rs = new ArrayList<String>();
		for (NA na : knowledge) {
			rs.add(na.code);
		}
		for (NA na : ability) {
			rs.add(na.code);
		}
		return rs;
	}
	public List<Object> getVals() {
		List<Object> rs = new ArrayList<Object>();
		for (NA na : knowledge) {
			rs.add(na.val);
		}
		for (NA na : ability) {
			rs.add(na.val);
		}
		return rs;
	}
	public NA getCode(String name) {
		String[] temp = name.split("_");
		NA rs = new NA("", "", "");
		if (temp.length > 1) {
			if ("k".equals(temp[0])) {
				for (NA na : knowledge) {
					if (na.name.equals(temp[1])) {
						rs = new NA(na.name, "k_" + na.code, na.val);
					}
				}
			} else {
				for (NA na : ability) {
					if (na.name.equals(temp[1])) {
						rs = new NA(na.name, "a_" + na.code, na.val);
					}
				}
			}
		}
		return rs;
	}
	public Map<String, Object> getPoints() {
		Map<String, Object> rs = new HashMap<String, Object>();
		for (NA na : knowledge) {
			rs.put(na.name, na.val);
		}
		for (NA na : ability) {
			rs.put(na.name, na.val);
		}
		return rs;
	}
}
