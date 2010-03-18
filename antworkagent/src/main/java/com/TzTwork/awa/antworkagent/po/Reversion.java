package com.TzTwork.awa.antworkagent.po;

import java.sql.Clob;

/**
 * 回复类
 * @author jiangjun
 *
 */
public class Reversion {

	private int reversionId;
	private Clob reversionContent;
	private int authorId;//作者ID
	private int officeId;//帖子ID
	private String date;
	private String note;
	public int getReversionId() {
		return reversionId;
	}
	public void setReversionId(int reversionId) {
		this.reversionId = reversionId;
	}
	
	public Clob getReversionContent() {
		return reversionContent;
	}
	public void setReversionContent(Clob reversionContent) {
		this.reversionContent = reversionContent;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public int getOfficeId() {
		return officeId;
	}
	public void setOfficeId(int officeId) {
		this.officeId = officeId;
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
	public Reversion() {
		super();
	}
	public Reversion(int reversionId, Clob reversionContent, int authorId,
			int officeId, String date, String note) {
		super();
		this.reversionId = reversionId;
		this.reversionContent = reversionContent;
		this.authorId = authorId;
		this.officeId = officeId;
		this.date = date;
		this.note = note;
	}
	
}
