package com.design_fun.motorctrl;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ScrollView;

public class MainActivity extends Activity implements OnClickListener{
	private TextView mTview;
	private Button buttonUp;
	private Button buttonDown;
	private ScrollView mScroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mScroll = (ScrollView)findViewById(R.id.scrollView1);
		
		mTview = (TextView)findViewById(R.id.textView1);

		
		buttonUp = (Button)findViewById(R.id.button1);
		buttonUp.setOnClickListener(this);
		buttonDown = (Button)findViewById(R.id.button2);
		buttonDown.setOnClickListener(this);
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void onClick(View v)
	{
		if(v==buttonUp){
			mTview.append("Push UP!\n> ");
			mScroll.post(new ScrollDown());
		}
		else if(v==buttonDown){
			mTview.append("Push Down!\n> ");
			mScroll.post(new ScrollDown());
		}
		
	}


	/* scroll down thread */
	private class ScrollDown implements Runnable { // (8)
        public void run() {
        	mScroll.fullScroll(View.FOCUS_DOWN); // (9)
        }
    }	
	
}

