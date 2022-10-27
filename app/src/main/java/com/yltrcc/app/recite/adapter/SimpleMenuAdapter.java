package com.yltrcc.app.recite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yltrcc.app.recite.R;

import java.util.List;

/**
 * Created by fengshawn on 2017/8/3.
 */

public class SimpleMenuAdapter<T> extends ArrayAdapter {

    private Context context;
    private List<T> list;
    private int resource;

    public SimpleMenuAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(resource, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.tv.setText((String) list.get(position));
        //v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //Log.i("tag", v + " height: " + v.getMeasuredHeight());
        return v;
    }

    class ViewHolder {
        TextView tv;

        public ViewHolder(View v) {
            tv = (TextView) v.findViewById(R.id.item_menu_tv);
        }
    }
}
