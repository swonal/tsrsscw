package com.tsrsscw.feed;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class Welcome extends Activity {
	ImageView iv;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_page);
		
		new Timer().schedule(new TimerTask(){

			@Override
			public void run() {
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}		
		},2500);
		
		
	}

}
