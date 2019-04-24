package com.capsa.prayer.activtites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.capsa.prayer.adaptors.QalmaListAdapter;
import com.capsa.prayer.time.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class QalmaActivity extends AppCompatActivity {

	String[] res;
	Context context;
	ListView lvQalme;

    AdRequest adRequest;
    private InterstitialAd interstitialgoogle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qalmay);

		initMain();
	}

	private void initMain() {

		context = QalmaActivity.this;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
		getSupportActionBar().setTitle("Kalimas");


		MobileAds.initialize(this, "ca-app-pub-2530669520505137~3330636005");

		adRequest = new AdRequest.Builder().build();
        interstitialgoogle = new InterstitialAd(this);
        interstitialgoogle
                .setAdUnitId("ca-app-pub-2530669520505137/7760835605");
        interstitialgoogle.loadAd(adRequest);

		/*String[] res = getResources().getStringArray(R.array.qalme_list);*/

		int resId = getResources().getIdentifier("qalma_name_list", "array", getPackageName());
		res = getResources().getStringArray(resId);

		lvQalme = (ListView) findViewById(R.id.lvQalme);
		lvQalme.setAdapter(new QalmaListAdapter(context, res));

		lvQalme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(interstitialgoogle.isLoaded())
                    interstitialgoogle.show();
                else
                    interstitialgoogle.loadAd(adRequest);

				String[] reso = getResources().getStringArray(R.array.qalma_list);
				String[] resoTrans = getResources().getStringArray(R.array.qalma_list_trans);

				Intent i = new Intent(context, Qalmas.class);
				i.putExtra("qalma_count", res[position]);
				i.putExtra("qalma", reso[position]);
				i.putExtra("qalmaTrans", resoTrans[position]);
				i.putExtra("qalmaNum", position);
				startActivity(i);
			}
		});
	}
}
