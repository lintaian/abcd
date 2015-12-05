package com.lps.tqms.cache;

public class KaCache {
	private long active;
	private Ka ka;
	public KaCache() {
		updateActive();
	}
	public KaCache(Ka ka) {
		super();
		this.ka = ka;
		updateActive();
	}
	public void updateActive() {
		this.active = System.currentTimeMillis();
	}
	public long getActive() {
		return active;
	}
	public void setKa(Ka ka) {
		this.ka = ka;
	}
	public Ka getKa() {
		updateActive();
		return ka;
	}
}
