package com.example.homenursingservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    int flag;
    ArrayList<Object> info;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext,int flag,ArrayList<Object> info) {
        this.context = applicationContext;
        this.flag = flag;
        this.info = info;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return info.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.teachers_list_item, null);
        CircleImageView circleImageView=view.findViewById(R.id.image);
        final TextView names = (TextView) view.findViewById(R.id.teacher_name);
        String s=(String)info.get(i);
        circleImageView.setVisibility(View.GONE);
        names.setText(s);
        return view;
    }
}