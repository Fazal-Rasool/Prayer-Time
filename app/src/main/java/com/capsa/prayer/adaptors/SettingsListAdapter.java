package com.capsa.prayer.adaptors;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.capsa.prayer.managers.AppManager;
import com.capsa.prayer.time.R;
import com.capsa.prayer.utills.AppConstants;

public class SettingsListAdapter extends ArrayAdapter<String> {

	private Context context;

	public String[] objects;

	public int mCurrentPrayerIndex;

	private AppManager appManager;

	private int listType;

	public SettingsListAdapter(Context context, int resource, String[] arrMethods, AppManager appManager, int listType) {
		super(context, resource, arrMethods);

		this.context = context;
		this.objects = arrMethods;
		this.appManager = appManager;
		this.listType = listType;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.settings_list_row_layout, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);

			convertView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.titleTextView.setText(objects[position]);

		if (listType == AppConstants.LIST_TYPE_JURISTICS) {
			if (appManager.getJuristic() == position) {
				holder.checkbox.setChecked(true);
			} else {
				holder.checkbox.setChecked(false);
			}

		} else if (listType == AppConstants.LIST_TYPE_CALC_METHOD) {
			if (appManager.getCalcMethod() == position) {
				holder.checkbox.setChecked(true);
			} else {
				holder.checkbox.setChecked(false);
			}
		}else if (listType == AppConstants.LIST_TYPE_LANGUAGE) {
			if (appManager.getSelectedLanguage() == position) {
				holder.checkbox.setChecked(true);
			} else {
				holder.checkbox.setChecked(false);
			}
		}

		return convertView;
	}

	static class ViewHolder {
		public TextView titleTextView;
		public CheckBox checkbox;
	}
}
