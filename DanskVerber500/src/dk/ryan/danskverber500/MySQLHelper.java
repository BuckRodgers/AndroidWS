package dk.ryan.danskverber500;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLHelper extends SQLiteOpenHelper{

	public static final String TAG = "DB MYSQLHELPER";
	public static final String TABLE_VERB = "verblist";
	public static final String COLUMN_ID = "_id";
	//public static final String 
	public static final String col_translation = "translation";
	public static final String col_navnem = "navnem";
	public static final String col_nutid = "nutid";
	public static final String col_datid = "datid";
	public static final String col_førnutid = "førnutid";
	public static final String col_førdatid = "førdatid";
	public static final String col_bydem = "bydem";
	public static final String col_boj = "bøj";
	public String[] allColumns = {MySQLHelper.COLUMN_ID, MySQLHelper.col_translation, MySQLHelper.col_navnem, 
			MySQLHelper.col_nutid, MySQLHelper.col_datid, MySQLHelper.col_førnutid, MySQLHelper.col_førdatid, MySQLHelper.col_bydem, MySQLHelper.col_boj};
	
	private static String DB_PATH = "";///data/data/dk.ryan.danskverber/databases/";
	private static String DB_NAME1  = "myverblist";
	private static String DB_NAME  = "MyVerbListDB";
	private SQLiteDatabase myDatabase;
	private final Context myContext;
	
	//this appeared to work, could see database in debug, but still had a crash
	//public MySQLHelper(Context context){
	//	super(context, DB_NAME, null, 1);
	//	this.myContext = context;
		
	//} 
	
	public MySQLHelper(Context context, String databaseName){
		super (context, databaseName, null, 1);
		this.myContext = context;
		//String packageName = context.getPackageName();
		if(android.os.Build.VERSION.SDK_INT >= 17){
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
		}else{
			DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
			
		}
		openDataBase();
	}
	//or
	
	public MySQLHelper(Context context){
		super(context, DB_NAME, null, 1);
		if(android.os.Build.VERSION.SDK_INT >= 17){
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
		}else{
			DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
			
		}
		this.myContext = context;
		
	}
	

	
	public void createDatabase() throws IOException{
		boolean dbExist = checkDatabase();
		SQLiteDatabase db_Read = null; //??
		
		if(dbExist){
			//do nothing
			Log.i(TAG, "database already exists");
		}else{ //if it doesnt exist then copy it from assets
			
			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			db_Read = this.getReadableDatabase();
			db_Read.close();
			
			//this.getReadableDatabase();
			//this.close();
			
			try {
				copyDatabase();
				Log.e(TAG, "createDatabase is created");
			}catch (IOException e){
				Log.e(TAG, "error copying database" + e.toString());
			}
			
		}
	}
	
	private void copyDatabase() throws IOException {
		//open local db as input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		
		//path to just created empty db 
		
		String outFileName = DB_PATH + DB_NAME;
		
		OutputStream myOutput = new FileOutputStream(outFileName);
		//transfer bytes from inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}
		
		myOutput.flush();
		myOutput.close();
		myInput.close();
		
		
	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String path = DB_PATH + DB_NAME;
		if (myDatabase == null) {
		try {
			createDatabase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "copuldnt create db");
		}
		myDatabase = SQLiteDatabase.openDatabase(path, null,
		SQLiteDatabase.OPEN_READWRITE);
		}
		return myDatabase;
		}

//or
	/*
	boolean openDatabase() throws SQLException{
		String myPath = DB_PATH + DB_NAME;
		myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);//SQLiteDatabase.OPEN_READONLY);
		return myDatabase != null;
	}
	*/
	
	
	
	private boolean checkDatabase() {
		
		SQLiteDatabase checkDB = null;
		
		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
			Log.e("DB_ERROR", "error checking db - database doesnt exist yet");
		}
		
		if (checkDB != null){
			checkDB.close();
		}
		return checkDB != null;// ? true : false;
		
	}
	
	/*
	//another way to check?
	 private boolean checkDataBase()
	    {
	        File dbFile = new File(DB_PATH + DB_NAME);
	        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
	        return dbFile.exists();
	    }

	 */
	
	@Override
	public synchronized void close(){
		if(myDatabase != null)
			myDatabase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	
 
 
	
	
	/*public MySQLHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}*/
	
	
	 // Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.
	

	
	
}
