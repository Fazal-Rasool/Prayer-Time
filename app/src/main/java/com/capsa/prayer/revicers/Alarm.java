package com.capsa.prayer.revicers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.capsa.prayer.activtites.AzanActivity;

public class Alarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent2 = new Intent(context, AzanActivity.class);
		intent2.putExtra("alarm", "alarm");
		intent2.putExtra("id", intent.getIntExtra("id", -1));
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2);
	}

}
