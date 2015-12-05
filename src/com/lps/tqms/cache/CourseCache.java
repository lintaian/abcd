package com.lps.tqms.cache;

import com.lps.tqms.db.pojo.ExamCourseInfo;

public class CourseCache {
	private long active;
	private ExamCourseInfo courseInfo;
	public CourseCache() {
		updateActive();
	}
	public CourseCache(ExamCourseInfo courseInfo) {
		super();
		this.courseInfo = courseInfo;
		updateActive();
	} 
	public long getActive() {
		return active;
	}
	public ExamCourseInfo getCourseInfo() {
		updateActive();
		return courseInfo;
	}
	public void updateActive() {
		this.active = System.currentTimeMillis();
	}
	public void setCourseInfo(ExamCourseInfo courseInfo) {
		this.courseInfo = courseInfo;
		updateActive();
	}
}
