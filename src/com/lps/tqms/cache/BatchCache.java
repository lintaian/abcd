package com.lps.tqms.cache;

import com.lps.tqms.db.pojo.ExamBatchInfo;

public class BatchCache {
	private long active;
	private ExamBatchInfo batchInfo;
	public BatchCache() {
		updateActive();
	}
	public BatchCache(ExamBatchInfo batchInfo) {
		super();
		this.batchInfo = batchInfo;
		updateActive();
	}
	public long getActive() {
		return active;
	}
	public ExamBatchInfo getBatchInfo() {
		updateActive();
		return batchInfo;
	}
	public void updateActive() {
		this.active = System.currentTimeMillis();
	}
	public void setBatchInfo(ExamBatchInfo batchInfo) {
		this.batchInfo = batchInfo;
		updateActive();
	}
}
