package com.capsa.prayer.activtites;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.capsa.prayer.adaptors.CaleAdapter;
import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.model.CaleModel;
import com.capsa.prayer.model.PrayerTime;
import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;
import com.capsa.prayer.utills.PrayTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RamadanCalendar extends AppCompatActivity {

    Context context;
    ListView lvCale;
    ArrayList<PrayerTime> prayerTimesList;
    AppManager appManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initMain();
    }

    private void initMain() {
        context = RamadanCalendar.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
        getSupportActionBar().setTitle("Ramadan Calendar");

        lvCale = (ListView) findViewById(R.id.lvCale);

        getPrayerTimes();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
    }

    private void getPrayerTimes() {

        appManager = new AppManager(getApplicationContext());
        UserLocation userLocation = appManager.getSelectedLocation();

        ArrayList<CaleModel> cModel = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = 16;

        for (int i = 1; i < 31; i++) {
            GregorianCalendar gc = new GregorianCalendar(2018, 4, 16);
            gc.add(Calendar.DATE, i);

            prayerTimesList = new PrayTime(getApplicationContext())
                    .getPrayerTimes(userLocation, appManager, gc);

            PrayerTime sehri = prayerTimesList.get(0);
            PrayerTime iftari = prayerTimesList.get(4);

            calendar.set(year, month, day + i);
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
            String strDate = format.format(calendar.getTime());

            cModel.add(new CaleModel(strDate, sehri.getPrayerTime(), iftari.getPrayerTime()));
        }
        lvCale.setAdapter(new CaleAdapter(getApplicationContext(), cModel));
    }

}
