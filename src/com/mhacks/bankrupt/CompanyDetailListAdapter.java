package com.mhacks.bankrupt;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CompanyDetailListAdapter extends BaseAdapter {
    private HashMap<String, String> details;
    private Activity activity;
    
    public CompanyDetailListAdapter(Activity ac) {
        details = new HashMap<>();
        activity = ac;
    }
    
    public void put(String key, String val) {
        details.put(key, val);
    }
    
    public String get(String key) {
        return details.get(key);
    }
    
    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int position) {
        ArrayList<String> keys = new ArrayList<String>(details.keySet());
        return details.get(keys.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        String key, value;
        ArrayList<String> keySet = new ArrayList<String>(details.keySet());
        key = keySet.get(position);
        value = details.get(key);

        if (key == null) return convertView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_company_detail_row, null);
            holder = new ViewHolder();
            holder.key = (TextView) convertView.findViewById(R.id.key);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.key.setText(key);
        holder.value.setText(value);

        return convertView;
    }

    private class ViewHolder {
        TextView key, value;
    }
}
