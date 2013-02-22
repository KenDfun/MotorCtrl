package com.design_fun.motorctrl;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.ToggleButton;


public class MainActivity extends Activity{
	private TextView mTview;
	private ScrollView mScroll;
	private ToggleButton mTglLed1;
	private ToggleButton mTglLed2;
	private ToggleButton mTglLed3;
	private ToggleButton mTglLed4;
	
	
	//btSp = new this.ButtonSp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mScroll = (ScrollView)findViewById(R.id.scrollView1);
		mTview = (TextView)findViewById(R.id.textView1);

		
		mTglLed1 = (ToggleButton) findViewById(R.id.toggleButton1);  
//		mTglLed1.setOnCheckedChangeListener((OnCheckedChangeListener) this);
		mTglLed2 = (ToggleButton) findViewById(R.id.toggleButton2);  
		mTglLed3 = (ToggleButton) findViewById(R.id.toggleButton3);  
		mTglLed4 = (ToggleButton) findViewById(R.id.toggleButton4);  

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	

	public void onClickSpeed(View v)
	{
		switch(v.getId()){
		case R.id.button1:
			mTview.append("Push UP!\n> ");
			mScroll.post(new ScrollDown());
			break;
			
		case R.id.button2:
			mTview.append("Push Down!\n> ");
			mScroll.post(new ScrollDown());
			break;
		}
		
	}


	 public void onClickTglBtn(View v){
		 String str;
		 
		 switch(v.getId()){
		 case R.id.toggleButton1:
			str = "LED1 ";
			if(mTglLed1.isChecked()){
				str += "ON\n";
			}
			else{
				str += "OFF\n";
			}
			break;
		 case R.id.toggleButton2:
			str = "LED2 ";
			if(mTglLed2.isChecked()){
				str += "ON\n";
			}
			else{
				str += "OFF\n";
			}
			break;
		 case R.id.toggleButton3:
			str = "LED3 ";
			if(mTglLed3.isChecked()){
				str += "ON\n";
			}
			else{
				str += "OFF\n";
			}
			break;
		 case R.id.toggleButton4:
			str = "LED4 ";
			if(mTglLed4.isChecked()){
				str += "ON\n";
			}
			else{
				str += "OFF\n";
			}
			break;
		default:
			str = "none";
		} 		
		
		mTview.append(str+"> ");
		mScroll.post(new ScrollDown());
	 }

	/* scroll down thread */
	private class ScrollDown implements Runnable { // (8)
        public void run() {
        	mScroll.fullScroll(View.FOCUS_DOWN); // (9)
        }
    }	
	
}

