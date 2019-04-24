package com.capsa.prayer.activtites;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
/*import com.google.android.gms.ads.InterstitialAd;*/
import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.model.PrayerTime;
import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;
import com.capsa.prayer.utills.AppUtils;
import com.capsa.prayer.utills.CallBack;
import com.capsa.prayer.utills.PrayTime;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AzanActivity extends AppCompatActivity implements OnClickListener, CallBack {

	AppManager appManager;
	MediaPlayer mediaPlayer;

	private ImageView playButton;

	private NotificationManager mNotificationManager;
	private Notification notification;
	private final int NOTIFICATION_ID = 35548614;

	private TextView prayerNameText;
	private TextView locationText;
	private TextView nextPrayerText;

	private Timer updateTimer;

	private ArrayList<PrayerTime> prayerTimesList;

	private boolean isFajrTime;

	AdRequest adRequest;
	private AdView adView;
	private InterstitialAd interstitialgoogle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_azan);
 
        // Enabling Up / Back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
		getSupportActionBar().setTitle("Azan");
		appManager = new AppManager(AzanActivity.this);

		initMain();

		Intent intent = getIntent();
		if (intent.hasExtra("alarm")) {
			playButton.setSelected(true);

			if (intent.getIntExtra("id", -1) == 0) {
				isFajrTime = true;
			}

			playAdhan();
		}
		MobileAds.initialize(this, "ca-app-pub-2530669520505137~3330636005");

		adView = new AdView(this);
		adView = (AdView) this.findViewById(R.id.adlayout);

		adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		interstitialgoogle = new InterstitialAd(this);
		interstitialgoogle
				.setAdUnitId("ca-app-pub-2530669520505137/9237568801");
		interstitialgoogle.loadAd(adRequest);
		prayerNameText = (TextView) findViewById(R.id.prayerNameText);
		locationText = (TextView) findViewById(R.id.locationText);
	//	nextPrayerText = (TextView) findViewById(R.id.nextPrayerText);
	}

	@Override
	protected void onResume() {
		super.onResume();

		int selectedLanguageIndex = appManager.getSelectedLanguage();
		AppUtils.updateLocale(this, selectedLanguageIndex);

		startTimer();

	}

	private void startTimer() {

		UserLocation userLocation = appManager.getSelectedLocation();

		locationText.setText(userLocation.getCityName() + ", "
				+ userLocation.getCountryName());

		prayerTimesList = new PrayTime(this).getPrayerTimes(userLocation,
				appManager);

		updateTimer = new Timer();
		updateTimer.schedule(new TimerTask() {
			public void run() {
				AzanActivity.this.notify("", "UPDATE_VIEWS");
			}
		}, 0, 60000);

	}

	@Override
	protected void onPause() {
		super.onPause();

		adView.pause();

		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer.purge();

			updateTimer = null;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	private void initMain() {
		playButton = (ImageView) findViewById(R.id.play_pause_button);
		playButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.location:

			intent = new Intent(AzanActivity.this, SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.play_pause_button:

			if(interstitialgoogle.isLoaded())
				interstitialgoogle.show();
			else
				interstitialgoogle.loadAd(adRequest);

			playButton.setSelected(!playButton.isSelected());
		
			if (playButton.isSelected()) {
				playAdhan();

			} else {
				removeNotification();
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					playButton.setSelected(false);
				}
			}

			break;
		case R.id.qibla:

			intent = new Intent(AzanActivity.this, QiblaActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	void playAdhan() {
		if (mediaPlayer == null) {
			if (isFajrTime) {
				mediaPlayer = MediaPlayer.create(AzanActivity.this,
						R.raw.madina_fajr);
			} else {
				mediaPlayer = MediaPlayer.create(AzanActivity.this,
						R.raw.adhan_makkah);
			}
		}
		createNotification();

		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				playButton.setSelected(false);
				removeNotification();
			}
		});

	}

	private void stopAdhan() {
		Log.d("Stopping", "media player");

		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		removeNotification();

		// onHomeButtonClicked(null);
	}

	@Override
	protected void onDestroy() {
		Log.d("Stopping", "media player");

		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		removeNotification();

		super.onDestroy();

	}

	public void onHomeButtonClicked(View view) {
		stopAdhan();

		Intent intent = new Intent(AzanActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void onFacebookMenuClicked(View view) {
		AppUtils.startFacebookApp(getApplicationContext());
	}

	public void onRemoveAdsMenuClicked(View view) {

	}

	public void onRateUsMenuClicked(View view) {
		AppUtils.showAppInGooglePlay(getApplicationContext(), getApplication()
				.getPackageName());
	}

	/************************************** Status Bar Notifications Methods **************************************/

	private void createNotification() {

		Intent actionIntent = new Intent(getApplicationContext(),
				AzanActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, actionIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		RemoteViews mNotificationView = new RemoteViews(getPackageName(),
				R.layout.notification_layout);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					getApplicationContext());
			builder.setSmallIcon(R.drawable.ic_launcher);
			builder.setContent(mNotificationView);
			builder.setOngoing(false);
			builder.setTicker(getString(R.string.prayer_time_now));
			builder.setContentIntent(pendingIntent);
			builder.setAutoCancel(true);

			notification = builder.build();

		} else {

			Notification statusNotification = new Notification();
			statusNotification.contentView = mNotificationView;
			statusNotification.flags |= Notification.FLAG_AUTO_CANCEL;
			statusNotification.icon = R.drawable.ic_launcher;
			statusNotification.contentIntent = PendingIntent
					.getActivity(this, 0, new Intent(getApplicationContext(),
							AzanActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT), 0);

			notification = statusNotification;
		}

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	private void removeNotification() {
		if (mNotificationManager != null) {
			if (notification != null) {
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotificationManager.notify(NOTIFICATION_ID, notification);
			}

			mNotificationManager.cancel(NOTIFICATION_ID);
			mNotificationManager.cancelAll();
			mNotificationManager = null;
		}

	}

	@Override
	public void notify(Object object, String type) {
		if (type.equals("UPDATE_VIEWS")) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateViews();
				}
			});
		}

	}

	@SuppressLint("SimpleDateFormat")
	private void updateViews() {
		calculateRemainingTime();
	}

	private int getCurrentPrayerIndex() {

		DateTime dateNow = new DateTime();
		DateTime date1;
		DateTime date2;

		PrayerTime firstPrayer;
		PrayerTime secondPrayer;

		for (int i = 0; i < prayerTimesList.size(); i++) {
			firstPrayer = prayerTimesList.get(i);

			if (i == 0) {
				secondPrayer = prayerTimesList.get(5);
			} else if (i == 5) {
				secondPrayer = prayerTimesList.get(0);
			} else {
				secondPrayer = prayerTimesList.get(i - 1);
			}

			String[] first = firstPrayer.getPrayerTime().replaceAll(" am", "")
					.replaceAll(" pm", "").split(":");
			String[] second = secondPrayer.getPrayerTime()
					.replaceAll(" am", "").replaceAll(" pm", "").split(":");

			date1 = new DateTime(dateNow.getYear(), dateNow.getMonthOfYear(),
					dateNow.getDayOfMonth(),
					getHourOfDay(first[0], firstPrayer),
					Integer.parseInt(first[1]));
			date2 = new DateTime(dateNow.getYear(), dateNow.getMonthOfYear(),
					dateNow.getDayOfMonth(), getHourOfDay(second[0],
							secondPrayer), Integer.parseInt(second[1]));

			date1.compareTo(date2);

			if ((dateNow.isEqual(date1) || dateNow.isBefore(date1))
					&& dateNow.isAfter(date2)) {
				return i;

			} else if (i == 0
					&& (dateNow.isEqual(date1) || dateNow.isBefore(date1) || dateNow
							.isAfter(date2))) {
				return i;

			}
		}
		return 0;
	}

	private int getHourOfDay(String hour, PrayerTime prayer) {
		int hourOfDay = Integer.parseInt(hour);

		if (prayer.getPrayerTime().contains("pm")) {
			if (hourOfDay != 12) {
				hourOfDay = hourOfDay + 12;
			}
		} else if (prayer.getPrayerTime().contains("am")) {
			if (hourOfDay == 12) {
				hourOfDay = 0;
			}
		}

		return hourOfDay;
	}

	private void calculateRemainingTime() {

		int currentPrayerIndex = getCurrentPrayerIndex();

		if (currentPrayerIndex == 0) {
			isFajrTime = true;
		}

		PrayerTime prayer = prayerTimesList.get(currentPrayerIndex);

		DateTime dateNow = new DateTime();
		String[] first = prayer.getPrayerTime().replaceAll(" am", "")
				.replaceAll(" pm", "").split(":");

		DateTime prayerTime = new DateTime(dateNow.getYear(),
				dateNow.getMonthOfYear(), dateNow.getDayOfMonth(),
				getHourOfDay(first[0], prayer), Integer.parseInt(first[1]));

		Period period = new Period(dateNow, prayerTime);
		PeriodFormatter HHMMSSFormater = new PeriodFormatterBuilder()
				.printZeroAlways().minimumPrintedDigits(2).appendHours()
				.appendSeparator("-").appendMinutes().appendSeparator("-")
				.appendSeconds().toFormatter(); // produce
		AppUtils.printLog("AzanActivity", HHMMSSFormater.print(period));

		String[] arrTime = HHMMSSFormater.print(period).split("-");

		int hours = 0;
		int minutes = 0;

		if (!TextUtils.isEmpty(arrTime[0])) {
			hours = Integer.parseInt(arrTime[0]);
		}

		if (!TextUtils.isEmpty(arrTime[1])) {
			minutes = Integer.parseInt(arrTime[1]);
		}

		String remainingTime = "";
		if (hours != 0) {
			remainingTime = hours + "  " + getString(R.string.hours) + " "
					+ minutes + " " + getString(R.string.minutes);
		} else {
			remainingTime = minutes + " " + getString(R.string.minutes);
		}

		prayerNameText.setText(prayer.getPrayerName() + " "
				+ getString(R.string.at) + " " + prayer.getPrayerTime());

		/*if (minutes != 0) {
			nextPrayerText.setText(prayer.getPrayerName() + " "
					+ getString(R.string.after) + " " + remainingTime);
		} else {
			nextPrayerText.setText(getString(R.string.its) + " "
					+ prayer.getPrayerName() + " " + getString(R.string.time));
		}*/

	}

}
