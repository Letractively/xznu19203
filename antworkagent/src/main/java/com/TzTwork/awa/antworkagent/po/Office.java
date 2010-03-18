package com.TzTwork.awa.antworkagent.po;

import java.sql.Clob;

/**
 * 帖子
 * @author jiangjun
 *
 */
public class Office {

	private int officeID;
	private Clob officeContent;
	private int authorId;//作者ID
	private int subjectId;//主题ID
	private String date;
	private String note;
	public int getOfficeID() {
		return officeID;
	}
	public void setOfficeID(int officeID) {
		this.officeID = officeID;
	}

	public Clob getOfficeContent() {
		return officeContent;
	}
	public void setOfficeContent(Clob officeContent) {
		this.officeContent = officeContent;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Office(int officeID, Clob officeContent, int authorId,
			int subjectId, String date, String note) {
		super();
		this.officeID = officeID;
		this.officeContent = officeContent;
		this.authorId = authorId;
		this.subjectId = subjectId;
		this.date = date;
		this.note = note;
	}
	public Office() {
		super();
	}
	
}
