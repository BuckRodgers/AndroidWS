package dk.ryan.danskverber500;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.ryan.danskverber500.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class DisplayActivity extends Activity {

	private Animation mInFromRight;
	private Animation mOutToLeft;
	private Animation mInFromLeft;
	private Animation mOutToRight;
	private Animation mInFromTop;
	private Animation mOutToTop;
	private Animation mInFromBottom;
	private Animation mOutToBottom;
	private ViewFlipper mViewFlipper;
	private ViewFlipper mViewFlipperUpDown;
	
	private float lastX;
	private float lastY;
	//private float lastTouchX, lastTouchY;
	
	private final int MINIMUM_HORIZONTAL_DISTANCE = 120;
    private final int MAXIMUM_VERTICAL_DISTANCE = 100;
    private final int MAX = 514;
	private boolean hasMoved = false;
	String English, Danish, nutidDatid, DanishNext, DanishPrevious;
	int posY, posX = 0;
	int nextposX = 1;
	int prevposX = MAX;
	TextView nowVerb,nextVerb, previousVerb, englishVerb, datidVerb ;
	
	private static String DB_NAME  = "MyVerbListDB";
	public static final String TABLE_VERB = "verblist";
	public static final String COLUMN_ID = "_id";
	public static final String col_navnem = "NAVNEM";
	
	LinearLayout LL;
	MySQLHelper mySQLHelper; 
	List<Verb> list;
	private SQLiteDatabase database;
	private ArrayList verbs;
	
	public static Typeface typeface;

	
	
	/** called when activity iws first creeted**/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		
	
		Danish = "at bede";
        English =  "Ask, Beg, Prey";
        nutidDatid = "beder, bad, har bedt";
        DanishNext = "at Betyde";
        DanishPrevious = "at Bide";
        try{
        	typeface = Typeface.createFromAsset(getAssets(),"Mate-Regular.otf");
        }catch(Exception e){
        	Log.e("ERROR", "Error in code " + e.toString() );
        	
        }
        
        nowVerb = (TextView) findViewById(R.id.VerbNow);
        nextVerb = (TextView) findViewById(R.id.VerbNext);
        previousVerb = (TextView) findViewById(R.id.VerbPrevious);
        englishVerb = (TextView) findViewById(R.id.VerbEnglish);
        datidVerb = (TextView) findViewById(R.id.VerbDatid);
        nowVerb.setTypeface(typeface);
        //nowVerb.setTextSize(24);
        nextVerb.setTypeface(typeface);
        previousVerb.setTypeface(typeface);
        englishVerb.setTypeface(typeface);
        //englishVerb.setTextSize(24);
        datidVerb.setTypeface(typeface);
        //datidVerb.setTextSize(24);
        
        
        MySQLHelper mSQLHelper = new MySQLHelper(this, DB_NAME);
        database = mSQLHelper.openDataBase();
  
        
        list = getAllVerbs(mSQLHelper);
        
        Verb nowVerb = list.get(posX);
    	Verb prevVerb = list.get(prevposX);
    	Verb nextVerb = list.get(nextposX);
    	
    	setText();
		
        
        mViewFlipperUpDown = (ViewFlipper) findViewById(R.id.view_flipper_UpDown);
        mViewFlipperUpDown.setDisplayedChild(1);
        
		mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		mViewFlipper.setDisplayedChild(1);
		initAnimations();
		
	}
	
	private void fillVerbs(){
		verbs = new ArrayList<String>();
		Cursor verbCursor = database.query(TABLE_VERB, new String[]{ COLUMN_ID,  col_navnem}, null, null, null, null, col_navnem);
		verbCursor.moveToFirst();
		if(!verbCursor.isAfterLast()){
			do{
				String verb_d = verbCursor.getString(1);
				verbs.add(verb_d);
				
			}while (verbCursor.moveToNext());
		}
		verbCursor.close();
	}
	
	
	
	public List<Verb> getAllVerbs(MySQLHelper msqlhelper) {
	    List<Verb> verbs = new ArrayList<Verb>();

	    Cursor cursor = database.query(msqlhelper.TABLE_VERB,
	        msqlhelper.allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    Verb verb = cursorToVerb(cursor);
	    verbs.add(verb);
	    cursor.moveToNext();
	    		
	   
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
	

	
	private void setText()
	{
		Verb vnowVerb = list.get(posX);
    	Verb vprevVerb = list.get(prevposX);
    	Verb vnextVerb = list.get(nextposX);
		//setTextViews(nowVerb.getTranslation(), nowVerb.getNavnem(), nextVerb.getNavnem(), prevVerb.getNavnem(), nowVerb.getDatid() + " " + nowVerb.getFørdatid() + " " + nowVerb.getFørnutid());
		nowVerb.setText(vnowVerb.getNavnem());
		//bug
		//the next sceeen never shows the nextVerb it always shows the now verb
		nextVerb.setText(vnowVerb.getNavnem()); 
		previousVerb.setText(vnowVerb.getNavnem());
		
		englishVerb.setText(vnowVerb.getTranslation());
		datidVerb.setText("Datid : \n" + vnowVerb.getDatid() + "\n"  
		+ "Førnutid : \n" + vnowVerb.getFørnutid() + "\n"
		+ "Førdatid : \n" +  vnowVerb.getFørdatid());
	
	}
	


	private void initAnimations() {
		// TODO Auto-generated method stub
		mInFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f, 
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		mInFromRight.setDuration(200);
		AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
		mInFromRight.setInterpolator(accelerateInterpolator);
		
		mInFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, 
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		mInFromLeft.setDuration(200);
		mInFromLeft.setInterpolator(accelerateInterpolator);
		
		mOutToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		mOutToRight.setDuration(200);
		mOutToRight.setInterpolator(accelerateInterpolator);
		
		mOutToLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT,-1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		mOutToLeft.setDuration(200);
		mOutToLeft.setInterpolator(accelerateInterpolator);

	/////////////Top Down////////////
		mInFromTop = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
				0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		mInFromTop.setDuration(200);
		mInFromTop.setInterpolator(accelerateInterpolator);
		
		mInFromBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, 
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		mInFromBottom.setDuration(200);
		mInFromBottom.setInterpolator(accelerateInterpolator);
		
		mOutToTop = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, .0f,
				Animation.RELATIVE_TO_PARENT, -1.0f);
		mOutToTop.setDuration(200);
		mOutToTop.setInterpolator(accelerateInterpolator);
		
		mOutToBottom = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT,0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f);
		mOutToBottom.setDuration(200);
		mOutToBottom.setInterpolator(accelerateInterpolator);
		
		
		final GestureDetector gestureDetector;
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		
		mViewFlipper.setOnTouchListener(new OnTouchListener() {
			
			
			//@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(gestureDetector.onTouchEvent(event)){
					return false;
				}else{
					return true;
					
				}
			
			}
		});
		
		mViewFlipperUpDown.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(gestureDetector.onTouchEvent(event)){
					return false;
				}else{
					return true;
				}
				
			}
		});
		
		
	}
	
	private void setPos()
	{
		
		prevposX = posX - 1;
		nextposX = posX + 1;
		
		if (posX > MAX){
			posX = 0;
			nextposX = 1;
		}
		if (posX < 0){
			posX = MAX;
			prevposX = MAX -1;
		}
		if (posX == 0){
			prevposX = MAX;
			
		}
		if (posX == MAX)
			nextposX = 0;
		
		
	}

	
	private class MyGestureDetector extends SimpleOnGestureListener{
		private static final int SWIPE_MIN_DISTANCE = 140;
		private static final int SWIPE_MAX_OFF_PATH = 100;
		private static final int SWIPE_THRESHOLD_VELOCITY = 150;
		
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
			System.out.println(" in onfling :: ");
		
		//only updown gesture on verb
			if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
				//up
				 System.out.println(" in onfling up :: ");
				 mViewFlipperUpDown.setInAnimation(mInFromBottom);
	                mViewFlipperUpDown.setOutAnimation(mOutToTop);
	                
	                //if verb dansk move to english, if englsih move to datid nutil...
	                
	           
	                mViewFlipperUpDown.showNext();
			}
			else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
				//down
				 System.out.println(" in onfling down :: ");
				 mViewFlipperUpDown.setInAnimation(mInFromTop);
	                mViewFlipperUpDown.setOutAnimation(mOutToBottom);
	                //if datid, move to verb english show danish
	             
	                mViewFlipperUpDown.showPrevious();
	               // mViewFlipper.
			}
			//left right on word removal or need to learn again
			else if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	//left
				
				posX++;
				setPos();
				
				
				setText();
            	
				
				mViewFlipper.setInAnimation(mInFromRight);
                mViewFlipper.setOutAnimation(mOutToLeft);
                mViewFlipper.showNext();
                
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //right
            	
            	posX--;
            	setPos();
            
            	setText();
            	
            	mViewFlipper.setInAnimation(mInFromLeft);
                mViewFlipper.setOutAnimation(mOutToRight);
                mViewFlipper.showPrevious();
                
            }
            
            return false;//super.onFling(e1, e2, velocityX, velocityY);
		
		}
		
	}
	
	
}
