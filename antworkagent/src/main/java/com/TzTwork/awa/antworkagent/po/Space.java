package com.TzTwork.awa.antworkagent.po;

/**
 * 版块
 * @author jiangjun
 *
 */
public class Space {

	private int spaceId;
	private String spaceName;
	private String note;
	public int getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(int spaceId) {
		this.spaceId = spaceId;
	}
	public String getSpaceName() {
		return spaceName;
	}
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Space(int spaceId, String spaceName, String note) {
		super();
		this.spaceId = spaceId;
		this.spaceName = spaceName;
		this.note = note;
	}
	public Space() {
		super();
	}
	
}
