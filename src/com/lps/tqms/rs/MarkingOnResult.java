package com.lps.tqms.rs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkingOnResult {
	public String unitName;
	public Map<String, List<MarkingOnTemp>> childs = new HashMap<String, List<MarkingOnTemp>>();
	public Map<String, Integer> students = new HashMap<String, Integer>();
}
