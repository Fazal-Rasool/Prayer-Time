package com.capsa.prayer.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.capsa.prayer.time.R;

public class CountryAdapter extends ArrayAdapter<String> {

	protected static final String TAG = null;
	Context mContext;

	String[] names_array;

	public CountryAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);

		mContext = context;
		names_array = objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();

			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_country, null);

			holder.name = (TextView) convertView
					.findViewById(R.id.textView_countryitem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(names_array[position].replace(".txt", ""));
		return convertView;
	}

	public class ViewHolder {
		TextView name;

	}

}
