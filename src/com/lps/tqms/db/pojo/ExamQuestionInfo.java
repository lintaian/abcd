package com.lps.tqms.db.pojo;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_exam_question_info")
public class ExamQuestionInfo {
	@Id
	private int id;
	@Column
	private String examCode;
	@Column
	private String questionTitle;
	@Column
	private double questionPoint;
	@Column
	private String knowledgeName;
	@Column
	private String abilityName;
	@Column
	private String knowledgeCode;
	@Column
	private String abilityCode;
	@Column
	private double questionOrder;
	@Column
	private String area;
	@Column
	private double difficulty;
	@Column
	private int questionType;
	
	public int getId() {
		return id;
	}
	public String getExamCode() {
		return examCode;
	}
	public String getQuestionTitle() {
		return questionTitle;
	}
	public double getQuestionPoint() {
		return questionPoint;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}
	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}
	public void setQuestionPoint(double questionPoint) {
		this.questionPoint = questionPoint;
	}
	public double getQuestionOrder() {
		return questionOrder;
	}
	public String getArea() {
		return area;
	}
	public double getDifficulty() {
		return difficulty;
	}
	public int getQuestionType() {
		return questionType;
	}
	public void setQuestionOrder(double questionOrder) {
		this.questionOrder = questionOrder;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public void setDifficulty(double difficulty) {
		this.difficulty = difficulty;
	}
	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}
	public String getKnowledgeName() {
		return knowledgeName;
	}
	public String getAbilityName() {
		return abilityName;
	}
	public void setKnowledgeName(String knowledgeName) {
		this.knowledgeName = knowledgeName;
	}
	public void setAbilityName(String abilityName) {
		this.abilityName = abilityName;
	}
	public String getKnowledgeCode() {
		return knowledgeCode;
	}
	public String getAbilityCode() {
		return abilityCode;
	}
	public void setKnowledgeCode(String knowledgeCode) {
		this.knowledgeCode = knowledgeCode;
	}
	public void setAbilityCode(String abilityCode) {
		this.abilityCode = abilityCode;
	}
}
