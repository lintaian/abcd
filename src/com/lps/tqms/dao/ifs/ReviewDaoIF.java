package com.lps.tqms.dao.ifs;

import java.util.List;

import org.nutz.dao.entity.Record;

public interface ReviewDaoIF {
	List<Record> getDifficulty(List<String> unitCodes, String examCode);
	List<Record> getQuestionScore(List<String> unitCodes, String examCode);
	List<Record> getStuQuestionScore(List<String> unitCodes, String examCode, String questionTitle);
}
