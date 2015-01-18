package com.mhacks.bankrupt;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CompanyListAdapter extends BaseAdapter {
    private ArrayList<Company> data;
    private Activity activity;

    public CompanyListAdapter(Activity ac) {
        super();
        data = new ArrayList<>();

        activity = ac;
    }

    public void add(Company co) {
        data.add(co);
    }

    public Company get(int i) {
        return data.get(i);
    }

    public void set(int i, Company co) {
        data.set(i, co);
    }

    public Company remove(int i) {
        return data.remove(i);
    }

    public boolean remove(Company co) {
        return data.remove(co);
    }

    public ArrayList<Company> getCompanyList() {
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        Company co = data.get(position);

        if (co == null) return convertView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_company_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.company_name);
//            holder.date = (TextView) convertView.findViewById(R.id.company_date);
            holder.descr = (TextView) convertView.findViewById(R.id.company_description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(co.getName());
//        holder.date.setText(co.getDate().toString());
        holder.descr.setText(co.getDescription());


        return convertView;
    }

    private class ViewHolder {
        TextView name, date, descr;
    }
}
