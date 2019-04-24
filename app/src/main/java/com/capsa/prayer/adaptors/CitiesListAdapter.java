package com.capsa.prayer.adaptors;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.capsa.prayer.model.UserLocation;
import com.capsa.prayer.time.R;

public class CitiesListAdapter extends ArrayAdapter<UserLocation>  {

	protected static final String TAG = null;
	Context mContext;
	
	ArrayList<UserLocation> names_array;

	public CitiesListAdapter(Context context, int resource, ArrayList<UserLocation> locationsList) {
		super(context, resource, locationsList);

		mContext = context;
		names_array = locationsList;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_country, null);

			holder.name = (TextView) convertView.findViewById(R.id.textView_countryitem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(names_array.get(position).getCityName().replace(".txt", ""));
		return convertView;
	}

	public class ViewHolder {
		TextView name;

	}

}
