package com.capsa.prayer.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.capsa.prayer.time.R;

public class QalmaListAdapter extends BaseAdapter {

    Context mContext;
    String[] qalma_array;

    public QalmaListAdapter(Context context, String[] qalmas) {
        mContext = context;
        qalma_array = qalmas;
    }

    @Override
    public int getCount() {
        return qalma_array.length;
    }

    @Override
    public Object getItem(int position) {
        return qalma_array[position];
    }

    @Override
    public long getItemId(int position) {
        return qalma_array.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.li_qalma, null);

            holder.tvQalma = (TextView) convertView.findViewById(R.id.tvQalma);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvQalma.setText(qalma_array[position]);
        return convertView;
    }

    public class ViewHolder {
        TextView tvQalma;

    }

}
