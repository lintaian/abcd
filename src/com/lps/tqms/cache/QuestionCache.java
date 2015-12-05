package com.lps.tqms.cache;

import java.util.ArrayList;
import java.util.List;

import com.lps.tqms.db.pojo.ExamQuestionInfo;

public class QuestionCache {
	private long active;
	private List<ExamQuestionInfo> infos = new ArrayList<ExamQuestionInfo>();
	public QuestionCache() {
		updateActive();
	}
	public QuestionCache(List<ExamQuestionInfo> infos) {
		super();
		this.infos = infos;
		updateActive();
	}
	public long getActive() {
		return active;
	}
	public List<ExamQuestionInfo> getInfos() {
		updateActive();
		return infos;
	}
	public void updateActive() {
		this.active = System.currentTimeMillis();
	}
	public void setInfos(List<ExamQuestionInfo> infos) {
		this.infos = infos;
		updateActive();
	}
}
