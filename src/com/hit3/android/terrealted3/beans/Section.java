package com.hit3.android.terrealted3.beans;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.db.tables.BaseTable;

public class Section {

	
	private Constant.CONTENT_TYPE contentType;
	private int RStringReference;
	private String tableName;
	private BaseTable table;
	private int position;
	private boolean enabled;
	
	
	public Section() {
		// TODO Auto-generated constructor stub
	}
	
	public Section(Constant.CONTENT_TYPE contentType, int RStringReference, String tableName, BaseTable table, int position) {
		this.contentType = contentType;
		this.RStringReference = RStringReference;
		this.tableName = tableName;
		this.table = table;
		this.position = position;
	}


	public Constant.CONTENT_TYPE getContentType() {
		return contentType;
	}


	public void setContentType(Constant.CONTENT_TYPE contentType) {
		this.contentType = contentType;
	}


	public int getRStringReference() {
		return RStringReference;
	}


	public void setRStringReference(int rStringReference) {
		RStringReference = rStringReference;
	}


	public String getTableName() {
		return tableName;
	}

	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
	public BaseTable getTable() {
		return table;
	}

	public void setTable(BaseTable table) {
		this.table = table;
	}

	public int getPosition() {
		return position;
	}


	public void setPosition(int position) {
		this.position = position;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	

}
