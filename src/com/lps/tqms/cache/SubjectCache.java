package com.lps.tqms.cache;

import java.util.ArrayList;
import java.util.List;

import com.lps.tqms.db.pojo.Subject;
/**
 * 科目信息缓存类
 * @author lta
 *
 */
public class SubjectCache {
	private long active;
	private List<Subject> subjects = new ArrayList<Subject>();
	public SubjectCache() {
		updateActive();
	}
	public SubjectCache(List<Subject> subjects) {
		this.subjects = subjects;
		updateActive();
	}
	/**
	 * 通过科目名称获取科目信息
	 * @param name
	 * @return
	 */
	public Subject getByName(String name) {
		Subject rs = null;
		for (Subject subject : subjects) {
			if (subject.getName().equals(name)) {
				rs = subject;
				break;
			}
		}
		updateActive();
		return rs;
	}
	/**
	 *  通过科目编号获取科目信息
	 * @param code
	 * @return
	 */
	public Subject getByCode(String code) {
		Subject rs = null;
		for (Subject subject : subjects) {
			if (subject.getId().equals(code)) {
				rs = subject;
				break;
			}
		}
		updateActive();
		return rs;
	}
	public long getActive() {
		return active;
	}
	public List<Subject> getSubjects() {
		return subjects;
	}
	public void updateActive() {
		this.active = System.currentTimeMillis();
	}
	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
		updateActive();
	}
}
