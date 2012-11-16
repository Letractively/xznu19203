package com.jiangjun.colabox.ui;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BilldbHelper {

	private static final String TAG = "Cola_BilldbHelper";
	private static final String DATABASE_NAME = "cola.db";

	SQLiteDatabase db;
	Context context;

	public BilldbHelper(Context _context) {
		// TODO Auto-generated constructor stub
		context = _context;
		// 创建数据库
		db = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
		Log.v(TAG, "db path=" + db.getPath());
	}
	
	/**
	 * 创建账目表
	 */
	public void createTable_acctitem()
	{
		try {
			db.execSQL("CREATE TABLE acctitem ("
					+ "ID INTER PRIMARY KEY,"
					+ "PID INTEGER,"
					+ "NAME TEXT,"
					+ "TYPE INTEGER"
					+ ");");
			Log.v("cola", "Create Table acctitem ok.");
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("cola", "Create Table acctitem err ,table exists.");
		}
	}
	
	public void createTable_bills()
	{
		try {
			db.execSQL("CREATE TABLE bills ("
					+ "ID INTEGER PRIMARY KEY,"
					+ "FEE INTEGER,"
					+ "USERID TEXT,"
					+ "SDATE TEXT,"
					+ "STIME TEXT,"
					+ "DESC TEXT"
					+ ");");
			Log.v(TAG, "Create Table biils ok.");
		} catch (Exception e) {
			// TODO: handle exception
			Log.v(TAG, "Create Table bills err ,table exists.");
		}
	}
	
	public void createTable_colaconfig()
	{
		try {
			db.execSQL("CREATE TABLE colaconfig ("
					+ "ID INTEGER PRIMARY KEY,"
					+ "NAME TEXT"
					+ ");");
			Log.v(TAG, "Create Table colaconfig ok.");
		} catch (Exception e) {
			// TODO: handle exception
			Log.v(TAG, "Create Table colaconfig err,table exists.");
		}
	}
	
	public void initAcctitem(){
		db.execSQL("insert into acctitem values(100,0,'收入',0)");
		db.execSQL("insert into acctitem values(100100,100,'工资',0)");
		db.execSQL("insert into acctitem values(200,0,'支出',1)");
		db.execSQL("insert into acctitem values(200100,200,'生活用品',1)");
		db.execSQL("insert into acctitem values(200101,200,'水电煤气费',1)");
		db.execSQL("insert into acctitem values(200102,200,'汽油费',1)");
		Log.v(TAG, "insert into ok.");
		
	}
	
	public void QueryTable_acctitem(){
		
	}
	
	public void FirstStart()
	{
		//如果是第一次启动就不存在colaconfig这张表
		try {
			String col[] = {"type","name"};
			Cursor c = db.query("sqlite_master", col, "name='colaconfig'", null, null, null, null);
			int n = c.getCount();
			if(c.getCount() == 0)
			{
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
