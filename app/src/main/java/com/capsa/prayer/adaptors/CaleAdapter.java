package com.capsa.prayer.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.capsa.prayer.model.CaleModel;
import com.capsa.prayer.time.R;

import java.util.ArrayList;

public class CaleAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CaleModel> cModel = new ArrayList<>();
    private static LayoutInflater inflater = null;

    public CaleAdapter(Context mContext, ArrayList<CaleModel> mCaleModel){
        context = mContext;

        cModel = mCaleModel;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cModel.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder {
        TextView tvDate, tvSehri, tvIftaar;
    }

    public void add(CaleModel newCmodel){
        Log.v("AddView", "new entry");
        this.cModel.add(newCmodel);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;

        View rowView = convertView;

        if (rowView == null) {
            rowView = inflater.inflate(R.layout.li_cale, null);

            holder = new Holder();

            holder.tvDate = (TextView) rowView.findViewById(R.id.liDate);
            holder.tvSehri = (TextView) rowView.findViewById(R.id.liSehri);
            holder.tvIftaar = (TextView) rowView.findViewById(R.id.liIftaari);


            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        final CaleModel tempModel = cModel.get(position);

        holder.tvDate.setText(tempModel.getDate());
        holder.tvSehri.setText(tempModel.getSehriTime());
        holder.tvIftaar.setText(tempModel.getIftaarTime());

        return rowView;
    }
}
