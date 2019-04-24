package com.capsa.prayer.activtites;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.model.PrayerTime;
import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;
import com.capsa.prayer.utills.AppUtils;
import com.capsa.prayer.utills.CallBack;
import com.capsa.prayer.utills.PrayTime;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TodayStatus extends AppCompatActivity implements View.OnClickListener,
        CallBack {

    Context context;
    ArrayList<PrayerTime> prayerTimesList;
    AppManager appManager;
    TextView tvSehriTime, tvIftaarTime, tvDateFM, currentPrayerTime, tvHijriDate, tvCityCountry;
    String[] islamicMonths;

    AdRequest adRequest;
    private AdView adView;
    private InterstitialAd interstitialgoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_new);

        appManager = new AppManager(getApplicationContext());

        if (appManager.isCountryDialogRun() && appManager.isCityDialogRun()) {
            initMain();
        }
        if (!appManager.isCountryDialogRun()) {
            AppUtils.showCountryDialog(TodayStatus.this, appManager, this, false);
            initMain();
        } else if (!appManager.isCityDialogRun()) {
            initMain();
            AppUtils.showCityDilog(TodayStatus.this, this, false, false);
        }

        adView = new AdView(this);
        adView = (AdView) this.findViewById(R.id.adlayout);

        MobileAds.initialize(this, "ca-app-pub-2530669520505137~3330636005");


        adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        interstitialgoogle = new InterstitialAd(this);
        interstitialgoogle
                .setAdUnitId("ca-app-pub-2530669520505137/3635940647");
        interstitialgoogle.loadAd(adRequest);
    }

    private void initMain() {
        context = TodayStatus.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
        getSupportActionBar().setTitle("Today\'s Status");

        appManager = new AppManager(getApplicationContext());

        islamicMonths = getResources().getStringArray(R.array.islamic_months);

        tvSehriTime = (TextView) findViewById(R.id.tvSehriTime);
        tvIftaarTime = (TextView) findViewById(R.id.tvIftaarTime);
        tvDateFM = (TextView) findViewById(R.id.tvDateFM);
        currentPrayerTime = (TextView) findViewById(R.id.currentPrayerTime);
        tvHijriDate = (TextView) findViewById(R.id.tvHijriDate);
        tvCityCountry = (TextView) findViewById(R.id.tvCityCountry);
        updateViews();
    }

    private void updateViews() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.set(year, month, day);

        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String strDate = format.format(calendar.getTime());

        tvDateFM.setText(strDate);

        DateTime date = new DateTime(IslamicChronology.getInstance());

        tvHijriDate.setText(islamicMonths[date.getMonthOfYear() - 1]
                + " " + date.getDayOfMonth() + ", "
                + date.getYear());

        tvCityCountry.setText(appManager.getSelectedLocation().getCityName() + ", " + appManager.getSelectedCountry());

        UserLocation userLocation = appManager.getSelectedLocation();
        prayerTimesList = new PrayTime(getApplicationContext())
                .getPrayerTimes(userLocation, appManager);

        getCurrentPrayerIndex();
        PrayerTime sehri = prayerTimesList.get(0);
        tvSehriTime.setText(sehri.getPrayerTime());

        PrayerTime iftari = prayerTimesList.get(4);
        tvIftaarTime.setText(iftari.getPrayerTime());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void notify(Object s, String type) {}

    private void getCurrentPrayerIndex() {
        DateTime dateNow = new DateTime();
        DateTime date1;
        DateTime date2;

        PrayerTime firstPrayer;
        PrayerTime secondPrayer;

        try {
            for (int i = 0; i < prayerTimesList.size(); i++) {
                firstPrayer = prayerTimesList.get(i);

                if (i == 0) {
                    secondPrayer = prayerTimesList.get(5);
                } else if (i == 5) {
                    secondPrayer = prayerTimesList.get(0);
                } else {
                    secondPrayer = prayerTimesList.get(i - 1);
                }

                String[] first = firstPrayer.getPrayerTime()
                        .replaceAll(" am", "").replaceAll(" pm", "").split(":");
                String[] second = secondPrayer.getPrayerTime()
                        .replaceAll(" am", "").replaceAll(" pm", "").split(":");

                date1 = new DateTime(dateNow.getYear(),
                        dateNow.getMonthOfYear(), dateNow.getDayOfMonth(),
                        getHourOfDay(first[0], firstPrayer),
                        Integer.parseInt(first[1]));
                date2 = new DateTime(dateNow.getYear(),
                        dateNow.getMonthOfYear(), dateNow.getDayOfMonth(),
                        getHourOfDay(second[0], secondPrayer),
                        Integer.parseInt(second[1]));

                date1.compareTo(date2);

                if ((dateNow.isEqual(date1) || dateNow.isBefore(date1))
                        && dateNow.isAfter(date2)) {

                    PrayerTime currentPrayerId = prayerTimesList.get(i);
                    currentPrayerTime.setText(currentPrayerId.getPrayerName() + " " + currentPrayerId.getPrayerTime());

                    return;

                } else if (i == 0
                        && (dateNow.isEqual(date1) || dateNow.isBefore(date1) || dateNow
                        .isAfter(date2))) {

                    PrayerTime currentPrayerId = prayerTimesList.get(i);
                    currentPrayerTime.setText(currentPrayerId.getPrayerName() + " " + currentPrayerId.getPrayerTime());

                    return;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getHourOfDay(String hour, PrayerTime prayer) {

        int hourOfDay = 0;
        try {
            hourOfDay = Integer.parseInt(hour);

            if (prayer.getPrayerTime().contains("pm")) {
                if (hourOfDay != 12) {
                    hourOfDay = hourOfDay + 12;
                }
            } else if (prayer.getPrayerTime().contains("am")) {
                if (hourOfDay == 12) {
                    hourOfDay = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hourOfDay;

    }
}
