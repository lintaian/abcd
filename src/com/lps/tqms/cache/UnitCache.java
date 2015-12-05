package com.lps.tqms.cache;

import com.lps.tqms.user.UnitQueryInfo;

public class UnitCache {
	private long active;
	private UnitQueryInfo info;
	public UnitCache() {
		updateActive();
	}
	public UnitCache(UnitQueryInfo info) {
		super();
		this.info = info;
		updateActive();
	}
	public long getActive() {
		return active;
	}
	public UnitQueryInfo getInfo() {
		updateActive();
		return info;
	}
	public void updateActive() {
		this.active = System.currentTimeMillis();
	}
	public void setInfo(UnitQueryInfo info) {
		this.info = info;
		updateActive();
	}
}
