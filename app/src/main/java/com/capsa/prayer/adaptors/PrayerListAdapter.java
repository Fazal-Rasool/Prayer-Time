package com.capsa.prayer.adaptors;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capsa.prayer.activtites.FragmnetMainActivity;
import com.capsa.prayer.activtites.MainActivity;
import com.capsa.prayer.model.PrayerTime;
import com.capsa.prayer.time.R;

public class PrayerListAdapter extends ArrayAdapter<PrayerTime> {

    private Context context;

    public ArrayList<PrayerTime> objects;

    private Integer[] iconImages = new Integer[] {
            R.drawable.fajar_icon_selector, R.drawable.shrooq_icon_selector,
            R.drawable.zuhur_icon_selector, R.drawable.asar_icon_selector,
            R.drawable.maghrib_icon_selector, R.drawable.isha_icon_selector };

    public int mCurrentPrayerIndex;

    public PrayerListAdapter(Context context, int resource,
                             ArrayList<PrayerTime> objects) {
        super(context, resource, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.prayer_time_list_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.prayerNameTextView = (TextView) convertView
                    .findViewById(R.id.prayerNameTextView);
            viewHolder.prayerTimeTextView = (TextView) convertView
                    .findViewById(R.id.prayerTimeTextView);
            viewHolder.prayerIcon = (ImageView) convertView
                    .findViewById(R.id.prayerIcon);
            viewHolder.speakerIcon = (ImageView) convertView
                    .findViewById(R.id.speakerIcon);

            viewHolder.cards = (LinearLayout) convertView
                    .findViewById(R.id.fajarLayout);
            convertView.setTag(viewHolder);
        }



        PrayerTime prayer = objects.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
//        Toast.makeText(context, "Name :"+prayer.getPrayerName(), Toast.LENGTH_SHORT).show();
        if(prayer.getPrayerName().contains("Shorook")){
            holder.cards.setBackgroundResource(R.drawable.prayer_yellow);
        }

        holder.prayerNameTextView.setText(prayer.getPrayerName());
        holder.prayerTimeTextView.setText(prayer.getPrayerTime());
        holder.prayerIcon.setImageResource(iconImages[position]);


        if (position == mCurrentPrayerIndex) {
            holder.prayerNameTextView.setTextColor(context.getResources()
                    .getColor(R.color.black));
            holder.prayerTimeTextView.setTextColor(context.getResources()
                    .getColor(R.color.black));
            //	holder.prayerIcon.setSelected(true);
            //	holder.speakerIcon.setEnabled(true);

			/*if (prayer.isAlarmSet()) {
				holder.speakerIcon.setSelected(true);
			} else {
				holder.speakerIcon.setSelected(false);
			}*/

        } else {
            holder.prayerNameTextView.setTextColor(context.getResources()
                    .getColor(R.color.black));
            holder.prayerTimeTextView.setTextColor(context.getResources()
                    .getColor(R.color.black));
            holder.prayerIcon.setSelected(false);

            holder.speakerIcon.setEnabled(false);
            if (prayer.isAlarmSet()) {
                holder.speakerIcon.setSelected(true);
            } else {
                holder.speakerIcon.setSelected(false);
            }

        }

        return convertView;
    }

    static class ViewHolder {
        public TextView prayerNameTextView, prayerTimeTextView;
        public ImageView prayerIcon, speakerIcon;
        public LinearLayout cards;
    }
}
