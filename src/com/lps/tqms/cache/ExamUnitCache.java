package com.lps.tqms.cache;

import java.util.ArrayList;
import java.util.List;

import com.lps.tqms.db.pojo.ExamUnitInfo;

public class ExamUnitCache {
	private long active;
	private List<ExamUnitInfo> infos = new ArrayList<ExamUnitInfo>();
	public ExamUnitCache() {
		updateActive();
	}
	public ExamUnitCache(List<ExamUnitInfo> infos) {
		super();
		this.infos = infos;
		updateActive();
	}
	public long getActive() {
		return active;
	}
	public List<ExamUnitInfo> getInfo() {
		updateActive();
		return infos;
	}
	public void updateActive() {
		this.active = System.currentTimeMillis();
	}
	public void setInfo(List<ExamUnitInfo> infos) {
		this.infos = infos;
		updateActive();
	}
}
