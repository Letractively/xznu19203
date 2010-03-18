package com.TzTwork.awa.antworkagent.po;

import java.sql.Clob;

/**
 * 主题类
 * @author jiangjun
 *
 */
public class Subject {

	private int subjectId;
	private String subjectContent;
	private int spaceId;//版块ID
	private String note;
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	
	public String getSubjectContent() {
		return subjectContent;
	}
	public void setSubjectContent(String subjectContent) {
		this.subjectContent = subjectContent;
	}
	public int getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(int spaceId) {
		this.spaceId = spaceId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Subject(int subjectId, String subjectContent, int spaceId,
			String note) {
		super();
		this.subjectId = subjectId;
		this.subjectContent = subjectContent;
		this.spaceId = spaceId;
		this.note = note;
	}
	public Subject() {
		super();
	}
	
}
