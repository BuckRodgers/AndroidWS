package dk.ryan.danskverber500;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//should be dataAdaptor
public class TestAdaptor {

	protected static final String TAG = "TestAdaptor";
	
	private final Context mContext;
	private SQLiteDatabase mDB;
	private MySQLHelper mSQLHelper;
	
	public TestAdaptor(Context context){
		this.mContext = context;
		mSQLHelper = new MySQLHelper(mContext);
		
	}
	
	public TestAdaptor createDatabase() throws SQLException{
		try{
			mSQLHelper.createDatabase();
		}catch(IOException e){
			Log.e(TAG, e.toString() + " unable to create database");
			throw new Error("unable to create database");
		}
		return this;
		
	}
	
	public TestAdaptor open() throws SQLException{
		try{
			mSQLHelper.openDataBase();
			mSQLHelper.close();
			mDB = mSQLHelper.getReadableDatabase();
		}catch(SQLException e){
			Log.e(TAG, "open >>"+ e.toString());
			throw e;
			
		}
		return this;
	}
	
	 public void close() 
	    {
	        mSQLHelper.close();
	    }
	 
	 
	public Cursor getTestData()
    {
        try
        {
            String sql ="SELECT * FROM "+mSQLHelper.TABLE_VERB;
            

            Cursor mCur = mDB.rawQuery(sql, null);
            if (mCur!=null)
            {
               mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException) 
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }
	
	public List<Verb> getAllVerbs() {
	    List<Verb> verbs = new ArrayList<Verb>();

	    Cursor cursor = mDB.query(mSQLHelper.TABLE_VERB,
	        mSQLHelper.allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    Verb verb = cursorToVerb(cursor);
	    verbs.add(verb);
	    cursor.moveToNext();
	    		
	     // Comment comment = cursorToComment(cursor);
	     // comments.add(comment);
	     // cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return verbs;
	  }
	
	private Verb cursorToVerb(Cursor cursor){
		 Verb verb = new Verb();
		 verb.setId(cursor.getLong(0));
		 verb.setNavnem(cursor.getString(2));
		 verb.setTranslation(cursor.getString(1));
		 verb.setNutid(cursor.getString(3));
		 verb.setDatid(cursor.getString(4));
		 verb.setFørnutid(cursor.getString(5));
		 verb.setFørdatid(cursor.getString(6));
		 verb.setBydem(cursor.getString(7));
		 verb.setBoj(cursor.getString(8));
		 return verb;
		 
		
		 
	 }
	
	
}
