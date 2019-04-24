package com.capsa.prayer.time;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;

import com.capsa.prayer.activtites.MainActivity;
import com.capsa.prayer.time.R;

public class SplashActivity extends Activity {
	private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// Toast.makeText(getApplicationContext(), "Application is running ",
		// Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent myintent = new Intent(SplashActivity.this,
						MainActivity.class);

				startActivity(myintent);

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
