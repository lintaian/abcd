package com.lps.tqms.user;

public class Role {
	private String user_id;
	private String orgi_code;
	private String exam_code;
	private String sub_code;
	private int marking;
	private int show_student;
	private int un_compare;
	public Role() {
	}
	public Role(String user_id, String orgi_code, String exam_code,
			String sub_code, int marking, int show_student, int un_compare) {
		super();
		this.user_id = user_id;
		this.orgi_code = orgi_code;
		this.exam_code = exam_code;
		this.sub_code = sub_code;
		this.marking = marking;
		this.show_student = show_student;
		this.un_compare = un_compare;
	}

	public String getUser_id() {
		return user_id;
	}
	public String getOrgi_code() {
		return orgi_code;
	}
	public String getExam_code() {
		return exam_code;
	}
	public String getSub_code() {
		return sub_code;
	}
	public int getMarking() {
		return marking;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public void setOrgi_code(String orgi_code) {
		this.orgi_code = orgi_code;
	}
	public void setExam_code(String exam_code) {
		this.exam_code = exam_code;
	}
	public void setSub_code(String sub_code) {
		this.sub_code = sub_code;
	}
	public void setMarking(int marking) {
		this.marking = marking;
	}
	public int getShow_student() {
		return show_student;
	}
	public void setShow_student(int show_student) {
		this.show_student = show_student;
	}
	public int getUn_compare() {
		return un_compare;
	}
	public void setUn_compare(int un_compare) {
		this.un_compare = un_compare;
	}
}
