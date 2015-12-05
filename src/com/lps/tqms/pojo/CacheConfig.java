package com.lps.tqms.pojo;

public class CacheConfig {
	private int clearInterval;
	private int clearTime;
	public CacheConfig() {
	}
	public CacheConfig(int clearInterval, int clearTime) {
		super();
		this.clearInterval = clearInterval;
		this.clearTime = clearTime;
	}
	public int getClearInterval() {
		return clearInterval;
	}
	public int getClearTime() {
		return clearTime;
	}
	public void setClearInterval(int clearInterval) {
		this.clearInterval = clearInterval;
	}
	public void setClearTime(int clearTime) {
		this.clearTime = clearTime;
	}
}
